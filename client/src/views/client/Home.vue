<template>
  <div class="mobile-shop-container">
    <header class="shop-header">
      <div class="header-content">
        <p class="brand-kicker">LEYI SNACK</p>
        <h1 class="brand-title">今日零食补给</h1>
      </div>
      <div class="search-bar-box">
        <el-input prefix-icon="Search" placeholder="搜索美味零食..." class="round-search" />
      </div>
    </header>

    <section class="category-strip">
      <div class="category-track">
        <div
          class="category-pill"
          :class="{ active: currentCategory1Id === 0 }"
          @click="onCategory1Change(0)"
        >
          全部
        </div>
        <div
          v-for="cate in category1List"
          :key="cate.id"
          class="category-pill"
          :class="{ active: currentCategory1Id === cate.id }"
          @click="onCategory1Change(cate.id)"
        >
          {{ cate.name }}
        </div>
      </div>
    </section>

    <section class="subcategory-strip" v-if="currentCategory1Id !== 0 && category2List.length > 0">
      <div class="subcategory-track">
        <button
          type="button"
          class="subcategory-chip"
          :class="{ active: currentCategory2Id === 0 }"
          @click="onCategory2Change(0)"
        >
          全部
        </button>
        <button
          v-for="sub in category2List"
          :key="sub.id"
          type="button"
          class="subcategory-chip"
          :class="{ active: currentCategory2Id === sub.id }"
          @click="onCategory2Change(sub.id)"
        >
          {{ sub.name }}
        </button>
      </div>
    </section>

    <section class="spotlight-zone" v-if="spotlightGoods.length">
      <div class="spotlight-head">
        <p class="spotlight-title">今日亮点</p>
        <div class="spotlight-tabs">
          <button
            v-for="tab in spotlightTabs"
            :key="tab.key"
            type="button"
            class="spotlight-tab"
            :class="{ active: activeSpotlight === tab.key }"
            @click="activeSpotlight = tab.key"
          >
            {{ tab.label }}
          </button>
        </div>
      </div>

      <div class="spotlight-track">
        <article
          v-for="goods in spotlightGoods"
          :key="'spot-' + goods.id"
          class="spotlight-card"
          @click="openGoodsDetail(goods)"
        >
          <div class="spotlight-thumb">
            <img :src="goods.imageUrl || 'https://placehold.co/200x200/f5f5f5/ccc?text=Snack'" loading="lazy" />
          </div>
          <div class="spotlight-info">
            <h4 class="spotlight-name">{{ goods.name }}</h4>
            <p class="spotlight-price">￥{{ goods.price }}</p>
          </div>
          <button type="button" class="spotlight-add" @click.stop="addToCart(goods)">
            <el-icon><Plus /></el-icon>
          </button>
        </article>
      </div>
    </section>

    <main class="home-feed">
      <div v-if="filteredGoodsList.length === 0" class="empty-state">
        <el-empty description="暂无相关商品" />
      </div>

      <div class="goods-list" v-if="!loading">
        <div
          v-for="goods in filteredGoodsList"
          :key="goods.id"
          class="goods-card card animate__animated animate__fadeIn"
          @click="openGoodsDetail(goods)"
        >
          <div class="img-box">
            <img
              :src="goods.imageUrl || 'https://placehold.co/200x200/f5f5f5/ccc?text=Snack'"
              loading="lazy"
            />
          </div>
          <div class="info-box">
            <h3 class="goods-name">{{ goods.name }}</h3>
            <div class="goods-meta">销量 99+</div>
            <div class="goods-bottom">
              <span class="price">¥<span class="price-num">{{ goods.price }}</span></span>
              <div class="add-btn" @click.stop="addToCart(goods)">
                <el-icon><Plus /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="goods-list">
        <div v-for="n in 6" :key="n" class="goods-card skeleton">
          <div class="img-box skeleton-bg"></div>
          <div class="info-box">
            <div class="skeleton-line short"></div>
            <div class="skeleton-line long"></div>
            <div class="skeleton-line price"></div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import { useCartStore } from '../../stores/cart'

