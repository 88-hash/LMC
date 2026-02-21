<template>
  <div class="cart-page">
    <header class="cart-header">
      <h1 class="title">购物车</h1>
      <p class="sub-info">
        <span>已选 {{ selectedUnits }} 件</span>
        <span class="dot">·</span>
        <span>合计 ¥{{ totalAmount }}</span>
      </p>
    </header>

    <section class="cart-content">
      <div v-if="loading" class="skeleton-list">
        <div v-for="n in 4" :key="'skeleton-' + n" class="skeleton-row">
          <div class="skeleton-check"></div>
          <div class="skeleton-thumb"></div>
          <div class="skeleton-main">
            <div class="skeleton-line w70"></div>
            <div class="skeleton-line w36"></div>
            <div class="skeleton-line w88"></div>
          </div>
        </div>
      </div>

      <div v-else-if="items.length === 0" class="empty-wrap">
        <el-empty description="购物车还是空的，先去挑点零食吧" />
        <el-button class="browse-btn" type="primary" @click="goBrowse">去逛逛</el-button>
      </div>

      <div v-else class="cart-list">
        <CartItemRow
          v-for="item in items"
          :key="item.id"
          :item="item"
          :saving="isItemSaving(item.id)"
          :error-message="itemErrorMap[item.id] || ''"
          :max-quantity="getMaxQuantity(item)"
          @toggle-selected="(checked) => (item.selected = checked)"
          @change-quantity="(nextQty) => handleQuantityChange(item, nextQty)"
          @remove="() => removeItem(item)"
        />
      </div>

      <div v-if="pageError" class="page-error">
        <span>{{ pageError }}</span>
        <button type="button" @click="loadCart">重试</button>
      </div>
    </section>

    <footer v-if="items.length > 0" class="checkout-bar">
      <div class="left">
        <el-checkbox v-model="isAllSelected" @change="toggleAll">全选</el-checkbox>
      </div>

      <div class="middle">
        <p class="total">¥{{ totalAmount }}</p>
        <p class="hint">不含运费/优惠</p>
      </div>

      <el-button type="primary" class="checkout-btn" @click="checkout">去结算</el-button>
    </footer>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import { useCartStore } from '../../stores/cart'
import CartItemRow from '../../components/client/cart/CartItemRow.vue'

const router = useRouter()
const cartStore = useCartStore()

const items = ref([])
const loading = ref(true)
const pageError = ref('')

const itemErrorMap = reactive({})
const inflightById = reactive({})
const queuedQtyById = reactive({})
const removingById = reactive({})
const debounceTimers = new Map()

const DEFAULT_MAX_QTY = 99

const selectedItems = computed(() => items.value.filter(item => item.selected))

const selectedUnits = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
})

const totalAmount = computed(() => {
  return selectedItems.value
    .reduce((sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0), 0)
    .toFixed(2)
})

const isAllSelected = computed({
  get: () => items.value.length > 0 && items.value.every(item => item.selected),
  set: (val) => {
    items.value.forEach(item => {
      item.selected = Boolean(val)
    })
  }
})

onMounted(() => {
  loadCart()
})

onBeforeUnmount(() => {
  debounceTimers.forEach(timer => clearTimeout(timer))
  debounceTimers.clear()
})

async function loadCart() {
  loading.value = true
  pageError.value = ''
  try {
    const res = await request.get('/cart/list', { silentError: true })
    if (res.code !== 1) {
      throw new Error(res.message || '购物车加载失败')
    }
    mergeCartList(res.data || [])
  } catch (e) {
    pageError.value = '购物车加载失败，请检查网络后重试'
    if (items.value.length === 0) {
      items.value = []
      syncCartCount()
    }
  } finally {
    loading.value = false
  }
}

function mergeCartList(serverList) {
  const selectedMap = new Map(items.value.map(item => [item.id, Boolean(item.selected)]))
  items.value = (serverList || []).map(item => ({
    ...item,
    selected: selectedMap.has(item.id) ? selectedMap.get(item.id) : true
  }))
  syncCartCount()
}

function getMaxQuantity(item) {
  const stock = Number(item?.stock || 0)
  if (stock > 0) return Math.max(1, Math.min(stock, DEFAULT_MAX_QTY))
  return DEFAULT_MAX_QTY
}

function isItemSaving(id) {
  return Boolean(inflightById[id] || queuedQtyById[id] !== undefined || removingById[id])
}

function handleQuantityChange(item, nextQty) {
  const max = getMaxQuantity(item)
  const normalized = Math.max(1, Math.min(max, Number(nextQty || 1)))
  if (normalized === Number(item.quantity || 1)) return

  item.quantity = normalized
  itemErrorMap[item.id] = ''
  syncCartCount()
  scheduleQuantitySync(item.id, normalized)
}

function scheduleQuantitySync(itemId, quantity) {
  queuedQtyById[itemId] = quantity
  if (debounceTimers.has(itemId)) {
    clearTimeout(debounceTimers.get(itemId))
  }
  const timer = setTimeout(() => {
    debounceTimers.delete(itemId)
    flushQuantitySync(itemId)
  }, 260)
  debounceTimers.set(itemId, timer)
}

