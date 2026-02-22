<template>
  <div class="order-page">
    <section class="toolbar-card">
      <button
        type="button"
        class="sticker-btn sticker-btn-primary"
        :disabled="isLoading || refreshing"
        @click="handleRefresh"
      >
        <el-icon :class="{ spinning: isLoading || refreshing }"><Refresh /></el-icon>
        <span>{{ isLoading || refreshing ? '查询中...' : '查询 / 刷新订单状态' }}</span>
      </button>
      <p class="toolbar-tip">待核销订单会显示核销码，到店可直接出示。</p>
    </section>

    <div v-if="showPayTip" class="pay-tip">支付成功，已定位到最新订单。</div>

    <section v-if="isLoading" class="state-card loading-list">
      <div v-for="n in 3" :key="`loading-${n}`" class="loading-item">
        <div class="loading-line w66"></div>
        <div class="loading-line w38"></div>
        <div class="loading-line w90"></div>
      </div>
    </section>

    <section v-else-if="pageError" class="state-card">
      <div class="state-title">订单加载失败</div>
      <p class="state-desc">{{ pageError }}</p>
      <button type="button" class="sticker-btn sticker-btn-secondary" @click="handleRefresh">
        重试加载
      </button>
    </section>

    <section v-else-if="orders.length === 0" class="state-card">
      <el-empty description="还没有订单，先去逛逛吧～" />
      <button type="button" class="sticker-btn sticker-btn-secondary" @click="goBrowse">
        去首页看看
      </button>
    </section>

    <section v-else class="order-list">
      <article
        v-for="o in orders"
        :id="`order-${o.orderNo}`"
        :key="o.id"
        class="order-card"
        :class="{ 'is-highlight': String(highlightOrderNo) === String(o.orderNo) }"
      >
        <header class="head">
          <div class="head-main">
            <p class="head-label">订单号</p>
            <p class="head-no">{{ o.orderNo }}</p>
          </div>
          <span class="status-pill" :class="`status-${getStatusMeta(o.status).kind}`">
            {{ getStatusMeta(o.status).text }}
          </span>
        </header>

        <div class="summary-row">
          <span class="summary-text">
            共 {{ orderMetaMap[o.id]?.itemCount || 0 }} 件商品
          </span>
          <div class="amount-pill">
            <span>实付</span>
            <strong>￥{{ Number(o.totalAmount || 0).toFixed(2) }}</strong>
          </div>
        </div>

        <div class="items-box">
          <div v-if="!(orderMetaMap[o.id]?.preview?.length)" class="item-empty">
            商品明细加载中...
          </div>
          <div v-else class="item-list">
            <div v-for="it in orderMetaMap[o.id].preview" :key="it.id" class="item-row">
              <span class="item-name">{{ it.goodsName }}</span>
              <span class="item-qty">x{{ it.quantity }}</span>
            </div>
            <div v-if="orderMetaMap[o.id].extraCount > 0" class="item-more">
              还有 {{ orderMetaMap[o.id].extraCount }} 件商品
            </div>
          </div>
        </div>

        <div v-if="Number(o.status) === 0" class="verify-code-section">
          <div class="verify-label">核销码</div>
          <div class="verify-code">{{ formatVerifyCode(o.verifyCode) }}</div>
        </div>

        <footer class="actions">
          <button
            type="button"
            class="sticker-btn sticker-btn-secondary"
            :disabled="refreshing || isLoading"
            @click="handleRefresh"
          >
            {{ refreshing ? '刷新中...' : '刷新状态' }}
          </button>
          <button
            v-if="Number(o.status) === 1"
            type="button"
            class="sticker-btn sticker-btn-primary"
            @click="goComment(o.id)"
          >
            去评价
          </button>
        </footer>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import request from '../../utils/request'

const route = useRoute()
const router = useRouter()

const orders = ref([])
const details = ref({})
const isLoading = ref(false)
const refreshing = ref(false)
const pageError = ref('')
const highlightOrderNo = ref('')
const showPayTip = ref(false)

