<template>
  <div class="order-page">
    <div class="cards">
      <el-empty v-if="orders.length === 0" description="暂无订单" />
      <el-row v-else :gutter="16">
        <el-col :xs="24" :sm="12" :md="8" v-for="o in orders" :key="o.id">
          <div class="order-card">
            <div class="head">
              <div class="no">订单号：{{ o.orderNo }}</div>
              <el-tag :type="o.status === 0 ? 'warning' : 'success'">{{ o.status === 0 ? '待核销' : '已完成' }}</el-tag>
            </div>
            <div class="body">
              <div class="meta">
                <span>金额</span>
                <b class="price">¥ {{ Number(o.totalAmount || 0).toFixed(2) }}</b>
              </div>
              <div class="items">
                <el-skeleton v-if="!details[o.id]" rows="2" animated />
                <ul v-else>
                  <li v-for="it in details[o.id]" :key="it.id" class="item">
                    <span class="name">{{ it.goodsName }}</span>
                    <span class="qty">x{{ it.quantity }}</span>
                  </li>
                </ul>
              </div>
            </div>
            <div class="foot">
              <el-button v-if="o.status===0" type="primary" plain @click="showVerify(o)">出示核销码</el-button>
              <el-button v-else type="primary" plain @click="goComment(o.id)">去评价</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-dialog v-model="verifyVisible" width="420px" :show-close="true">
      <div class="verify-box">
        <div class="big">核销码</div>
        <div class="code">{{ currentOrder?.verifyCode || '******' }}</div>
        <div class="tips">请向店长出示此核销码完成取货</div>
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
.order-page { }
.cards { }
.order-card { background:#fff; border-radius:12px; box-shadow:0 8px 24px rgba(0,0,0,.06); padding:14px; display:flex; flex-direction:column; gap:10px }
.head { display:flex; justify-content:space-between; align-items:center }
.no { font-weight:600; color:#333 }
.body { display:flex; flex-direction:column; gap:8px }
.meta { display:flex; justify-content:space-between; align-items:baseline }
.price { color:#ff6b6b; font-size:18px }
.items ul { list-style:none; margin:0; padding:0; display:flex; flex-direction:column; gap:6px }
.item { display:flex; justify-content:space-between; color:#666; background:#fafafa; padding:8px 10px; border-radius:8px }
.name { overflow:hidden; text-overflow:ellipsis; white-space:nowrap; max-width:70% }
.qty { color:#999 }
.foot { display:flex; justify-content:flex-end }
.verify-box { text-align:center; padding:12px }
.verify-box .big { font-size:14px; color:#666 }
.verify-box .code { font-size:28px; font-weight:800; letter-spacing:4px; margin:8px 0 }
.verify-box .tips { color:#999 }
</style>

