<template>
  <div class="order-page">
    <div class="cards">
      <el-empty v-if="orders.length === 0" description="暂无订单" />
      <div v-else class="order-list">
        <div v-for="o in orders" :key="o.id" class="order-card animate__animated animate__fadeIn">
          <div class="head">
            <div class="no">订单号：{{ o.orderNo }}</div>
            <el-tag :type="o.status === 0 ? 'warning' : 'success'" effect="dark" round size="small">
              {{ o.status === 0 ? '待核销' : '已完成' }}
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
            <div class="divider"></div>
            <div class="meta">
              <span>实付金额</span>
              <b class="price">¥ {{ Number(o.totalAmount || 0).toFixed(2) }}</b>
            </div>
          </div>
          <div class="foot">
            <el-button v-if="o.status===0" type="primary" round class="action-btn" @click="showVerify(o)">出示核销码</el-button>
            <el-button v-else type="primary" plain round class="action-btn" @click="goComment(o.id)">去评价</el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="verifyVisible" width="80%" center align-center :show-close="false" class="verify-dialog">
      <div class="verify-box">
        <div class="big">取货核销码</div>
        <div class="code">{{ currentOrder?.verifyCode || '******' }}</div>
        <div class="tips">请向店员出示此码</div>
        <el-button round @click="verifyVisible = false" style="margin-top: 20px">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const orders = ref([])
const details = ref({})
const verifyVisible = ref(false)
const currentOrder = ref(null)

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

const showVerify = (o) => { currentOrder.value = o; verifyVisible.value = true }
const goComment = (id) => { router.push(`/comment/add/${id}`) }

onMounted(load)
</script>

<style scoped>
.order-page {
  padding: 16px;
  padding-bottom: 80px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: white;
  border-radius: var(--card-radius);
  box-shadow: var(--shadow-card);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: transform 0.2s;
}

.order-card:active {
  transform: scale(0.99);
}

.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px dashed #f0f0f0;
}

.no {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.items {
  background: #f9f9f9;
  border-radius: 8px;
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
  color: #666;
  font-size: 13px;
}

.name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 70%;
}

.qty {
  color: #999;
}

.meta {
  display: flex;
  justify-content: flex-end;
  align-items: baseline;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.price {
  color: var(--color-primary);
  font-size: 18px;
  font-weight: bold;
}

.foot {
  display: flex;
  justify-content: flex-end;
  padding-top: 4px;
}

.action-btn {
  padding: 8px 20px;
  font-weight: 600;
}

.verify-box {
  text-align: center;
  padding: 20px 0;
}

.verify-box .big {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.verify-box .code {
  font-size: 32px;
  font-weight: 800;
  letter-spacing: 6px;
  color: var(--color-primary);
  margin: 10px 0;
  font-family: monospace;
}

.verify-box .tips {
  color: #999;
  font-size: 12px;
}
</style>