const STATUS_META = {
  0: { text: '待核销', kind: 'pending' },
  1: { text: '已完成', kind: 'done' },
  2: { text: '已取消', kind: 'cancel' },
  '-1': { text: '已取消', kind: 'cancel' }
}

const orderMetaMap = computed(() => {
  const meta = {}
  for (const order of orders.value) {
    const items = details.value[order.id] || []
    meta[order.id] = {
      preview: items.slice(0, 2),
      extraCount: Math.max(0, items.length - 2),
      itemCount: items.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
    }
  }
  return meta
})

const getStatusMeta = (status) => {
  return STATUS_META[status] || { text: '未知状态', kind: 'cancel' }
}

const formatVerifyCode = (code) => {
  if (!code) return '******'
  return String(code).replace(/(.{3})/g, '$1 ').trim()
}

const focusHighlightedOrder = async () => {
  const targetNo = route.query?.highlight
  if (!targetNo) return

  const found = orders.value.find(order => String(order.orderNo) === String(targetNo))
  if (!found) return

  highlightOrderNo.value = String(targetNo)
  showPayTip.value = route.query?.fromPay === '1'

  await nextTick()
  const el = document.getElementById(`order-${targetNo}`)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  setTimeout(() => {
    highlightOrderNo.value = ''
    showPayTip.value = false
    if (route.query?.highlight || route.query?.fromPay) {
      router.replace({ path: '/order' })
    }
  }, 2200)
}

const load = async ({ manual = false } = {}) => {
  if (isLoading.value || refreshing.value) return

  if (manual) {
    refreshing.value = true
  } else {
    isLoading.value = true
  }
  pageError.value = ''

  try {
    const orderRes = await request.get('/order/list', { silentError: true })
    orders.value = orderRes.data || []

    const detailMap = {}
    await Promise.all(
      orders.value.map(async (order) => {
        try {
          const detailRes = await request.get('/order/detail', {
            params: { id: order.id },
            silentError: true
          })
          detailMap[order.id] = detailRes.data?.items || []
        } catch (error) {
          detailMap[order.id] = []
        }
      })
    )
    details.value = detailMap

    await focusHighlightedOrder()
    if (manual) {
      ElMessage.success('订单状态已更新')
    }
  } catch (error) {
    pageError.value = '网络异常，请稍后重试'
  } finally {
    isLoading.value = false
    refreshing.value = false
  }
}

const handleRefresh = () => {
  load({ manual: true })
}

const goComment = (id) => {
  router.push(`/comment/add/${id}`)
}

