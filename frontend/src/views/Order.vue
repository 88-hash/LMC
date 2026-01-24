<template>
  <div class="order-page">
    <div class="cards">
      <!-- 手动刷新提示栏 -->
      <div class="refresh-bar" @click="load">
        <el-icon><Refresh /></el-icon>
        <span>点击刷新订单状态</span>
      </div>

      <el-empty v-if="orders.length === 0" description="暂无订单" />
      <div v-else class="order-list">
        <div v-for="o in orders" :key="o.id" class="order-card animate__animated animate__fadeIn">
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
            
            <!-- 核销码展示区域 (统一：状态0为待核销) -->
            <div v-if="o.status === 0" class="verify-code-section animate__animated animate__pulse">
              <div class="vc-label">出示核销码</div>
              <div class="vc-num">{{ formatVerifyCode(o.verifyCode) }}</div>
            </div>

            <div class="divider"></div>
            <div class="meta">
              <span>实付金额</span>
              <b class="price">¥ {{ Number(o.totalAmount || 0).toFixed(2) }}</b>
            </div>
          </div>
          <div class="foot">
            <el-button v-if="o.status === 0" type="primary" plain round class="action-btn" @click="load">刷新状态</el-button>
            <el-button v-if="o.status === 1" type="success" plain round class="action-btn" @click="goComment(o.id)">去评价</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 移除了 verifyVisible 弹窗相关代码，因为直接在卡片展示了 -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const orders = ref([])
const details = ref({})

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', '-1': 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待核销', 1: '已完成', '-1': '已取消' }
  return map[status] || '未知'
}

const formatVerifyCode = (code) => {
  if (!code) return '******'
  return code.replace(/(.{4})/g, '$1 ').trim()
}
// ... existing load/goToPay/goComment logic ...
const load = async () => {
  const res = await request.get('/order/list')
  if (res.code === 1) {
    orders.value = res.data || []
    const ids = orders.value.map(o => o.id)
    await Promise.all(ids.map(async (id) => {
      const d = await request.get('/order/detail', { params: { id } })
      if (d.code === 1) details.value[id] = d.data.items || []
    }))
  }
}

const goToPay = (o) => {
  router.push(`/cashier?orderNo=${o.orderNo}`)
}

const goComment = (id) => { 
  router.push(`/comment/add/${id}`) 
}

onMounted(load)
</script>

<style scoped>
/* ... existing styles ... */
.verify-code-section {
  background: #fff7e6;
  border: 1px dashed #ffa940;
  border-radius: 8px;
  padding: 12px;
  text-align: center;
  margin: 8px 0;
}

.vc-label {
  font-size: 12px;
  color: #fa8c16;
  margin-bottom: 4px;
}

.vc-num {
  font-size: 24px;
  font-weight: 800;
  color: #333;
  font-family: monospace;
  letter-spacing: 2px;
}

.refresh-bar {
  background: #e6f7ff;
  color: #1890ff;
  padding: 10px;
  border-radius: 8px;
  text-align: center;
  margin-bottom: 12px;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  border: 1px dashed #91d5ff;
}

.order-page { padding: 16px; padding-bottom: 80px; }
.order-list { display: flex; flex-direction: column; gap: 16px; }
.order-card { background: white; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); padding: 16px; display: flex; flex-direction: column; gap: 12px; }
.head { display: flex; justify-content: space-between; align-items: center; padding-bottom: 8px; border-bottom: 1px dashed #f0f0f0; }
.no { font-weight: 600; color: #333; font-size: 14px; }
.items { background: #f9f9f9; border-radius: 8px; padding: 8px; }
.item-list { display: flex; flex-direction: column; gap: 6px; }
.item { display: flex; justify-content: space-between; color: #666; font-size: 13px; }
.meta { display: flex; justify-content: flex-end; align-items: baseline; gap: 8px; font-size: 13px; color: #666; }
.price { color: #ff6b6b; font-size: 18px; font-weight: bold; }
.foot { display: flex; justify-content: flex-end; padding-top: 4px; }
.action-btn { padding: 8px 20px; font-weight: 600; }
.big { font-size: 18px; font-weight: bold; color: #333; }
.tips { color: #999; font-size: 12px; }
</style>
