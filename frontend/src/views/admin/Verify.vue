<template>
  <div class="verify-container">
    <!-- 顶部数据卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card pending">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待核销订单</div>
              <div class="stat-value">{{ pendingList.length }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索与列表 -->
    <el-card shadow="never" class="list-card">
      <template #header>
        <div class="card-header">
          <span class="title">核销列表</span>
          <div class="search-box">
            <el-input
              v-model="keyword"
              placeholder="搜索订单号或备注..."
              clearable
              prefix-icon="Search"
              style="width: 300px"
            />
          </div>
        </div>
      </template>

      <el-table :data="filteredList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" min-width="180">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="verifyCode" label="核销码" width="150">
          <template #default="{ row }">
            <el-tag effect="plain" type="warning" size="large" class="verify-code">
              {{ row.verifyCode }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="remark" label="备注" show-overflow-tooltip />

        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="success" 
              size="large" 
              class="verify-btn"
              @click="handleVerify(row)"
            >
              <el-icon class="mr-1"><Check /></el-icon>
              确认核销
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Timer, Search, Check } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const loading = ref(false)
const pendingList = ref([])
const keyword = ref('')

// 过滤列表
const filteredList = computed(() => {
  if (!keyword.value) return pendingList.value
  const k = keyword.value.toLowerCase()
  return pendingList.value.filter(item => 
    item.orderNo.toLowerCase().includes(k) || 
    (item.remark && item.remark.toLowerCase().includes(k)) ||
    (item.verifyCode && item.verifyCode.includes(k))
  )
})

// 获取待核销列表
const fetchPendingOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/verify/pending')
    if (res.code === 1) {
      pendingList.value = res.data || []
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 确认核销
const handleVerify = (row) => {
  ElMessageBox.confirm(
    `确定要核销订单 ${row.orderNo} 吗？`,
    '核销确认',
    {
      confirmButtonText: '确定核销',
      cancelButtonText: '取消',
      type: 'warning',
      center: true
    }
  ).then(async () => {
    try {
      const res = await request.post('/verify/confirm', { orderId: row.id })
      if (res.code === 1) {
        ElMessage.success('核销成功！')
        fetchPendingOrders() // 刷新列表
      }
    } catch (error) {
      // 错误已由拦截器处理
    }
  }).catch(() => {})
}

// 时间格式化
const formatTime = (arr) => {
  if (!arr) return ''
  if (Array.isArray(arr)) {
    // 处理 LocalDateTime 数组 [2023, 1, 1, 12, 0, 0]
    const [y, m, d, h, min, s] = arr
    return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')} ${String(h).padStart(2, '0')}:${String(min).padStart(2, '0')}`
  }
  return arr
}

onMounted(() => {
  fetchPendingOrders()
})
</script>

<style scoped>
.verify-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 20px;
}

.stat-card {
  border: none;
  border-radius: 12px;
  color: white;
  transition: transform 0.3s;
}

.stat-card.pending {
  background: linear-gradient(135deg, #e6a23c 0%, #f3d19e 100%);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  font-size: 48px;
  margin-right: 20px;
  opacity: 0.8;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.list-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.order-no {
  font-family: monospace;
  font-weight: 600;
  color: #409EFF;
  font-size: 16px;
}

.price {
  color: #F56C6C;
  font-weight: bold;
  font-size: 16px;
}

.verify-code {
  font-family: monospace;
  font-weight: bold;
  letter-spacing: 1px;
}

.verify-btn {
  font-weight: bold;
  letter-spacing: 1px;
  transition: all 0.3s;
}

.verify-btn:hover {
  transform: scale(1.05);
}

.mr-1 {
  margin-right: 4px;
}
</style>