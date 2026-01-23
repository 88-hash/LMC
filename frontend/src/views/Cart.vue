<template>
  <div class="cart-page">
    <div class="list-card">
      <el-empty v-if="items.length === 0" description="购物车空空如也" />
      <div v-else class="cart-list">
        <transition-group name="fade">
          <div v-for="item in items" :key="item.id" class="cart-item">
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
      <div class="summary">
        <span>共 {{ totalCount }} 件</span>
        <span class="sum">合计：<b>¥ {{ totalAmount }}</b></span>
      </div>
      <el-button type="primary" size="large" :loading="loading" class="checkout-btn" @click="checkout">立即结算</el-button>
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

const totalAmount = computed(() => {
  return items.value.reduce((sum, i) => sum + Number(i.price || 0) * Number(i.quantity || 0), 0).toFixed(2)
})
const totalCount = computed(() => items.value.reduce((sum, i) => sum + Number(i.quantity || 0), 0))

const load = async () => {
  const res = await request.get('/cart/list')
  if (res.code === 1) {
    items.value = res.data || []
    store.setCount(totalCount.value)
  }
}

const onChange = async (item) => {
  const res = await request.post('/cart/update', { id: item.id, quantity: item.quantity })
  if (res.code === 1) {
    store.setCount(totalCount.value)
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
    store.setCount(totalCount.value)
    ElMessage.success('已删除')
  }
}

const checkout = () => {
  if (items.value.length === 0) return ElMessage.warning('请先选购商品')
  router.push('/confirm')
}

onMounted(load)
</script>

<style scoped>
.cart-page { padding-bottom: 90px }
.list-card { background: #fff; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,.06); padding: 12px }
.cart-list { display: flex; flex-direction: column; gap: 12px }
.cart-item { display: flex; gap: 14px; padding: 12px; border-radius: 12px; background: #fafafa; align-items: center; transition: all .25s }
.cart-item:hover { box-shadow: 0 4px 14px rgba(0,0,0,.08); transform: translateY(-2px) }
.thumb { width: 90px; height: 90px; border-radius: 10px; object-fit: cover; background:#f2f2f2 }
.info { flex: 1; display: flex; flex-direction: column; gap: 8px }
.title { font-weight: 600; color: #333 }
.meta { color: #999 }
.price { color: #ff6b6b; font-weight: 700 }
.actions { display: flex; align-items: center; gap: 10px }
.checkout-bar { position: fixed; left: 0; right: 0; bottom: 0; height: 70px; display: flex; align-items: center; justify-content: space-between; padding: 0 20px; background: rgba(255,255,255,.7); backdrop-filter: saturate(180%) blur(16px); border-top: 1px solid rgba(255,255,255,.6) }
.summary { display: flex; align-items: baseline; gap: 16px; color: #666 }
.summary .sum { font-size: 16px }
.checkout-btn { border-radius: 999px; padding: 0 28px }
.fade-leave-active, .fade-enter-active { transition: all .25s }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(6px) }
</style>

