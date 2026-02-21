<template>
  <div class="goods-detail-page">
    <section class="hero-card">
      <button type="button" class="back-btn" @click="goBack">← 返回</button>
      <div class="hero-image-wrap">
        <img :src="displayImage" :alt="displayName" class="hero-image" />
      </div>
    </section>

    <section class="info-card">
      <h1 class="goods-name">{{ displayName }}</h1>
      <p class="goods-price">￥{{ displayPrice }}</p>

      <div class="meta-row">
        <span v-if="salesValue !== null" class="meta-chip">销量 {{ salesValue }}</span>
        <span v-if="stockValue !== null" class="meta-chip">库存 {{ stockValue }}</span>
        <span class="meta-chip">品质零食</span>
      </div>

      <p v-if="displayDescription" class="goods-desc">{{ displayDescription }}</p>
      <p v-else class="goods-desc placeholder">这款零食口感扎实，适合追剧和办公室补给。</p>
    </section>

    <section class="action-bar">
      <button type="button" class="btn ghost" @click="continueBrowse">继续逛逛</button>
      <button type="button" class="btn primary" @click="addCurrentToCart">加入购物车</button>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import { useCartStore } from '../../stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const fallbackGoods = ref(buildFallbackFromRoute())
const goods = ref({ ...fallbackGoods.value })
const hasFallbackData = computed(() => {
  return Boolean(fallbackGoods.value?.name || fallbackGoods.value?.price || fallbackGoods.value?.imageUrl)
})

const displayName = computed(() => goods.value?.name || fallbackGoods.value?.name || '零食商品')
const displayPrice = computed(() => goods.value?.price ?? fallbackGoods.value?.price ?? '--')
const displayImage = computed(() => goods.value?.imageUrl || fallbackGoods.value?.imageUrl || 'https://placehold.co/640x420/f5f5f5/ccc?text=Snack')
const displayDescription = computed(() => goods.value?.description || fallbackGoods.value?.description || '')

const salesValue = computed(() => {
  const source = goods.value || {}
  const keys = ['sales', 'saleCount', 'sold', 'salesVolume', 'sellCount']
  for (const key of keys) {
    const value = Number(source[key])
    if (!Number.isNaN(value) && value >= 0) return value
  }
  return null
})

const stockValue = computed(() => {
  const value = Number(goods.value?.stock)
  if (Number.isNaN(value)) return null
  return value
})

onMounted(async () => {
  await fetchGoodsDetail()
})

async function fetchGoodsDetail() {
  const id = Number(route.params.id)
  if (!id || Number.isNaN(id)) {
    if (!hasFallbackData.value) {
      ElMessage.error('商品信息加载失败')
    }
    return
  }

  try {
    const res = await request.get(`/goods/${id}`, { silentError: true })
    if (res?.data) {
      goods.value = { ...fallbackGoods.value, ...res.data }
    }
  } catch (e) {
    console.error(e)
    // keep fallback data from route query to avoid blank detail page
    goods.value = { ...fallbackGoods.value }
    if (!hasFallbackData.value) {
      ElMessage.error('商品信息加载失败')
    }
  }
}

function buildFallbackFromRoute() {
  return {
    id: Number(route.params.id) || null,
    name: route.query?.name || '',
    price: route.query?.price || '',
    imageUrl: route.query?.img || '',
    description: route.query?.desc || '',
    stock: parseNullableNumber(route.query?.stock),
    sales: parseNullableNumber(route.query?.sales)
  }
}

function parseNullableNumber(value) {
  const num = Number(value)
  return Number.isNaN(num) ? null : num
}

function goBack() {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/home')
  }
}

function continueBrowse() {
  router.push('/home')
}

async function addCurrentToCart() {
  const goodsId = Number(goods.value?.id || route.params.id)
  if (!goodsId || Number.isNaN(goodsId)) {
    ElMessage.error('商品信息缺失，无法加入购物车')
    return
  }

  try {
    const res = await request.post('/cart/add', { goodsId, quantity: 1 })
    if (res.code === 1) {
      ElMessage.success('已加入购物车')
      cartStore.refresh()
    } else {
      ElMessage.error(res.message || '加购失败，请稍后重试')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('网络连接异常，请稍后重试')
  }
}
</script>

<style scoped>
.goods-detail-page {
  min-height: 100%;
  background: var(--color-bg);
  padding: 10px 12px calc(72px + env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hero-card {
  background: #fff;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-float);
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.back-btn {
  width: fit-content;
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  background: #fff;
  color: var(--color-text);
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
  padding: 7px 12px;
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.85);
}

.back-btn:active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.85);
}

.hero-image-wrap {
  border: var(--border-strong);
  border-radius: calc(var(--radius-card) - 4px);
  overflow: hidden;
  background: #fff;
  aspect-ratio: 1.2 / 1;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.info-card {
  background: #fff;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  box-shadow: 0 3px 0 rgba(0, 0, 0, 0.85);
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.goods-name {
  margin: 0;
  color: var(--color-text);
  font-size: 20px;
  line-height: 1.3;
  font-weight: 900;
}

.goods-price {
  margin: 0;
  color: var(--color-accent);
  font-size: 30px;
  line-height: 1;
  font-weight: 900;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.meta-chip {
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  padding: 5px 10px;
  background: #fffbe6;
  color: rgba(0, 0, 0, 0.7);
  font-size: 11px;
  line-height: 1;
  font-weight: 700;
}

.goods-desc {
  margin: 0;
  color: rgba(0, 0, 0, 0.76);
  font-size: 14px;
  line-height: 1.55;
}

.goods-desc.placeholder {
  color: rgba(0, 0, 0, 0.58);
}

.action-bar {
  position: sticky;
  bottom: calc(10px + env(safe-area-inset-bottom));
  z-index: 5;
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 10px;
  background: #fff;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  padding: 8px;
}

.btn {
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  min-height: 42px;
  font-size: 14px;
  font-weight: 800;
  box-shadow: 0 3px 0 rgba(0, 0, 0, 0.88);
}

.btn.ghost {
  background: #fff;
  color: var(--color-text);
}

.btn.primary {
  background: var(--color-primary);
  color: #111;
}

.btn:active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.88);
}
</style>
