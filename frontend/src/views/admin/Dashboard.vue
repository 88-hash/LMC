<template>
  <div class="dashboard-container">
    <!-- 1. 顶部核心指标 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">今日营业额</div>
            <div class="stat-num text-gold">¥{{ stats.overview.todaySales }}</div>
            <div class="stat-sub">目标达成率 85%</div>
          </div>
          <div class="stat-icon bg-gold">
            <el-icon><Money /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">核销转化率</div>
            <div class="stat-num text-blue">{{ stats.overview.verifyRate }}</div>
            <div class="stat-sub">累计核销订单数</div>
          </div>
          <div class="stat-icon bg-blue">
            <el-icon><DataLine /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">待核销订单</div>
            <div class="stat-num text-green">{{ stats.overview.pendingVerify }}</div>
            <div class="stat-sub">请及时处理</div>
          </div>
          <div class="stat-icon bg-green">
            <el-icon><Timer /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">风险商品</div>
            <div class="stat-num text-red">{{ stats.overview.warningCount }}</div>
            <div class="stat-sub">库存不足 / 临期</div>
          </div>
          <div class="stat-icon bg-red">
            <el-icon><Warning /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 2. 中间区域：趋势图 + 热销榜 -->
    <el-row :gutter="20" class="mb-4">
      <!-- 左侧：营业额趋势 -->
      <el-col :span="16">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="title">营业额趋势</span>
              <el-radio-group v-model="trendDays" size="small" @change="fetchData">
                <el-radio-button label="7">近7天</el-radio-button>
                <el-radio-button label="30">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="chartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      
      <!-- 右侧：热销 Top10 -->
      <el-col :span="8">
        <el-card shadow="never" class="list-card">
          <template #header>
            <div class="card-header">
              <span class="title">🔥 热销 Top 10</span>
            </div>
          </template>
          <el-table :data="stats.topGoods" stripe height="300" :show-header="false">
            <el-table-column type="index" width="50">
              <template #default="{ $index }">
                <span :class="['rank-badge', $index < 3 ? 'top-3' : '']">{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="goodsName" label="商品" show-overflow-tooltip />
            <el-table-column prop="quantity" label="销量" width="80" align="right">
              <template #default="{ row }">
                <span class="sales-num">{{ row.quantity }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 3. 底部区域：风险预警 -->
    <el-card shadow="never" class="risk-card">
      <template #header>
        <div class="card-header">
          <span class="title text-red">⚠️ 风险预警 (库存/临期)</span>
        </div>
      </template>
      <el-table :data="stats.riskGoods" style="width: 100%">
        <el-table-column prop="type" label="风险类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === '库存不足' ? 'warning' : 'danger'">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="stock" label="当前库存" width="120">
          <template #default="{ row }">
            <span class="stock-warning">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default>
            <el-button link type="primary" @click="$router.push('/admin/goods')">去处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Money, DataLine, Timer, Warning } from '@element-plus/icons-vue'
import request from '../../utils/request'

const chartRef = ref(null)
const trendDays = ref('7')
let myChart = null

const stats = ref({
  overview: { todaySales: 0, verifyRate: '0%', pendingVerify: 0, warningCount: 0 },
  trend: { dates: [], values: [] },
  topGoods: [],
  riskGoods: []
})

const fetchData = async () => {
  try {
    const res = await request.get('/admin/stats/dashboard')
    if (res.code === 1) {
      stats.value = res.data
      initChart()
    }
  } catch (error) {
    console.error(error)
  }
}

const initChart = () => {
  if (!chartRef.value) return
  
  if (myChart) myChart.dispose()
  myChart = echarts.init(chartRef.value)
  
  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: stats.value.trend.dates
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '营业额',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 170, 0, 0.5)' },
            { offset: 1, color: 'rgba(255, 170, 0, 0.05)' }
          ])
        },
        itemStyle: { color: '#ffaa00' },
        data: stats.value.trend.values
      }
    ]
  }
  
  myChart.setOption(option)
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', () => myChart && myChart.resize())
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.mb-4 { margin-bottom: 20px; }

/* 顶部卡片 */
.stat-card {
  border: none;
  border-radius: 8px;
  overflow: hidden;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.stat-content { display: flex; flex-direction: column; }
.stat-label { color: #909399; font-size: 14px; margin-bottom: 8px; }
.stat-num { font-size: 28px; font-weight: bold; margin-bottom: 4px; }
.stat-sub { font-size: 12px; color: #C0C4CC; }

.stat-icon {
  width: 50px; height: 50px;
  border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-size: 24px;
  color: white;
}

.text-gold { color: #ffaa00; }
.bg-gold { background: linear-gradient(135deg, #ffaa00 0%, #ffd700 100%); }

.text-blue { color: #409EFF; }
.bg-blue { background: linear-gradient(135deg, #409EFF 0%, #79bbff 100%); }

.text-green { color: #67C23A; }
.bg-green { background: linear-gradient(135deg, #67C23A 0%, #95d475 100%); }

.text-red { color: #F56C6C; }
.bg-red { background: linear-gradient(135deg, #F56C6C 0%, #f89898 100%); }

/* 标题样式 */
.title { font-weight: bold; font-size: 16px; color: #303133; }
.text-red { color: #F56C6C; }

.card-header { display: flex; justify-content: space-between; align-items: center; }

/* 图表容器 */
.chart-container { height: 320px; width: 100%; }

/* 排行榜 */
.rank-badge {
  display: inline-block;
  width: 20px; height: 20px;
  line-height: 20px; text-align: center;
  border-radius: 50%;
  background-color: #f0f2f5;
  color: #909399;
  font-size: 12px;
  font-weight: bold;
}
.rank-badge.top-3 {
  background-color: #303133;
  color: gold;
}
.sales-num { font-weight: bold; color: #606266; }

/* 风险列表 */
.stock-warning { color: #F56C6C; font-weight: bold; }
</style>