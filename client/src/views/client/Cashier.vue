<template>
  <div class="cashier-container">
    <div class="card animate__animated animate__zoomIn">
      <div class="header">
        <div class="title">收银台</div>
        <div class="subtitle">订单提交成功，请尽快支付</div>
      </div>

      <div class="amount-box">
        <div class="label">支付金额</div>
        <div class="amount">￥{{ order.totalAmount }}</div>
        <div class="timer">
          <el-icon><Timer /></el-icon>
          <span>支付剩余时间 {{ formatTime(timeLeft) }}</span>
        </div>
      </div>

      <div class="pay-methods">
        <div
          class="method-item"
          :class="{ active: payMethod === 'wechat' }"
          @click="payMethod = 'wechat'"
        >
          <div class="icon wechat"><el-icon><ChatDotRound /></el-icon></div>
          <span class="name">微信支付</span>
          <el-icon v-if="payMethod === 'wechat'" class="check"><Select /></el-icon>
        </div>

        <div
          class="method-item"
          :class="{ active: payMethod === 'alipay' }"
          @click="payMethod = 'alipay'"
        >
          <div class="icon alipay"><el-icon><Money /></el-icon></div>
          <span class="name">支付宝</span>
          <el-icon v-if="payMethod === 'alipay'" class="check"><Select /></el-icon>
        </div>
      </div>

      <div class="action-group">
        <el-button
          class="cancel-btn"
          size="large"
          :disabled="!canCancel || canceling"
          :loading="canceling"
          @click="handleCancel"
        >
          取消订单
        </el-button>

        <el-button
          type="primary"
          class="pay-btn"
          size="large"
          :loading="paying"
          :disabled="isCanceled"
          @click="handlePay"
        >
          {{ isCanceled ? '订单已取消' : `立即支付 ￥${order.totalAmount}` }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Timer, ChatDotRound, Money, Select } from '@element-plus/icons-vue'
