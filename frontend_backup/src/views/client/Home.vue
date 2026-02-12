<template>
  <div class="mobile-shop-container">
    <div class="shop-header">
      <div class="header-content">
        <div class="brand-title">🍬 乐逸零食</div>
        <div class="brand-subtitle">李先生的小店</div>
      </div>
      <div class="search-bar-box">
        <el-input prefix-icon="Search" placeholder="搜索美味零食..." class="round-search" />
      </div>
    </div>

    <div class="shop-body">
      <aside class="category-sidebar home-sidebar">
        <div 
          class="sidebar-item" 
          :class="{ active: currentCategoryId === 0 }"
          @click="currentCategoryId = 0"
        >
          <span class="item-text">全部</span>
        </div>
        <div 
          v-for="cate in categoryList" 
          :key="cate.id"
          class="sidebar-item"
          :class="{ active: currentCategoryId === cate.id }"
          @click="currentCategoryId = cate.id"
        >
          <span class="item-text">{{ cate.name }}</span>
        </div>
      </aside>

      <main class="product-area home-content">
        <div class="banner-box">
          <el-carousel :interval="4000" height="160px" arrow="never" indicator-position="none">
            <el-carousel-item v-for="item in banners" :key="item.id">
              <div class="banner-item" :style="{ backgroundColor: item.color }">
                <span class="banner-text">{{ item.text }}</span>
                <span class="banner-emoji">{{ item.emoji }}</span>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
        
        

        <div v-if="filteredGoodsList.length === 0" class="empty-state">
          <el-empty description="暂无相关商品" />
        </div>

        <div class="goods-list" v-if="!loading">
          <div 
            v-for="goods in filteredGoodsList" 
            :key="goods.id" 
            class="goods-card card animate__animated animate__fadeIn"
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import { useCartStore } from '../../stores/cart'

const cartStore = useCartStore()
const categoryList = ref([])
const goodsList = ref([])
const loading = ref(false)
const currentCategoryId = ref(0)

const banners = [
  { id: 1, color: '#ff9a9e', text: '今日特惠', emoji: '🍬' },
  { id: 2, color: '#a18cd1', text: '新品上市', emoji: '🍪' },
  { id: 3, color: '#fad0c4', text: '人气推荐', emoji: '🍩' }
]

// ... (existing logic)
const filteredGoodsList = computed(() => {
  if (currentCategoryId.value === 0) return goodsList.value
  return goodsList.value.filter(g => g.categoryId === currentCategoryId.value)
})

onMounted(async () => {
  await fetchCategories()
  await fetchGoods()
})

const fetchCategories = async () => {
  try {
    const res = await request.get('/category/list')
    if (res.code === 1) categoryList.value = res.data
  } catch (e) { console.error(e) }
}

const fetchGoods = async () => {
  try {
    loading.value = true
    const res = await request.get('/goods/list')
    if (res.code === 1) goodsList.value = res.data
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

// 收藏功能回退（不保留）

const addToCart = async (goods) => {
  console.log('addToCart:', goods)
  try {
    const gid = Number(goods?.id)
    if (!gid || Number.isNaN(gid)) {
      ElMessage.error(`商品ID缺失：${goods?.name || ''}`)
      return
    }
    const res = await request.post('/cart/add', { goodsId: gid, quantity: 1 })
    console.log('addToCart res:', res)
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
</script>

<style scoped>
/* 1. 容器与背景 (Global) */
.mobile-shop-container {
  height: 100vh; /* 视口高度 */
  display: flex;
  flex-direction: column;
  background-color: #f5f6fa; /* 防止大白屏 */
}

.shop-body {
  flex: 1; /* 占满剩余空间 */
  display: flex;
  overflow: hidden; /* 关键：防止整体滚动 */
  position: relative;
}

/* 2. 顶部品牌栏 (.shop-header) */
.shop-header {
  background: linear-gradient(135deg, #ff9f43 0%, #ff6b6b 100%);
  height: 130px;
  border-bottom-left-radius: 24px;
  border-bottom-right-radius: 24px;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex-shrink: 0;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
}

.header-content {
  margin-top: 10px;
}

.brand-title {
  font-size: 26px;
  font-weight: 900;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 4px;
}

.brand-subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* 搜索框优化 */
.search-bar-box :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  border: none;
}

.search-bar-box :deep(.el-input__inner) {
  color: #333;
}

/* 3. 左侧导航 (.category-sidebar) */
.home-sidebar {
  width: 30%;
  min-width: 140px;
  background-color: #f7f8fa;
  overflow-y: auto;
  padding-bottom: 20px;
}

/* 隐藏滚动条 */
.home-sidebar::-webkit-scrollbar,
.home-content::-webkit-scrollbar {
  display: none;
}

.sidebar-item {
  padding: 12px 14px;
  text-align: left;
  font-size: 14px;
  color: #666;
  position: relative;
  transition: all 0.2s;
  cursor: pointer;
  border-radius: 99px;
  margin: 8px 12px;
  background: rgba(255,255,255,0.6);
  display: flex;
  align-items: center;
  min-height: 46px;
}

.sidebar-item.active {
  background-color: #ffffff;
  color: #ff6b6b;
  font-weight: 800;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.sidebar-item.active::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  height: 10px;
  width: 10px;
  background-color: #ff6b6b;
  border-radius: 50%;
}
.item-text {
  width: 100%;
  margin-left: 18px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 4. 右侧商品区 (.product-area) */
.home-content {
  width: 70%;
  background-color: #ffffff;
  overflow-y: auto;
  padding: 12px;
}

/* 轮播图美化 */
.banner-box {
  margin-bottom: 16px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
}

.banner-emoji {
  font-size: 48px;
  margin-left: 10px;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

/* 分类横向滚动双排 */
/* 分类横向滚动双排回退 */

/* 商品列表 */
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-bottom: 20px;
  background: #fff;
  border-radius: 16px;
}
.goods-card {
  background: #fff;
  border-radius: 16px;
  padding: 12px;
  display: flex;
  gap: 12px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.08);
  transition: transform 0.1s;
  position: relative;
}

.goods-card:active {
  transform: scale(0.98);
}

.img-box {
  width: 100px;
  height: 100px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  background: #f9f9f9;
  position: relative;
}

.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 收藏按钮回退 */

.info-box {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 4px 0;
}

.goods-name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-meta {
  font-size: 11px;
  color: #999;
}

.goods-bottom {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding-right: 56px;
}

.price {
  color: #ff6b6b;
  font-size: 12px;
  font-weight: bold;
}

.price-num {
  font-size: 20px;
  margin-left: 2px;
}

.add-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9f43 0%, #ff6b6b 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 12px rgba(255, 107, 107, 0.35);
  cursor: pointer;
  position: absolute;
  right: 12px;
  bottom: 12px;
}

.add-btn:active {
  transform: scale(0.9);
}

.empty-state {
  padding: 40px;
  text-align: center;
}

/* Skeleton */
.skeleton .skeleton-bg { background: #eee; }
.skeleton .skeleton-line { height: 12px; background: #eee; border-radius: 6px; margin: 8px 0; }
.skeleton .skeleton-line.short { width: 60%; }
.skeleton .skeleton-line.long { width: 90%; }
.skeleton .skeleton-line.price { width: 40%; }
</style>
