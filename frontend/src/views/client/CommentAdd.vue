<template>
  <div class="comment-add-page">
    <div class="header">
      <h2>发表评价</h2>
    </div>
    <div class="content" v-loading="loading">
      <div v-for="(item, index) in items" :key="item.id" class="item-card">
        <div class="goods-info">
          <img :src="item.goodsImage" class="goods-img" />
          <div class="goods-name">{{ item.goodsName }}</div>
        </div>
        <div class="form-area">
          <div class="rate-row">
            <span>评分：</span>
            <el-rate v-model="forms[index].rating" :colors="['#ff9f43', '#ff9f43', '#ff9f43']" />
          </div>
          <el-input
            v-model="forms[index].content"
            type="textarea"
            :rows="3"
            placeholder="写下你的评价吧~"
            class="comment-input"
          />
        </div>
        <div class="action-row">
            <el-button type="primary" round size="small" @click="submitOne(index)" :disabled="forms[index].submitted">
                {{ forms[index].submitted ? '已发布' : '发布评价' }}
            </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const orderId = route.params.orderId
const items = ref([])
const forms = ref([])
const loading = ref(false)

const loadOrder = async () => {
  loading.value = true
  const res = await request.get('/order/detail', { params: { id: orderId } })
  if (res.code === 1) {
    items.value = res.data.items || []
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    
    forms.value = items.value.map(item => ({
        orderItemId: item.id,
        rating: 5,
        content: '',
        userPhone: userInfo.phone || '',
        submitted: false
    }))
  }
  loading.value = false
}

const submitOne = async (index) => {
    const form = forms.value[index]
    if (!form.content.trim()) {
        ElMessage.warning('请输入评价内容')
        return
    }
    
    // Refresh user phone just in case
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    form.userPhone = userInfo.phone

    const res = await request.post('/comment/add', form)
    if (res.code === 1) {
        ElMessage.success('评价成功')
        form.submitted = true
    } else {
        ElMessage.error(res.message)
    }
}

onMounted(loadOrder)
</script>

<style scoped>
.comment-add-page {
  min-height: 100vh;
  background-color: #f7f1e3;
  padding: 20px;
}
.header { margin-bottom: 20px; text-align: center; color: #333; }
.item-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.goods-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.goods-img { width: 50px; height: 50px; border-radius: 8px; object-fit: cover; }
.goods-name { font-weight: bold; color: #333; }
.rate-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.comment-input :deep(.el-textarea__inner) {
  border-radius: 12px;
  background-color: #fafafa;
  border: 1px solid #eee;
}
.action-row { text-align: right; margin-top: 10px; }
</style>