import { ElMessage, ElLoading, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const route = useRoute()
const router = useRouter()
const orderNo = route.query.orderNo
const order = ref({ totalAmount: '0.00', status: 0 })
const timeLeft = ref(900)
const payMethod = ref('wechat')
const paying = ref(false)
const canceling = ref(false)
const isCanceled = ref(false)
let timerInterval = null

const canCancel = computed(() => {
  if (!orderNo) return false
  return Number(order.value?.status) === 0 && !isCanceled.value
})

const fetchOrder = async () => {
  if (!orderNo) return
  try {
    const res = await request.get('/order/getByNo', { params: { orderNo } })
    if (res.code === 1) {
      order.value = res.data || order.value
      isCanceled.value = Number(res.data?.status) === 2
    } else {
      ElMessage.error(res.message)
    }
  } catch (error) {
    console.error(error)
  }
}

const startTimer = () => {
  timerInterval = setInterval(() => {
    if (timeLeft.value > 0) {
      timeLeft.value--
    } else {
      clearInterval(timerInterval)
    }
  }, 1000)
}

const formatTime = (seconds) => {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

const handlePay = async () => {
  if (isCanceled.value) {
    ElMessage.warning('订单已取消，无法继续支付')
    return
  }

  paying.value = true
  const loading = ElLoading.service({
    lock: true,
    text: '正在连接支付网关...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    await new Promise(r => setTimeout(r, 1000))

    const res = await request.post('/order/pay', {
      orderNo,
      payMethod: payMethod.value
    })

    if (res.code === 1) {
      ElMessage.success('支付成功')
      router.push({ path: '/order', query: { highlight: orderNo, fromPay: '1' } })
    } else {
      ElMessage.error(res.msg || res.message || '支付失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('支付请求异常')
  } finally {
    loading.close()
    paying.value = false
  }
}

const handleCancel = async () => {
  if (!canCancel.value) return

  try {
    await ElMessageBox.confirm('确认取消当前订单吗？取消后不可继续支付。', '取消订单', {
      confirmButtonText: '确认取消',
      cancelButtonText: '再想想',
      type: 'warning'
    })
  } catch {
    return
  }

  canceling.value = true
  try {
    const res = await request.post('/order/cancel', { orderNo })
    if (res.code === 1) {
      isCanceled.value = true
      ElMessage.success('订单已取消')
      router.replace({ path: '/order', query: { highlight: orderNo, fromCancel: '1' } })
    } else {
      ElMessage.error(res.message || '取消失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error(error?.message || '取消失败')
  } finally {
    canceling.value = false
  }
}

onMounted(() => {
  fetchOrder()
  startTimer()
})

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
})
</script>

<style scoped>
.cashier-container {
  min-height: 100vh;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  background: var(--color-bg, #fffbe6);
  padding: 14px 12px calc(20px + env(safe-area-inset-bottom));
}

.cashier-container *,
.cashier-container *::before,
.cashier-container *::after {
  box-sizing: border-box;
}

.card {
  width: 100%;
  max-width: 100%;
  min-width: 0;
  background: #fffdf5;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-card, 20px);
  box-shadow: var(--shadow-float, 0 10px 0 #000);
  padding: 16px 14px;
}

.header {
  text-align: center;
  margin-bottom: 14px;
}

.title {
  font-size: 22px;
  line-height: 1.1;
  font-weight: 900;
  color: var(--color-text, #111);
}

.subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.62);
  overflow-wrap: anywhere;
}

.amount-box {
  width: 100%;
  max-width: 100%;
  min-width: 0;
  border: var(--border-strong, 2px solid #000);
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 4px 0 #000;
  padding: 14px 10px;
  text-align: center;
  margin-bottom: 14px;
}

.amount-box .label {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.62);
}

.amount-box .amount {
  margin-top: 2px;
  max-width: 100%;
  font-size: clamp(30px, 10vw, 40px);
  line-height: 1;
  font-weight: 900;
  color: var(--color-accent, #ff69b4);
  overflow-wrap: anywhere;
}

.timer {
  margin-top: 8px;
  width: 100%;
  max-width: 100%;
  min-width: 0;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 4px 10px;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-pill, 999px);
  background: #fff3f8;
  color: #000;
  font-size: 12px;
  font-weight: 700;
}

.timer span {
  min-width: 0;
  overflow-wrap: anywhere;
}

.pay-methods {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 14px;
  min-width: 0;
}

.method-item {
  width: 100%;
  max-width: 100%;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fff;
  border: var(--border-strong, 2px solid #000);
  border-radius: 14px;
  box-shadow: 0 4px 0 #000;
  padding: 11px 12px;
  transition: transform .12s ease, box-shadow .12s ease, background-color .12s ease;
  user-select: none;
}

.method-item .icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  border: var(--border-strong, 2px solid #000);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
  flex-shrink: 0;
}

.method-item .icon.wechat { background: #07c160; }
.method-item .icon.alipay { background: #1677ff; }

.method-item .name {
  flex: 1;
  min-width: 0;
  font-size: 14px;
  font-weight: 800;
  color: var(--color-text, #111);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.method-item .check {
  color: var(--color-primary, #ffd700);
  font-size: 18px;
  flex-shrink: 0;
}

.method-item.active {
  background: #fff7d9;
  box-shadow: var(--shadow-card, 0 6px 0 #000);
}

.method-item:active {
  transform: translateY(2px) scale(0.99);
  box-shadow: 0 2px 0 #000;
}

.action-group {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
  min-width: 0;
}

.cancel-btn,
.pay-btn {
  width: 100%;
  max-width: 100%;
  min-width: 0;
  height: 46px;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-pill, 999px);
  font-size: 16px;
  font-weight: 900;
  box-shadow: var(--shadow-card, 0 6px 0 #000);
}

.cancel-btn {
  background: #fff;
  color: #000;
}

.pay-btn {
  background: var(--color-primary, #ffd700);
  color: #000;
}

.cancel-btn:active,
.pay-btn:active {
  transform: translateY(2px) scale(0.99);
  box-shadow: 0 3px 0 #000;
}

:deep(.cancel-btn .el-button__text),
:deep(.pay-btn .el-button__text) {
  white-space: normal;
  overflow-wrap: anywhere;
}
</style>
