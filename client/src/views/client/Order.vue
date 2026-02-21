<template>
  <div class="order-page">
    <div class="cards">
      <div class="refresh-bar" @click="load">
        <el-icon><Refresh /></el-icon>
        <span>刷新订单状态</span>
      </div>

      <div v-if="showPayTip" class="pay-tip">
        支付成功，已为你定位到最新订单
      </div>

      <el-empty v-if="orders.length === 0" description="暂无订单" image="https://placehold.co/200x200?text=No+Orders" />

      <div v-else class="order-list">
        <div
          v-for="o in orders"
          :id="`order-${o.orderNo}`"
          :key="o.id"
          class="order-card animate__animated animate__fadeIn"
          :class="{ 'is-highlight': String(highlightOrderNo) === String(o.orderNo) }"
        >
          <div class="head">
            <div class="no">订单号：{{ o.orderNo }}</div>
            <el-tag :type="getStatusType(o.status)" effect="dark" round size="small">
              {{ getStatusText(o.status) }}
            </el-tag>
          </div>

          <div class="body">
            <div class="items">
              <el-skeleton v-if="!details[o.id]" rows="2" animated />
              <div v-else class="item-list">
                <div v-for="it in details[o.id]" :key="it.id" class="item">
                  <span class="name">{{ it.goodsName }}</span>
                  <span class="qty">x{{ it.quantity }}</span>
                </div>
              </div>
            </div>

            <div v-if="o.status === 0" class="verify-code-section animate__animated animate__pulse">
              <div class="vc-label">出示核销码</div>
              <div class="vc-num">{{ formatVerifyCode(o.verifyCode) }}</div>
            </div>

            <div class="divider"></div>

            <div class="meta">
              <span>实付金额</span>
              <b class="price">¥{{ Number(o.totalAmount || 0).toFixed(2) }}</b>
            </div>
          </div>

          <div class="foot">
            <el-button v-if="o.status === 0" type="primary" plain round class="action-btn" @click="load">刷新状态</el-button>
            <el-button v-if="o.status === 1" type="success" plain round class="action-btn" @click="goComment(o.id)">去评价</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import request from '../../utils/request'

const route = useRoute()
const router = useRouter()

const orders = ref([])
const details = ref({})
const highlightOrderNo = ref('')
const showPayTip = ref(false)

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'info', '-1': 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待核销', 1: '已完成', 2: '已取消', '-1': '已取消' }
  return map[status] || '未知'
}

const formatVerifyCode = (code) => {
  if (!code) return '******'
  return String(code).replace(/(.{4})/g, '$1 ').trim()
}

const focusHighlightedOrder = async () => {
  const targetNo = route.query?.highlight
  if (!targetNo) return

  const found = orders.value.find(o => String(o.orderNo) === String(targetNo))
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

const load = async () => {
  const res = await request.get('/order/list')
  if (res.code === 1) {
    orders.value = res.data || []
    const ids = orders.value.map(o => o.id)
    await Promise.all(ids.map(async (id) => {
      const d = await request.get('/order/detail', { params: { id } })
      if (d.code === 1) details.value[id] = d.data.items || []
    }))
    await focusHighlightedOrder()
  }
}

const goComment = (id) => {
  router.push(`/comment/add/${id}`)
}

onMounted(load)
</script>

<style scoped>
.order-page {
  padding: 12px;
  padding-bottom: calc(92px + env(safe-area-inset-bottom));
}

.refresh-bar {
  background: #fff7d9;
  color: #000;
  padding: 10px;
  border-radius: 12px;
  text-align: center;
  margin-bottom: 10px;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  border: var(--border-strong, 2px solid #000);
  box-shadow: 0 4px 0 #000;
  font-weight: 700;
}

.refresh-bar:active {
  transform: translateY(2px) scale(0.995);
  box-shadow: 0 2px 0 #000;
}

.pay-tip {
  margin-bottom: 10px;
  padding: 8px 10px;
  border: var(--border-strong, 2px solid #000);
  border-radius: 12px;
  background: #eafff1;
  box-shadow: 0 4px 0 #000;
  font-size: 12px;
  color: #000;
  font-weight: 700;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background: #fff;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-card, 20px);
  box-shadow: var(--shadow-card, 0 6px 0 #000);
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: transform .18s ease, box-shadow .18s ease;
}

.order-card.is-highlight {
  background: #fff9d9;
  box-shadow: 0 10px 0 #000;
}

.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.2);
  gap: 10px;
}

.no {
  font-weight: 700;
  color: var(--color-text, #111);
  font-size: 13px;
  line-height: 1.3;
}

.items {
  background: #fafafa;
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  padding: 8px;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.item {
  display: flex;
  justify-content: space-between;
  color: rgba(0, 0, 0, 0.72);
  font-size: 13px;
}

.item .name {
  max-width: 70%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.verify-code-section {
  background: #fff7e6;
  border: var(--border-strong, 2px solid #000);
  border-radius: 12px;
  padding: 10px;
  text-align: center;
  margin: 6px 0;
  box-shadow: 0 4px 0 #000;
}

.vc-label {
  font-size: 12px;
  color: #fa8c16;
  margin-bottom: 4px;
  font-weight: 700;
}

.vc-num {
  font-size: 22px;
  font-weight: 900;
  color: var(--color-text, #111);
  font-family: monospace;
  letter-spacing: 2px;
}

.divider {
  border-bottom: 1px dashed rgba(0, 0, 0, 0.2);
}

.meta {
  display: flex;
  justify-content: flex-end;
  align-items: baseline;
  gap: 8px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.65);
}

.price {
  color: var(--color-accent, #ff69b4);
  font-size: 20px;
  font-weight: 900;
}

.foot {
  display: flex;
  justify-content: flex-end;
  padding-top: 2px;
}

.action-btn {
  border-width: 2px;
  font-weight: 700;
}

:deep(.el-button.action-btn:active) {
  transform: translateY(1px) scale(0.99);
}
</style>