const cartStore = useCartStore()
const router = useRouter()
const category1List = ref([])
const category2List = ref([])
const allCategories = ref([])
const goodsList = ref([])
const loading = ref(false)
const currentCategory1Id = ref(0)
const currentCategory2Id = ref(0)
const activeSpotlight = ref('recommended')

const spotlightTabs = [
  { key: 'recommended', label: '今日推荐' },
  { key: 'hot', label: '爆款' },
  { key: 'new', label: '新品' }
]

const parseSalesScore = (goods) => {
  const keys = ['sales', 'saleCount', 'sold', 'salesVolume', 'sellCount']
  for (const key of keys) {
    const value = Number(goods?.[key])
    if (!Number.isNaN(value) && value > 0) return value
  }
  return 0
}

const normalizedGoods = computed(() => goodsList.value.filter(item => item && item.id !== undefined && item.id !== null))

const spotlightPools = computed(() => {
  const source = normalizedGoods.value
  const recommended = source.slice(0, 10)
  const hot = [...source]
    .sort((a, b) => parseSalesScore(b) - parseSalesScore(a) || Number(b.id || 0) - Number(a.id || 0))
    .slice(0, 10)
  const newest = [...source]
    .sort((a, b) => Number(b.id || 0) - Number(a.id || 0))
    .slice(0, 10)
  return { recommended, hot, new: newest }
})

const spotlightGoods = computed(() => spotlightPools.value[activeSpotlight.value] || spotlightPools.value.recommended)

const filteredGoodsList = computed(() => goodsList.value)

onMounted(async () => {
  await fetchCategory1()
  await fetchGoods()
})

const fetchCategory1 = async () => {
  try {
    const res = await request.get('/category1', { silentError: true })
    if (res.code === 1) {
      category1List.value = res.data || []
      return
    }
  } catch (e) {
    console.warn('category1 endpoint fallback to legacy list:', e?.message || e)
  }

  const all = await fetchAllCategoriesLegacy()
  category1List.value = all.filter(c => Number(c.parentId || 0) === 0)
}

const fetchCategory2 = async (parentId) => {
  if (!parentId || Number(parentId) === 0) {
    category2List.value = []
    return
  }

  try {
    const res = await request.get('/category2', { params: { parentId }, silentError: true })
    if (res.code === 1) {
      category2List.value = res.data || []
      return
    }
  } catch (e) {
    console.warn('category2 endpoint fallback to legacy list:', e?.message || e)
  }

  const all = await fetchAllCategoriesLegacy()
  category2List.value = all.filter(c => Number(c.parentId || 0) === Number(parentId))
}

const fetchGoods = async () => {
  loading.value = true
  try {
    try {
      const params = {}
      if (currentCategory1Id.value) params.category1Id = currentCategory1Id.value
      if (currentCategory2Id.value) params.category2Id = currentCategory2Id.value

      const res = await request.get('/goods', { params, silentError: true })
      if (res.code === 1) {
        goodsList.value = res.data || []
        return
      }
    } catch (e) {
      console.warn('goods filter endpoint fallback to legacy list:', e?.message || e)
    }

    try {
      const legacy = await request.get('/goods/list', { silentError: true })
      if (legacy.code === 1) {
        goodsList.value = filterGoodsByCurrentCategory(legacy.data || [])
      } else {
        goodsList.value = []
      }
    } catch (e) {
      console.error(e)
      goodsList.value = []
    }
  } finally {
    loading.value = false
  }
}

const fetchAllCategoriesLegacy = async () => {
  if (allCategories.value.length > 0) return allCategories.value
  try {
    const res = await request.get('/category/list', { silentError: true })
    if (res.code === 1) {
      allCategories.value = res.data || []
      return allCategories.value
    }
  } catch (e) {
    console.warn('legacy category/list request failed:', e?.message || e)
  }
  return []
}

const filterGoodsByCurrentCategory = (rawGoods) => {
  if (!Array.isArray(rawGoods)) return []
  if (currentCategory2Id.value) {
    return rawGoods.filter(item => Number(item.categoryId) === Number(currentCategory2Id.value))
  }
  if (currentCategory1Id.value) {
    const childIds = category2List.value.map(item => Number(item.id))
    if (childIds.length > 0) {
      return rawGoods.filter(item => childIds.includes(Number(item.categoryId)))
    }
    return rawGoods.filter(item => Number(item.categoryId) === Number(currentCategory1Id.value))
  }
  return rawGoods
}

