<template>
  <div class="home-container">
    <!-- 热门轮播图 -->
    <div class="banner-section">
      <el-carousel :interval="4000" type="card" height="150px" indicator-position="none">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item" :style="{ backgroundColor: item.color }">
            <span class="banner-text">{{ item.text }}</span>
            <span class="banner-emoji">{{ item.emoji }}</span>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="content-wrapper">
      <!-- 左侧分类栏 -->
      <aside class="category-sidebar">
        <div 
          class="category-item" 
          :class="{ active: currentCategoryId === 0 }"
          @click="currentCategoryId = 0"
        >
          <span class="category-name">全部</span>
        </div>
        <div 
          v-for="cate in categoryList" 
          :key="cate.id"
          class="category-item"
          :class="{ active: currentCategoryId === cate.id }"
          @click="currentCategoryId = cate.id"
        >
          <span class="category-name">{{ cate.name }}</span>
        </div>
      </aside>

      <!-- 右侧商品网格 -->
      <main class="goods-grid">
        <div v-if="filteredGoodsList.length === 0" class="empty-state">
          <el-empty description="暂无相关商品" />
        </div>
        
        <div class="goods-list">
          <div 
            v-for="goods in filteredGoodsList" 
            :key="goods.id" 
            class="goods-card animate__animated animate__fadeIn"
          >
            <div class="goods-img-box">
              <img 
                :src="goods.imageUrl || 'https://placehold.co/200x200/f5f5f5/ccc?text=Snack'" 
                class="goods-img" 
                alt="goods"
              />
            </div>
            <div class="goods-info">
              <h3 class="goods-name">{{ goods.name }}</h3>
              <div class="goods-meta">
                <span class="sales">月销 99+</span>
              </div>
              <div class="goods-bottom">
                <div class="price-bubble">
                  <span class="symbol">¥</span>
                  <span class="num">{{ goods.price }}</span>
                </div>
                <button class="add-btn" @click.stop="addToCart(goods)">
                  <el-icon><Plus /></el-icon>
                </button>
              </div>
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
import request from '../utils/request'
import { useCartStore } from '../stores/cart'

const cartStore = useCartStore()
const categoryList = ref([])
const goodsList = ref([])
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
    const res = await request.get('/goods/list')
    if (res.code === 1) goodsList.value = res.data
  } catch (e) { console.error(e) }
}

const addToCart = async (goods) => {
  try {
    const res = await request.post('/cart/add', { goodsId: goods.id, quantity: 1 })
    if (res.code === 1) {
      ElMessage.success('已加入购物车')
      cartStore.refresh()
    }
  } catch (e) { console.error(e) }
}
</script>

<style scoped>
.home-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.banner-section {
  padding: 10px 16px;
  background: white;
  margin-bottom: 10px;
}

.banner-item {
  height: 100%;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
  position: relative;
  overflow: hidden;
}

.banner-emoji {
  font-size: 48px;
  margin-left: 10px;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

.content-wrapper {
  flex: 1;
  display: flex;
  overflow: hidden;
  background: white;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  box-shadow: 0 -4px 16px rgba(0,0,0,0.05);
}

/* Sidebar */
.category-sidebar {
  width: 86px;
  background: #f8f8f8;
  overflow-y: auto;
  padding-bottom: 20px;
}

.category-item {
  padding: 16px 8px;
  text-align: center;
  font-size: 13px;
  color: #666;
  position: relative;
  transition: all 0.3s;
}

.category-item.active {
  background: white;
  color: var(--color-primary);
  font-weight: 600;
}

.category-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  height: 16px;
  width: 4px;
  background: var(--color-primary);
  border-radius: 0 2px 2px 0;
}

/* Goods Grid */
.goods-grid {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  background: white;
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}

.goods-card {
  background: white;
  border-radius: var(--card-radius);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  transition: transform 0.2s;
  display: flex;
  flex-direction: column;
}

.goods-card:active {
  transform: scale(0.98);
}

.goods-img-box {
  width: 100%;
  aspect-ratio: 1;
  background: #f9f9f9;
}

.goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  padding: 10px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.goods-name {
  font-size: 14px;
  margin: 0 0 4px;
  line-height: 1.4;
  height: 40px; /* 2 lines */
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.goods-meta {
  font-size: 10px;
  color: #999;
  margin-bottom: 8px;
}

.goods-bottom {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-bubble {
  background: #fff0f0;
  color: var(--color-primary);
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: bold;
}

.symbol {
  font-size: 10px;
  margin-right: 1px;
}

.num {
  font-size: 16px;
}

.add-btn {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--color-primary);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.4);
}
</style>
