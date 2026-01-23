<template>
  <div class="confirm-container">
    <div class="header">
      <el-page-header @back="$router.go(-1)" content="确认订单" />
    </div>

    <div class="content">
      <!-- 1. 收货方式 (静态) -->
      <div class="section-card animate__animated animate__fadeInUp">
        <h3 class="section-title">收货方式</h3>
        <div class="delivery-box active">
          <el-icon class="icon"><Shop /></el-icon>
          <div class="info">
            <div class="title">门店自提</div>
            <div class="desc">乐逸零食旗舰店 (营业时间: 09:00-22:00)</div>
          </div>
          <el-icon class="check"><Select /></el-icon>
        </div>
      </div>

      <!-- 2. 商品清单 -->
      <div class="section-card animate__animated animate__fadeInUp" style="animation-delay: 0.1s">
        <h3 class="section-title">商品清单</h3>
        <div class="goods-list">
          <div v-for="item in cartList" :key="item.id" class="goods-item">
            <img :src="item.imageUrl || 'https://placehold.co/100'" class="goods-img" />
            <div class="goods-info">
              <div class="name">{{ item.goodsName }}</div>
              <div class="price-row">
                <span class="price">¥{{ item.price }}</span>
                <span class="qty">x{{ item.quantity }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 3. 备注 -->
      <div class="section-card animate__animated animate__fadeInUp" style="animation-delay: 0.2s">
        <h3 class="section-title">订单备注</h3>
        <el-input
          v-model="remark"
          type="textarea"
          placeholder="口味偏好、包装要求等..."
          :rows="2"
        />
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div class="footer-bar animate__animated animate__fadeInUp">
      <div class="total-info">
        <span>合计:</span>
        <span class="total-price">¥{{ totalAmount }}</span>
      </div>
      <el-button 
        type="primary" 
        size="large" 
        class="submit-btn" 
        :loading="submitting"
        @click="handleSubmit"
      >
        提交订单
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Shop, Select } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const cartList = ref([])
const remark = ref('')
const submitting = ref(false)

const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
})

const fetchCart = async () => {
  try {
    const res = await request.get('/cart/list')
    if (res.code === 1) {
      cartList.value = res.data
      if (cartList.value.length === 0) {
        ElMessage.warning('购物车为空，请先选购')
        router.push('/')
      }
    }
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  if (cartList.value.length === 0) return
  
  submitting.value = true
  try {
    const res = await request.post('/order/create', { remark: remark.value })
    if (res.code === 1) {
      ElMessage.success('订单创建成功')
      // 跳转收银台，带上 orderNo
      router.push(`/cashier?orderNo=${res.data}`)
    } else {
      ElMessage.error(res.message || '下单失败')
    }
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchCart()
})
</script>

<style scoped>
.confirm-container {
  min-height: 100vh;
  background: #f7f1e3; /* 奶茶色 */
  padding-bottom: 80px;
}

.header {
  background: #fff;
  padding: 15px 20px;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.content {
  padding: 20px;
}

.section-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.03);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 15px 0;
  color: #333;
}

/* 收货方式 */
.delivery-box {
  border: 2px solid #ff9f43; /* 活力橙 */
  background: #fffbf0;
  border-radius: 12px;
  padding: 15px;
  display: flex;
  align-items: center;
  position: relative;
}

.delivery-box .icon {
  font-size: 24px;
  color: #ff9f43;
  margin-right: 12px;
}

.delivery-box .info .title {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.delivery-box .info .desc {
  font-size: 12px;
  color: #999;
}

.delivery-box .check {
  position: absolute;
  right: 15px;
  color: #ff9f43;
  font-size: 20px;
}

/* 商品列表 */
.goods-item {
  display: flex;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f5f5f5;
}

.goods-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.goods-img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  margin-right: 12px;
  object-fit: cover;
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-info .name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
}

.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: #ff6b6b;
  font-weight: 600;
}

.qty {
  color: #999;
  font-size: 12px;
}

/* 底部栏 */
.footer-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  padding: 10px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.05);
  z-index: 100;
}

.total-info {
  font-size: 14px;
  color: #333;
}

.total-price {
  font-size: 20px;
  color: #ff6b6b;
  font-weight: 700;
  margin-left: 5px;
}

.submit-btn {
  background: linear-gradient(45deg, #ff9f43, #ff6b6b);
  border: none;
  border-radius: 20px;
  padding: 0 30px;
  font-weight: 600;
}
</style>