const goBrowse = () => {
  router.push('/home')
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.order-page {
  min-height: 100%;
  max-width: 100%;
  overflow-x: hidden;
  padding: 10px 12px calc(96px + env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-page,
.order-page * {
  box-sizing: border-box;
}

.toolbar-card {
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-card, 20px);
  background: #fff;
  box-shadow: var(--shadow-card, 0 6px 0 #000);
  padding: 10px;
}

.toolbar-tip {
  margin: 8px 0 0;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.66);
  font-weight: 700;
}

.pay-tip {
  border: var(--border-strong, 2px solid #000);
  border-radius: 14px;
  background: #e8fff1;
  box-shadow: 0 4px 0 #000;
  padding: 10px;
  font-size: 12px;
  font-weight: 800;
  color: #111;
}

.state-card {
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-card, 20px);
  background: #fff;
  box-shadow: var(--shadow-card, 0 6px 0 #000);
  padding: 16px 12px;
  text-align: center;
}

.state-title {
  font-size: 18px;
  line-height: 1.2;
  font-weight: 900;
  color: #111;
}

.state-desc {
  margin: 8px 0 12px;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.7);
  font-weight: 700;
}

.loading-list {
  display: grid;
  gap: 10px;
}

.loading-item {
  border: var(--border-strong, 2px solid #000);
  border-radius: 14px;
  background: #fffbe8;
  box-shadow: 0 4px 0 #000;
  padding: 12px;
  display: grid;
  gap: 8px;
}

.loading-line {
  height: 10px;
  border-radius: var(--radius-pill, 999px);
  background: linear-gradient(100deg, #f0efea 20%, #f8f6ef 40%, #f0efea 60%);
  background-size: 220% 100%;
  animation: shimmer 1.2s linear infinite;
}

.loading-line.w66 {
  width: 66%;
}

.loading-line.w38 {
  width: 38%;
}

.loading-line.w90 {
  width: 90%;
}

.order-list {
  display: grid;
  gap: 12px;
}

.order-card {
  max-width: 100%;
  min-width: 0;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-card, 20px);
  background: #fff;
  box-shadow: var(--shadow-card, 0 6px 0 #000);
  padding: 12px;
  display: grid;
  gap: 10px;
  content-visibility: auto;
  contain-intrinsic-size: 260px;
  transition: transform .12s ease, box-shadow .12s ease, background-color .12s ease;
}

.order-card.is-highlight {
  background: #fff7c8;
  box-shadow: 0 10px 0 #000;
}

.order-card:active {
  transform: translateY(2px);
  box-shadow: 0 4px 0 #000;
}

.head {
  display: flex;
  gap: 8px;
  justify-content: space-between;
  align-items: flex-start;
}

.head-main {
  min-width: 0;
  flex: 1;
}

.head-label {
  margin: 0;
  font-size: 11px;
  color: rgba(0, 0, 0, 0.55);
  font-weight: 700;
}

.head-no {
  margin: 2px 0 0;
  font-size: 14px;
  line-height: 1.3;
  font-weight: 900;
  color: #111;
  word-break: break-all;
}

.status-pill {
  flex-shrink: 0;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-pill, 999px);
  padding: 4px 10px;
  font-size: 12px;
  line-height: 1;
  font-weight: 900;
  color: #111;
  box-shadow: 0 3px 0 #000;
}

.status-pending {
  background: #ffe27a;
}

.status-done {
  background: #97f4b6;
}

.status-cancel {
  background: #f1f1f1;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.summary-text {
  min-width: 0;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.62);
  font-weight: 700;
}

.amount-pill {
  flex-shrink: 0;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-pill, 999px);
  background: #fff0f7;
  box-shadow: 0 3px 0 #000;
  padding: 5px 10px;
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.amount-pill span {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.58);
  font-weight: 700;
}

.amount-pill strong {
  font-size: 18px;
  line-height: 1;
  color: var(--color-accent, #ff69b4);
}

.items-box {
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  background: #fafafa;
  padding: 9px;
  min-width: 0;
}

.item-empty {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.5);
  font-weight: 700;
}

.item-list {
  display: grid;
  gap: 6px;
}

.item-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.item-name {
  min-width: 0;
  flex: 1;
  font-size: 13px;
  color: #222;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-qty {
  flex-shrink: 0;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.65);
  font-weight: 800;
}

.item-more {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.5);
  font-weight: 700;
}

.verify-code-section {
  border: var(--border-strong, 2px solid #000);
  border-radius: 14px;
  background: #fff7df;
  box-shadow: 0 4px 0 #000;
  padding: 9px 10px;
  text-align: center;
}

.verify-label {
  font-size: 12px;
  color: #d98200;
  font-weight: 900;
}

.verify-code {
  margin-top: 4px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
  font-size: 30px;
  line-height: 1;
  letter-spacing: 1px;
  color: #111;
  font-weight: 900;
  word-break: break-all;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.sticker-btn {
  min-height: 42px;
  min-width: 118px;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-pill, 999px);
  box-shadow: 0 4px 0 #000;
  padding: 9px 14px;
  font-size: 14px;
  font-weight: 900;
  color: #111;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: #fff;
  transition: transform .12s ease, box-shadow .12s ease, filter .12s ease;
}

.sticker-btn:active {
  transform: translateY(2px) scale(0.99);
  box-shadow: 0 2px 0 #000;
}

.sticker-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.sticker-btn-primary {
  background: #ffe46f;
}

.sticker-btn-secondary {
  background: #eef6ff;
}

.toolbar-card .sticker-btn {
  width: 100%;
}

.actions .sticker-btn {
  flex: 1;
  min-width: 0;
}

.spinning {
  animation: rotate 1s linear infinite;
}

@keyframes shimmer {
  from {
    background-position: 180% 0;
  }
  to {
    background-position: -20% 0;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
