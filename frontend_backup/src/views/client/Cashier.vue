<template>
  <div class="cashier-container">
    <div class="card animate__animated animate__zoomIn">
      <div class="header">
        <div class="title">收银台</div>
        <div class="subtitle">订单提交成功，请尽快支付</div>
      </div>

      <div class="amount-box">
        <div class="label">支付金额</div>
        <div class="amount">¥{{ order.totalAmount }}</div>
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

      <el-button 
        type="primary" 
        class="pay-btn" 
        size="large"
        :loading="paying"
        @click="handlePay"
      >
        立即支付 ¥{{ order.totalAmount }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Timer, ChatDotRound, Money, Select } from '@element-plus/icons-vue'
import { ElMessage, ElLoading } from 'element-plus'
import request from '../../utils/request'

const route = useRoute()
const router = useRouter()
const orderNo = route.query.orderNo
const order = ref({ totalAmount: '0.00' })
const timeLeft = ref(900) // 15分钟
const payMethod = ref('wechat')
const paying = ref(false)
let timerInterval = null

const fetchOrder = async () => {
  if (!orderNo) return
  try {
    const res = await request.get('/order/getByNo', { params: { orderNo } })
    if (res.code === 1) {
      order.value = res.data
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
  paying.value = true
  const loading = ElLoading.service({
    lock: true,
    text: '正在连接支付网关...',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  
  try {
    // 模拟网络延迟
    await new Promise(r => setTimeout(r, 1000))

    // 调用后端支付接口
    const res = await request.post('/order/pay', {
      orderNo: orderNo,
      payMethod: payMethod.value
    })
    
    if (res.code === 1) {
      ElMessage.success('支付成功！')
      router.push('/order') // 跳转回订单列表，此时状态应已更新
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
  background: #f7f1e3;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 24px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.05);
  text-align: center;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  margin-bottom: 5px;
}

.subtitle {
  font-size: 13px;
  color: #999;
  margin-bottom: 30px;
}

.amount-box {
  margin-bottom: 40px;
}

.amount-box .label {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.amount-box .amount {
  font-size: 40px;
  font-weight: 800;
  color: #ff9f43; /* 活力橙 */
  font-family: 'DIN Alternate', sans-serif;
}

.timer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  color: #ff6b6b;
  font-size: 13px;
  margin-top: 10px;
  background: #fff0f0;
  padding: 4px 12px;
  border-radius: 20px;
  display: inline-flex;
}

.pay-methods {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 30px;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.method-item.active {
  border-color: #54a0ff;
  background: #f0f9ff;
  box-shadow: 0 4px 12px rgba(84, 160, 255, 0.15);
}

.method-item .icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 12px;
  color: white;
}

.method-item .icon.wechat { background: #07c160; }
.method-item .icon.alipay { background: #1677ff; }

.method-item .name {
  flex: 1;
  text-align: left;
  font-weight: 500;
  color: #333;
}

.method-item .check {
  color: #54a0ff;
  font-weight: bold;
}

.pay-btn {
  width: 100%;
  border-radius: 25px;
  font-weight: 600;
  background: linear-gradient(45deg, #0984e3, #74b9ff);
  border: none;
  box-shadow: 0 4px 15px rgba(9, 132, 227, 0.3);
  transition: all 0.3s;
}

.pay-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(9, 132, 227, 0.4);
}
</style>