async function flushQuantitySync(itemId) {
  if (inflightById[itemId]) return
  if (queuedQtyById[itemId] === undefined) return

  const quantity = Number(queuedQtyById[itemId] || 1)
  delete queuedQtyById[itemId]
  inflightById[itemId] = true

  try {
    const res = await request.post('/cart/update', { id: itemId, quantity }, { silentError: true })
    if (res.code !== 1) {
      throw new Error(res.message || '数量更新失败')
    }
    itemErrorMap[itemId] = ''
  } catch (e) {
    itemErrorMap[itemId] = '数量更新失败，已恢复为服务端数量'
    await reconcileItemFromServer(itemId)
  } finally {
    inflightById[itemId] = false
    if (queuedQtyById[itemId] !== undefined) {
      flushQuantitySync(itemId)
    }
  }
}

async function reconcileItemFromServer(itemId) {
  try {
    const res = await request.get('/cart/list', { silentError: true })
    if (res.code !== 1) return

    const latest = (res.data || []).find(row => row.id === itemId)
    if (!latest) {
      items.value = items.value.filter(row => row.id !== itemId)
    } else {
      const target = items.value.find(row => row.id === itemId)
      if (target) target.quantity = Number(latest.quantity || 1)
    }
    syncCartCount()
  } catch (e) {
    // Keep current optimistic state and local error text.
  }
}

async function removeItem(item) {
  const itemId = item.id
  if (removingById[itemId]) return
  removingById[itemId] = true
  itemErrorMap[itemId] = ''

  if (debounceTimers.has(itemId)) {
    clearTimeout(debounceTimers.get(itemId))
    debounceTimers.delete(itemId)
  }
  delete queuedQtyById[itemId]

  try {
    const res = await request.get('/cart/delete', {
      params: { id: itemId },
      silentError: true
    })
    if (res.code !== 1) {
      throw new Error(res.message || '删除失败')
    }
    items.value = items.value.filter(row => row.id !== itemId)
    delete itemErrorMap[itemId]
    syncCartCount()
  } catch (e) {
    itemErrorMap[itemId] = '删除失败，请稍后重试'
  } finally {
    removingById[itemId] = false
    inflightById[itemId] = false
  }
}

function toggleAll(val) {
  const checked = Boolean(val)
  items.value.forEach(item => {
    item.selected = checked
  })
}

function checkout() {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请先选择要结算的商品')
    return
  }
  router.push('/confirm')
}

function goBrowse() {
  router.push('/home')
}

function syncCartCount() {
  const totalQty = items.value.reduce((sum, row) => sum + Number(row.quantity || 0), 0)
  cartStore.setCount(totalQty)
}
</script>

<style scoped>
.cart-page {
  min-height: 100%;
  background: var(--color-bg);
  padding: 10px 12px calc(146px + env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cart-header {
  background: #fff;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  padding: 10px 12px;
}

.title {
  margin: 0;
  font-size: 22px;
  line-height: 1.2;
  font-weight: 900;
  color: var(--color-text);
}

.sub-info {
  margin: 6px 0 0;
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(0, 0, 0, 0.63);
  font-size: 12px;
  font-weight: 700;
}

.dot {
  color: rgba(0, 0, 0, 0.38);
}

.cart-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-row {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.8);
  padding: 10px;
}

.skeleton-check,
.skeleton-thumb,
.skeleton-line {
  background: linear-gradient(100deg, #f0f0f0 30%, #f8f8f8 50%, #f0f0f0 70%);
  background-size: 220% 100%;
  animation: shimmer 1.25s linear infinite;
}

.skeleton-check {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  flex-shrink: 0;
}

.skeleton-thumb {
  width: 72px;
  height: 72px;
  border-radius: 14px;
  flex-shrink: 0;
}

.skeleton-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.skeleton-line {
  height: 10px;
  border-radius: var(--radius-pill);
}

.skeleton-line.w70 {
  width: 70%;
}

.skeleton-line.w36 {
  width: 36%;
}

.skeleton-line.w88 {
  width: 88%;
}

.empty-wrap {
  background: #fff;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  padding: 16px 10px 18px;
  text-align: center;
}

.browse-btn {
  margin-top: 4px;
  border: var(--border-strong);
  box-shadow: 0 3px 0 rgba(0, 0, 0, 0.85);
  border-radius: var(--radius-pill);
  min-height: 38px;
  background: var(--color-primary);
  color: #111;
  font-weight: 800;
}

.browse-btn:active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.85);
}

.page-error {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  background: #fff;
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.85);
  padding: 10px 12px;
  color: #d62828;
  font-size: 12px;
  font-weight: 700;
}

.page-error button {
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  background: #fff;
  color: #111;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
  padding: 5px 10px;
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.8);
}

.checkout-bar {
  position: fixed;
  left: 12px;
  right: 12px;
  bottom: calc(58px + env(safe-area-inset-bottom));
  z-index: 95;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 10px;
  background: #fff;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-float);
  padding: 10px 12px;
}

.left {
  min-width: 56px;
}

.middle {
  min-width: 0;
}

.total {
  margin: 0;
  color: var(--color-accent);
  font-size: 22px;
  line-height: 1;
  font-weight: 900;
}

.hint {
  margin: 4px 0 0;
  color: rgba(0, 0, 0, 0.52);
  font-size: 11px;
  font-weight: 700;
}

.checkout-btn {
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  box-shadow: 0 3px 0 rgba(0, 0, 0, 0.88);
  min-height: 42px;
  padding: 0 18px;
  background: var(--color-primary);
  color: #111;
  font-size: 14px;
  font-weight: 900;
}

.checkout-btn:active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.88);
}

@keyframes shimmer {
  from {
    background-position: 180% 0;
  }
  to {
    background-position: -20% 0;
  }
}
</style>
