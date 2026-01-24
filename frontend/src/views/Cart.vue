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
.cart-page {
  padding-bottom: 120px; /* Space for checkout bar + tab bar */
  padding-left: 16px;
  padding-right: 16px;
  padding-top: 16px;
}

.list-card {
  /* Remove container card style, use transparent container */
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
  border-radius: var(--card-radius);
  background: white;
  align-items: center;
  transition: all .25s;
  box-shadow: var(--shadow-card);
}

.cart-item:active {
  transform: scale(0.98);
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
  color: var(--color-primary);
  font-weight: 700;
  font-size: 16px;
}

.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* Custom quantity control */
.qty-btn {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  border: 1px solid #ddd;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #666;
}

.qty-btn:active {
  background: #f5f5f5;
}

.qty-num {
  font-size: 14px;
  min-width: 20px;
  text-align: center;
  font-weight: 500;
}

.del-btn {
  margin-left: auto;
  color: #ff4d4f;
  font-size: 12px;
  padding: 4px 8px;
  background: #fff0f0;
  border-radius: 4px;
  cursor: pointer;
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
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 30px;
  box-shadow: var(--shadow-float);
  z-index: 900;
}

.summary {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.count-text {
  font-size: 12px;
  color: #999;
}

.sum {
  font-size: 14px;
  color: #333;
}

.sum b {
  font-size: 18px;
  color: var(--color-primary);
}

.checkout-btn {
  border-radius: 20px;
  padding: 0 24px;
  background: var(--color-primary);
  border: none;
  font-weight: 600;
  height: 40px;
}

.fade-leave-active, .fade-enter-active { transition: all .25s }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(10px) }
</style>