const onCategory1Change = async (id) => {
  currentCategory1Id.value = Number(id) || 0
  currentCategory2Id.value = 0

  await fetchCategory2(currentCategory1Id.value)
  await fetchGoods()
}

const onCategory2Change = async (id) => {
  currentCategory2Id.value = Number(id) || 0
  await fetchGoods()
}

const addToCart = async (goods) => {
  try {
    const gid = Number(goods?.id)
    if (!gid || Number.isNaN(gid)) {
      ElMessage.error(`商品ID缺失：${goods?.name || ''}`)
      return
    }
    const res = await request.post('/cart/add', { goodsId: gid, quantity: 1 })
    if (res.code === 1) {
      ElMessage.success('已加入购物车')
      cartStore.refresh()
    } else {
      ElMessage.error(res.message || '加购失败，请稍后重试')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('网络连接异常，请检查网络')
  }
}

const openGoodsDetail = (goods) => {
  if (!goods?.id) return
  router.push({
    name: 'GoodsDetail',
    params: { id: goods.id },
    query: {
      name: goods.name || '',
      price: goods.price ?? '',
      img: goods.imageUrl || '',
      desc: goods.description || '',
      stock: goods.stock ?? ''
    }
  })
}
</script>

<style scoped>
.mobile-shop-container {
  min-height: 100%;
  padding: 10px 12px 14px;
  background: var(--color-bg);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.shop-header {
  background: #fff;
  border: var(--border-strong);
  box-shadow: var(--shadow-card);
  border-radius: var(--radius-card);
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.header-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-kicker {
  margin: 0;
  font-size: 11px;
  letter-spacing: 0.6px;
  text-transform: uppercase;
  color: rgba(0, 0, 0, 0.55);
  font-weight: 700;
}

.brand-title {
  margin: 0;
  font-size: 22px;
  line-height: 1.15;
  color: var(--color-text);
  font-weight: 900;
}

.search-bar-box :deep(.el-input__wrapper) {
  border-radius: var(--radius-pill);
  border: var(--border-strong);
  box-shadow: none;
  background: #fff;
}

.search-bar-box :deep(.el-input__inner) {
  font-size: 14px;
}

.category-strip {
  position: sticky;
  top: 0;
  z-index: 6;
  background: var(--color-bg);
  padding: 2px 0 4px;
}

.category-track {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 2px;
  scrollbar-width: none;
}

.category-track::-webkit-scrollbar {
  display: none;
}

.subcategory-strip {
  margin-top: -2px;
}

.subcategory-track {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 2px;
  scrollbar-width: none;
}

.subcategory-track::-webkit-scrollbar {
  display: none;
}

.subcategory-chip {
  flex: 0 0 auto;
  white-space: nowrap;
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  background: #fff;
  color: rgba(0, 0, 0, 0.64);
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
  padding: 6px 12px;
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.82);
}

.subcategory-chip.active {
  background: var(--color-accent);
  color: #111;
}

.subcategory-chip:active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.8);
}

.category-pill {
  flex: 0 0 auto;
  white-space: nowrap;
  padding: 8px 14px;
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  background: #fff;
  box-shadow: 0 3px 0 rgba(0, 0, 0, 0.9);
  font-size: 13px;
  font-weight: 700;
  color: rgba(0, 0, 0, 0.72);
  line-height: 1;
  user-select: none;
  cursor: pointer;
  transition: transform 0.12s ease, box-shadow 0.12s ease, background 0.12s ease, color 0.12s ease;
}

.category-pill.active {
  background: var(--color-primary);
  color: #111;
  box-shadow: 0 4px 0 rgba(0, 0, 0, 0.95);
}

.category-pill:active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.9);
}

