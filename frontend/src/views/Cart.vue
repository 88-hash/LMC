<template>
  <div class="cart-page">
    <div class="list-card">
      <el-empty v-if="items.length === 0" description="购物车空空如也" image="https://placehold.co/200x200?text=Empty+Cart" />
      <div v-else class="cart-list">
        <transition-group name="fade">
          <div v-for="item in items" :key="item.id" class="cart-item">
            <!-- 阻止事件冒泡，确保点击复选框不会触发其他事件 -->
            <div class="checkbox-wrapper" @click.stop>
              <el-checkbox v-model="item.selected" size="large" />
            </div>
            <img :src="item.imageUrl || placeholder" class="thumb" />
            <div class="info">
              <div class="title">{{ item.goodsName }}</div>
              <div class="meta">
                <span class="price">¥ {{ item.price }}</span>
              </div>
              <div class="actions">
                <el-button size="small" circle @click="decr(item)"><el-icon><Minus /></el-icon></el-button>
                <el-input-number v-model="item.quantity" :min="1" :max="999" @change="onChange(item)" />
                <el-button size="small" circle @click="incr(item)"><el-icon><Plus /></el-icon></el-button>
                <el-button type="danger" link @click="removeItem(item)">删除</el-button>
              </div>
            </div>
          </div>
        </transition-group>
      </div>
    </div>

    <div class="checkout-bar">
      <div class="check-all">
        <el-checkbox v-model="isAllSelected" @change="toggleAll">全选</el-checkbox>
      </div>
      <div class="summary">
        <span>已选 {{ selectedCount }} 件</span>
        <span class="sum">合计：<b>¥ {{ totalAmount }}</b></span>
      </div>
      <el-button type="primary" size="large" :loading="loading" class="checkout-btn" @click="checkout">
        去结算
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Minus, Plus } from '@element-plus/icons-vue'
import request from '../utils/request'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useCartStore()
const items = ref([])
const loading = ref(false)
const placeholder = 'https://placehold.co/120x120?text=Snack'

// 动态计算全选状态
const isAllSelected = computed({
  get: () => items.value.length > 0 && items.value.every(item => item.selected),
  set: (val) => items.value.forEach(item => item.selected = val)
})

// 计算选中商品的总价
const totalAmount = computed(() => {
  return items.value
    .filter(i => i.selected)
    .reduce((sum, i) => sum + Number(i.price || 0) * Number(i.quantity || 0), 0)
    .toFixed(2)
})

// 计算选中商品的总数
const selectedCount = computed(() => {
  return items.value
    .filter(i => i.selected)
    .reduce((sum, i) => sum + Number(i.quantity || 0), 0)
})

const load = async () => {
  const res = await request.get('/cart/list')
  if (res.code === 1) {
    items.value = (res.data || []).map(item => ({
      ...item,
      selected: true // 默认选中
    }))
    store.setCount(items.value.reduce((s, i) => s + i.quantity, 0))
  }
}

const toggleAll = (val) => {
  items.value.forEach(item => item.selected = val)
}

const onChange = async (item) => {
  const res = await request.post('/cart/update', { id: item.id, quantity: item.quantity })
  if (res.code === 1) {
    store.setCount(items.value.reduce((s, i) => s + i.quantity, 0))
  }
}

const incr = async (item) => {
  item.quantity = Number(item.quantity || 0) + 1
  await onChange(item)
}

const decr = async (item) => {
  const q = Number(item.quantity || 0) - 1
  if (q <= 0) {
    await removeItem(item)
  } else {
    item.quantity = q
    await onChange(item)
  }
}

const removeItem = async (item) => {
  const res = await request.get('/cart/delete', { params: { id: item.id } })
  if (res.code === 1) {
    items.value = items.value.filter(i => i.id !== item.id)
    store.setCount(items.value.reduce((s, i) => s + i.quantity, 0))
    ElMessage.success('已删除')
  }
}

const checkout = () => {
  if (selectedCount.value === 0) return ElMessage.warning('请先选择商品')
  router.push('/confirm')
}

onMounted(load)
</script>

<style scoped>
.cart-page {
  padding-bottom: 120px; /* Space for checkout bar + tab bar */
  padding-left: 16px;
  padding-right: 16px;
  padding-top: 16px;
}

.list-card {
  background: transparent;
  box-shadow: none;
  padding: 0;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cart-item {
  display: flex;
  gap: 14px;
  padding: 16px;
  border-radius: 16px; /* 使用固定数值代替变量 */
  background: white;
  align-items: center;
  transition: all .25s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05); /* 使用固定数值代替变量 */
}

.cart-item:active {
  transform: scale(0.98);
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  padding-right: 4px;
}

.thumb {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  object-fit: cover;
  background: #f2f2f2;
}

.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 80px;
}

.title {
  font-weight: 600;
  color: #333;
  font-size: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.price {
  color: #ff6b6b; /* 硬编码颜色 */
  font-weight: 700;
  font-size: 16px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.checkout-bar {
  position: fixed;
  left: 16px;
  right: 16px;
  bottom: calc(60px + env(safe-area-inset-bottom)); /* Above TabBar */
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 30px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  z-index: 900;
}

.check-all {
  display: flex;
  align-items: center;
}

.summary {
  display: flex;
  flex-direction: column;
  justify-content: center;
  margin-left: auto;
  margin-right: 12px;
  text-align: right;
}

.sum {
  font-size: 14px;
  color: #333;
}

.sum b {
  font-size: 18px;
  color: #ff6b6b; /* 硬编码颜色 */
}

.checkout-btn {
  border-radius: 20px;
  padding: 0 24px;
  background-color: #ff6b6b !important; /* 强制颜色 */
  border: none;
  color: white !important;
  font-weight: 600;
  height: 40px;
}

.fade-leave-active, .fade-enter-active { transition: all .25s }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(10px) }
</style>
