<template>
  <div class="home-container">
    <div class="content-wrapper">
      <!-- 左侧分类栏 -->
      <aside class="category-sidebar">
        <div 
          class="category-item" 
          :class="{ active: currentCategoryId === 0 }"
          @click="currentCategoryId = 0"
        >
          <span class="category-name">全部商品</span>
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
        
        <el-row :gutter="20">
          <el-col 
            v-for="goods in filteredGoodsList" 
            :key="goods.id" 
            :xs="24" :sm="12" :md="8" :lg="6" :xl="6"
          >
            <div class="goods-card animate__animated animate__fadeIn">
              <div class="goods-img-box">
                <img 
                  :src="goods.imageUrl || 'https://via.placeholder.com/200?text=Snack'" 
                  class="goods-img" 
                  alt="goods"
                />
              </div>
              <div class="goods-info">
                <h3 class="goods-name" :title="goods.name">{{ goods.name }}</h3>
                <div class="goods-meta">
                  <span class="stock">库存: {{ goods.stock }}</span>
                  <span class="sales">销量: 99+</span>
                </div>
                <div class="goods-bottom">
                  <div class="price">
                    <span class="symbol">¥</span>
                    <span class="num">{{ goods.price }}</span>
                  </div>
                  <button class="add-btn" @click="addToCart(goods)">
                    <el-icon><Plus /></el-icon>
                  </button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const categoryList = ref([])
const goodsList = ref([])
const currentCategoryId = ref(0) // 0代表全部

// 计算属性：前端过滤商品
const filteredGoodsList = computed(() => {
  if (currentCategoryId.value === 0) {
    return goodsList.value
  }
  return goodsList.value.filter(g => g.categoryId === currentCategoryId.value)
})

onMounted(async () => {
  await fetchCategories()
  await fetchGoods()
})

const fetchCategories = async () => {
  try {
    const res = await request.get('/category/list')
    if (res.code === 1) {
      categoryList.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchGoods = async () => {
  try {
    const res = await request.get('/goods/list')
    if (res.code === 1) {
      goodsList.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const addToCart = async (goods) => {
  try {
    const res = await request.post('/cart/add', {
      goodsId: goods.id,
      quantity: 1
    })
    if (res.code === 1) {
      ElMessage.success('已加入购物车')
    }
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
}

.content-wrapper {
  display: flex;
  gap: 20px;
  height: 100%;
}

/* 左侧分类栏 */
.category-sidebar {
  width: 200px;
  background: white;
  border-radius: 12px;
  padding: 10px 0;
  height: fit-content;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
  position: sticky;
  top: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
}

.category-item {
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
  position: relative;
}

.category-item:hover {
  background-color: #f5f7fa;
  color: #ff8e53;
}

.category-item.active {
  background-color: #fff6f0;
  color: #ff8e53;
  font-weight: 600;
}

.category-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background-color: #ff8e53;
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
}

/* 右侧商品网格 */
.goods-grid {
  flex: 1;
}

.goods-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
  cursor: pointer;
}

.goods-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
  border-color: #ff8e53;
}

.goods-img-box {
  width: 100%;
  aspect-ratio: 1/1;
  overflow: hidden;
  background: #f9f9f9;
}

.goods-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.goods-card:hover .goods-img {
  transform: scale(1.05);
}

.goods-info {
  padding: 12px;
}

.goods-name {
  font-size: 15px;
  margin: 0 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333;
}

.goods-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
}

.goods-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ff4d4f;
  font-weight: bold;
}

.symbol {
  font-size: 12px;
  margin-right: 2px;
}

.num {
  font-size: 20px;
}

.add-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #ff8e53;
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.add-btn:hover {
  background: #ff6b6b;
  transform: scale(1.1);
}

.add-btn:active {
  transform: scale(0.9);
}

.empty-state {
  padding: 40px;
  text-align: center;
  background: white;
  border-radius: 12px;
}
</style>
