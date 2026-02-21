<template>
  <div class="confirm-container">
    <div class="header">
      <el-page-header @back="$router.go(-1)" content="确认订单" />
    </div>

    <div class="content">
      <div class="section-card animate__animated animate__fadeInUp">
        <h3 class="section-title">自提信息</h3>
        <div class="delivery-box">
          <el-icon class="icon"><Shop /></el-icon>
          <div class="info">
            <div class="title">门店自提</div>
            <div class="desc">乐逸零食旗舰店（营业时间 09:00-22:00）</div>
          </div>
          <el-icon class="check"><Select /></el-icon>
        </div>
      </div>

      <div class="section-card animate__animated animate__fadeInUp" style="animation-delay: 0.08s">
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
            <div class="line-total">¥{{ (Number(item.price || 0) * Number(item.quantity || 0)).toFixed(2) }}</div>
          </div>
        </div>
      </div>

      <div class="section-card animate__animated animate__fadeInUp" style="animation-delay: 0.14s">
        <h3 class="section-title">费用明细</h3>
        <div class="fee-row">
          <span>商品小计</span>
          <b>¥{{ totalAmount }}</b>
        </div>
        <div class="fee-row">
          <span>自提服务费</span>
          <b>¥0.00</b>
        </div>
        <div class="fee-row total">
          <span>合计</span>
          <b>¥{{ totalAmount }}</b>
        </div>
      </div>

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

    <div class="footer-bar animate__animated animate__fadeInUp">
      <div class="total-info">
        <span>合计</span>
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
import request from '../../utils/request'

const router = useRouter()
const cartList = ref([])
const remark = ref('')
const submitting = ref(false)

const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0), 0).toFixed(2)
})

const fetchCart = async () => {
  try {
    const res = await request.get('/cart/list')
    if (res.code === 1) {
      cartList.value = res.data
      if (cartList.value.length === 0) {
        ElMessage.warning('购物车为空，请先选购')
        router.push('/home')
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
  background: var(--color-bg, #f7f1e3);
  padding-bottom: calc(104px + env(safe-area-inset-bottom));
}

.header {
  position: sticky;
  top: 0;
  z-index: 30;
  background: var(--color-bg, #f7f1e3);
  padding: 12px 12px 8px;
}

.content {
  padding: 8px 12px 0;
}

.section-card {
  background: #fffdf5;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-card, 20px);
  box-shadow: var(--shadow-card, 0 6px 0 #000);
  padding: 12px;
  margin-bottom: 12px;
}

.section-title {
  margin: 0 0 10px;
  font-size: 14px;
  line-height: 1.2;
  font-weight: 800;
  color: var(--color-text, #111);
}

.delivery-box {
  border: var(--border-strong, 2px solid #000);
  border-radius: calc(var(--radius-card, 20px) - 6px);
  background: #fff;
  padding: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  box-shadow: inset 0 -2px 0 rgba(0, 0, 0, 0.08);
}

.delivery-box .icon,
.delivery-box .check {
  font-size: 18px;
  color: var(--color-primary, #ffd700);
}

.delivery-box .info {
  flex: 1;
  min-width: 0;
}

.delivery-box .title {
  font-size: 14px;
  font-weight: 700;
  color: var(--color-text, #111);
  line-height: 1.2;
}

.delivery-box .desc {
  margin-top: 2px;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.62);
  line-height: 1.35;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.goods-item {
  display: grid;
  grid-template-columns: 56px 1fr auto;
  align-items: center;
  gap: 8px;
  border: var(--border-strong, 2px solid #000);
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 3px 0 #000;
  padding: 8px;
}

.goods-img {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  border: var(--border-strong, 2px solid #000);
  object-fit: cover;
  background: #f8f8f8;
}

.goods-info {
  min-width: 0;
}

.goods-info .name {
  font-size: 13px;
  font-weight: 700;
  color: var(--color-text, #111);
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price-row {
  margin-top: 4px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
}

.price {
  color: var(--color-accent, #ff69b4);
  font-weight: 800;
  font-size: 13px;
}

.qty {
  color: rgba(0, 0, 0, 0.55);
  font-size: 12px;
  font-weight: 600;
}

.line-total {
  font-size: 13px;
  font-weight: 800;
  color: var(--color-text, #111);
}

.fee-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 2px;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.75);
  border-bottom: 1px dashed rgba(0, 0, 0, 0.16);
}

.fee-row:last-child {
  border-bottom: 0;
}

.fee-row.total {
  padding-top: 10px;
  font-weight: 800;
  color: var(--color-text, #111);
}

.fee-row.total b {
  color: var(--color-accent, #ff69b4);
  font-size: 18px;
}

.footer-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 40;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  background: #fffdf5;
  border-top: var(--border-strong, 2px solid #000);
  box-shadow: 0 -4px 0 #000;
  padding: 10px 12px calc(10px + env(safe-area-inset-bottom));
}

.total-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  color: rgba(0, 0, 0, 0.66);
  font-size: 12px;
}

.total-price {
  color: var(--color-accent, #ff69b4);
  font-size: 22px;
  line-height: 1;
  font-weight: 900;
}

.submit-btn {
  min-width: 136px;
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-pill, 999px);
  background: var(--color-primary, #ffd700);
  color: #000;
  box-shadow: var(--shadow-card, 0 6px 0 #000);
  font-weight: 800;
}

.submit-btn:active {
  transform: translateY(2px) scale(0.985);
  box-shadow: 0 3px 0 #000;
}

:deep(.el-page-header__content) {
  font-size: 16px;
  font-weight: 800;
  color: var(--color-text, #111);
}

:deep(.el-textarea__inner) {
  border: var(--border-strong, 2px solid #000);
  border-radius: 14px;
  box-shadow: inset 0 -2px 0 rgba(0, 0, 0, 0.08);
  padding: 10px 12px;
}
</style>
