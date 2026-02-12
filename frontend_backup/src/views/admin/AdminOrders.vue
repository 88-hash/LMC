<template>
  <div class="orders-container">
    <!-- 顶部筛选栏 -->
    <el-card shadow="hover" class="filter-card">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="状态">
          <el-select v-model="filter.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="待核销" :value="0" />
            <el-option label="已完成" :value="1" />
            <el-option label="已取消" :value="-1" />
          </el-select>
        </el-form-item>
        <el-form-item label="搜索">
          <el-input v-model="filter.keyword" placeholder="订单号 / 手机号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 订单表格 -->
    <el-card shadow="hover" class="table-card">
      <div v-if="!hasSearched" class="empty-state">
        <el-empty description="请选择条件进行查询" :image-size="200" />
      </div>

      <div v-else>
        <el-table 
          :data="orders" 
          stripe 
          v-loading="loading"
          @row-click="handleDetail"
          style="width: 100%"
          :header-cell-style="{background:'#fffbf0', color:'#2c3e50', fontWeight:'bold'}"
        >
          <el-table-column prop="orderNo" label="订单编号" width="220">
            <template #default="{ row }">
              <span class="order-no">{{ row.orderNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="总金额" width="120" align="right">
            <template #default="{ row }">
              <span class="amount">¥{{ row.totalAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="dark" class="status-tag">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="verifyCode" label="核销码" width="120" align="center">
            <template #default="{ row }">
              <span v-if="row.status === 0" class="verify-code">{{ row.verifyCode }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="下单时间" min-width="160">
            <template #default="{ row }">
               {{ formatTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" align="center">
            <template #default>
              <el-button link type="primary" size="small">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-box">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchData"
            @current-change="fetchData"
          />
        </div>
      </div>
    </el-card>

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="订单详情"
      size="400px"
      :destroy-on-close="true"
    >
      <div v-if="currentOrder" class="detail-content">
        <!-- 头部状态 -->
        <div class="detail-header" :class="'status-' + currentOrder.status">
          <div class="status-text">{{ getStatusText(currentOrder.status) }}</div>
          <div class="order-id">NO. {{ currentOrder.orderNo }}</div>
        </div>

        <!-- 关键信息 -->
        <div class="info-group">
          <div class="info-item">
            <label>下单时间</label>
            <span>{{ formatTime(currentOrder.createdAt) }}</span>
          </div>
          <div class="info-item">
            <label>用户ID</label>
            <span>{{ currentOrder.userId }}</span>
          </div>
          <div class="info-item" v-if="currentOrder.verifyCode">
            <label>核销码</label>
            <span class="code">{{ currentOrder.verifyCode }}</span>
          </div>
          <div class="info-item" v-if="currentOrder.remark">
            <label>备注</label>
            <span>{{ currentOrder.remark }}</span>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="goods-list">
          <div class="section-title">商品明细</div>
          <div v-for="item in currentItems" :key="item.id" class="goods-item">
            <img :src="item.goodsImage || 'https://placehold.co/60'" class="goods-img" />
              <div class="goods-info">
              <div class="name">{{ item.goodsName }}</div>
              <div class="price-row">
                <span class="price">¥{{ item.price }}</span>
                <span class="qty">x{{ item.quantity }}</span>
              </div>
              
              <div v-if="item.comment" class="comment-box">
                <div class="comment-header">
                  <el-rate 
                    v-model="item.comment.rating" 
                    disabled 
                    show-score 
                    text-color="#ff9900" 
                    size="small"
                  />
                  <span class="comment-time">{{ formatTime(item.comment.createdAt) }}</span>
                </div>
                <div class="comment-content">
                  {{ item.comment.content }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 合计 -->
        <div class="detail-footer">
          <div class="total-label">实付金额</div>
          <div class="total-amount">¥{{ currentOrder.totalAmount }}</div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const dateRange = ref([])

const filter = reactive({
  status: '',
  keyword: ''
})

const drawerVisible = ref(false)
const currentOrder = ref(null)
const currentItems = ref([])

const hasSearched = ref(false)

const fetchData = async () => {
  loading.value = true
  hasSearched.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      status: filter.status === '' ? null : filter.status,
      keyword: filter.keyword,
      startDate: dateRange.value?.[0] ? dateRange.value[0] + ' 00:00:00' : null,
      endDate: dateRange.value?.[1] ? dateRange.value[1] + ' 23:59:59' : null
    }
    const res = await request.get('/admin/order/list', { params })
    if (res.code === 1) {
      orders.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchData()
}

const resetFilter = () => {
  filter.status = ''
  filter.keyword = ''
  dateRange.value = []
  // 不自动查询，等待用户点击
  hasSearched.value = false
  orders.value = []
  total.value = 0
}

const handleDetail = async (row) => {
  try {
    const res = await request.get('/admin/order/detail', { params: { id: row.id } })
    if (res.code === 1) {
      currentOrder.value = res.data.order
      currentItems.value = res.data.items
      drawerVisible.value = true
    }
  } catch (error) {
    console.error(error)
  }
}

const getStatusText = (status) => {
  const map = { 0: '待核销', 1: '已完成', '-1': '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', '-1': 'info' }
  return map[status] || 'info'
}

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 移除自动查询
// onMounted(() => {
//   fetchData()
// })
</script>

<style scoped>
.orders-container {
  padding: 24px;
  background-color: #f7f1e3; /* 奶茶色 */
  min-height: 100vh;
}

.filter-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
}

.table-card {
  border-radius: 12px;
  border: none;
  min-height: 500px;
}

.order-no {
  font-family: 'Courier New', Courier, monospace;
  font-weight: bold;
  color: #57606f;
}

.amount {
  font-weight: 800;
  color: #ff6b6b;
  font-size: 16px;
}

.status-tag {
  border-radius: 12px;
  padding: 0 12px;
}

.verify-code {
  background: #ffeaa7;
  padding: 2px 8px;
  border-radius: 4px;
  color: #d35400;
  font-weight: bold;
  letter-spacing: 1px;
}

.pagination-box {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 详情抽屉 */
.detail-content {
  padding: 0 10px;
}

.detail-header {
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  color: #fff;
}

.detail-header.status-0 { background: linear-gradient(135deg, #f1c40f, #e67e22); }
.detail-header.status-1 { background: linear-gradient(135deg, #2ecc71, #27ae60); }
.detail-header.status--1 { background: #95a5a6; }

.status-text { font-size: 20px; font-weight: bold; margin-bottom: 5px; }
.order-id { font-size: 12px; opacity: 0.8; }

.info-group {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
}
.info-item:last-child { margin-bottom: 0; }
.info-item label { color: #7f8c8d; }
.info-item span { color: #2c3e50; font-weight: 500; }
.info-item .code { color: #e67e22; font-weight: 800; font-size: 16px; }

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  border-left: 4px solid #3498db;
  padding-left: 10px;
}

.goods-item {
  display: flex;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}
.goods-img {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  margin-right: 10px;
  object-fit: cover;
}
.goods-info { flex: 1; }
.goods-info .name { font-size: 14px; margin-bottom: 5px; }
.price-row { display: flex; justify-content: space-between; font-size: 13px; }
.price-row .price { color: #e74c3c; font-weight: bold; }
.price-row .qty { color: #95a5a6; }

.detail-footer {
  margin-top: 30px;
  border-top: 2px dashed #bdc3c7;
  padding-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.total-label { font-size: 14px; font-weight: bold; }
.total-amount { font-size: 24px; color: #e74c3c; font-weight: 800; }

.comment-box {
  margin-top: 10px;
  background: #fdf6ec;
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid #faecd8;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
  word-break: break-all;
}
</style>