.spotlight-zone {
  background: #fff;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-float);
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.spotlight-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.spotlight-title {
  margin: 0;
  font-size: 15px;
  font-weight: 900;
  color: var(--color-text);
}

.spotlight-tabs {
  display: flex;
  gap: 6px;
}

.spotlight-tab {
  border: var(--border-strong);
  background: #fff;
  border-radius: var(--radius-pill);
  font-size: 11px;
  font-weight: 700;
  color: rgba(0, 0, 0, 0.65);
  padding: 4px 10px;
  line-height: 1;
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.85);
  transition: transform 0.12s ease, box-shadow 0.12s ease, background 0.12s ease, color 0.12s ease;
}

.spotlight-tab.active {
  background: var(--color-primary);
  color: #111;
}

.spotlight-tab:active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.8);
}

.spotlight-track {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding-bottom: 2px;
  scrollbar-width: none;
}

.spotlight-track::-webkit-scrollbar {
  display: none;
}

.spotlight-card {
  flex: 0 0 76%;
  display: flex;
  align-items: center;
  gap: 10px;
  background: #fffbe6;
  border: var(--border-strong);
  border-radius: calc(var(--radius-card) - 2px);
  box-shadow: 0 4px 0 rgba(0, 0, 0, 0.9);
  padding: 8px;
  position: relative;
  cursor: pointer;
}

.spotlight-card:active {
  transform: translateY(1px) scale(0.99);
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.85);
}

.spotlight-thumb {
  width: 62px;
  height: 62px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  border: var(--border-strong);
  background: #fff;
}

.spotlight-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spotlight-info {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.spotlight-name {
  margin: 0;
  font-size: 13px;
  line-height: 1.35;
  font-weight: 800;
  color: var(--color-text);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.spotlight-price {
  margin: 0;
  font-size: 17px;
  font-weight: 900;
  color: var(--color-accent);
}

.spotlight-add {
  width: 30px;
  height: 30px;
  border-radius: var(--radius-pill);
  border: var(--border-strong);
  background: var(--color-primary);
  color: #111;
  box-shadow: 0 3px 0 rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.spotlight-add:active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.8);
}

.home-feed {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.empty-state {
  padding: 30px 12px;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  background: #fff;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-bottom: 8px;
}

.goods-card {
  background: #fff;
  border-radius: var(--radius-card);
  border: var(--border-strong);
  padding: 10px;
  display: flex;
  gap: 10px;
  box-shadow: 0 3px 0 rgba(0, 0, 0, 0.86);
  transition: transform 0.12s ease, box-shadow 0.12s ease;
  position: relative;
  min-height: 96px;
  cursor: pointer;
}

.goods-card:active {
  transform: translateY(1px) scale(0.99);
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.82);
}

.img-box {
  width: 82px;
  height: 82px;
  flex-shrink: 0;
  border-radius: calc(var(--radius-card) - 4px);
  border: var(--border-strong);
  overflow: hidden;
  background: #fff;
}

.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-box {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 8px;
}

.goods-name {
  margin: 0;
  font-size: 14px;
  line-height: 1.35;
  font-weight: 800;
  color: var(--color-text);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-meta {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.55);
}

.goods-bottom {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding-right: 44px;
}

.price {
  color: var(--color-accent);
  font-size: 13px;
  font-weight: 800;
  line-height: 1;
}

.price-num {
  font-size: 22px;
  font-weight: 900;
  margin-left: 2px;
}

.add-btn {
  width: 34px;
  height: 34px;
  border-radius: var(--radius-pill);
  background: var(--color-primary);
  border: var(--border-strong);
  color: #111;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 0 rgba(0, 0, 0, 0.9);
  transition: transform 0.12s ease, box-shadow 0.12s ease, filter 0.12s ease;
  cursor: pointer;
  position: absolute;
  right: 10px;
  bottom: 10px;
}

.add-btn :deep(.el-icon) {
  font-size: 18px;
  font-weight: 900;
}

.add-btn:active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.9);
  filter: brightness(0.96);
}

.skeleton .skeleton-bg {
  background: #efefef;
}

.skeleton .skeleton-line {
  height: 11px;
  background: #efefef;
  border-radius: var(--radius-pill);
  margin: 6px 0;
}

.skeleton .skeleton-line.short {
  width: 62%;
}

.skeleton .skeleton-line.long {
  width: 90%;
}

.skeleton .skeleton-line.price {
  width: 42%;
}
</style>
