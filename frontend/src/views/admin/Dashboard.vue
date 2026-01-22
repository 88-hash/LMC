<template>
  <div class="dashboard-container">
    <!-- Row 1: 核心指标 (糖果卡片) -->
    <el-row :gutter="24" class="mb-4">
      <el-col :span="6">
        <el-card shadow="hover" class="metric-card bg-lemon">
          <div class="metric-content">
            <span class="metric-label">今日营业额</span>
            <span class="metric-value color-choco">¥{{ stats.overview.todaySales }}</span>
            <span class="metric-sub color-choco-light">日环比 +12% <el-icon><Top /></el-icon></span>
          </div>
          <div class="metric-icon-box icon-lemon">
            <el-icon><Money /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="metric-card bg-mint">
          <div class="metric-content">
            <span class="metric-label">平均客单价 (ATV)</span>
            <span class="metric-value color-choco">¥{{ stats.overview.averageTicketValue }}</span>
            <span class="metric-sub color-choco-light">行业优秀水平</span>
          </div>
          <div class="metric-icon-box icon-mint">
            <el-icon><TrendCharts /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="metric-card bg-soda">
          <div class="metric-content">
            <span class="metric-label">今日订单量</span>
            <span class="metric-value color-choco">{{ stats.overview.todayOrderCount }}</span>
            <span class="metric-sub color-choco-light">较昨日持平</span>
          </div>
          <div class="metric-icon-box icon-soda">
            <el-icon><List /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="metric-card bg-berry">
          <div class="metric-content">
            <span class="metric-label">待核销订单</span>
            <span class="metric-value color-choco">{{ stats.overview.pendingVerify }}</span>
            <span class="metric-sub color-choco-light">请尽快处理</span>
          </div>
          <div class="metric-icon-box icon-berry">
            <el-icon><Timer /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 2: 趋势 & 品类 -->
    <el-row :gutter="24" class="mb-4">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title color-choco"><span class="title-dot bg-orange"></span>近7天营业趋势</span>
            </div>
          </template>
          <div ref="lineChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title color-choco"><span class="title-dot bg-purple"></span>品类销售占比</span>
            </div>
          </template>
          <div ref="pieChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Row 3: 时段 & Top10 & 风险 -->
    <el-row :gutter="24">
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title color-choco"><span class="title-dot bg-mint"></span>24小时下单热力</span>
            </div>
          </template>
          <div ref="barChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover" class="list-card">
          <template #header>
            <div class="card-header">
              <span class="card-title color-choco"><span class="title-dot bg-red"></span>🔥 热销 Top 10</span>
            </div>
          </template>
          <el-table :data="stats.topGoods" height="320" stripe :header-cell-style="{background:'#FFF5EB',color:'#2D3436'}">
            <el-table-column type="index" width="50" align="center">
              <template #default="{ $index }">
                <span :class="['rank-badge', $index < 3 ? 'top-rank' : '']">{{ $index + 1 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="goodsName" label="商品名称" show-overflow-tooltip />
            <el-table-column prop="quantity" label="销量" width="80" align="right">
               <template #default="{ row }">
                <span class="font-bold color-orange">{{ row.quantity }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card shadow="hover" class="list-card">
          <template #header>
            <div class="card-header">
              <span class="card-title color-red">⚠️ 风险预警</span>
            </div>
          </template>
          <el-table :data="stats.riskGoods" height="320" style="width: 100%" :header-cell-style="{background:'#FFE3E3',color:'#D63031'}">
            <el-table-column prop="type" label="类型" width="90">
              <template #default="{ row }">
                <el-tag size="small" :type="row.type === '库存不足' ? 'warning' : 'danger'" effect="dark" class="rounded-tag">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="name" label="商品名称" show-overflow-tooltip />
            <el-table-column prop="stock" label="库存" width="70" align="center">
              <template #default="{ row }">
                <span class="stock-warning">{{ row.stock }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Money, TrendCharts, List, Timer, Top, Warning } from '@element-plus/icons-vue'
import request from '../../utils/request'

// Refs for Charts
const lineChartRef = ref(null)
const pieChartRef = ref(null)
const barChartRef = ref(null)

let lineChart = null
let pieChart = null
let barChart = null

const stats = ref({
  overview: { todaySales: 0, averageTicketValue: 0, todayOrderCount: 0, pendingVerify: 0 },
  trend: { dates: [], values: [] },
  categoryShare: [],
  hourlyStats: { hours: [], counts: [] },
  topGoods: [],
  riskGoods: []
})

// 多巴胺配色数组
const candyColors = ['#FF9F43', '#54A0FF', '#FF6B6B', '#1DD1A1', '#5f27cd', '#ff9ff3'];

const fetchData = async () => {
  try {
    const res = await request.get('/admin/stats/dashboard')
    if (res.code === 1) {
      stats.value = res.data
      nextTick(() => {
        initCharts()
      })
    }
  } catch (error) {
    console.error(error)
  }
}

const initCharts = () => {
  // 1. 折线图 (活力橙)
  if (lineChartRef.value) {
    if (lineChart) lineChart.dispose()
    lineChart = echarts.init(lineChartRef.value)
    lineChart.setOption({
      color: candyColors,
      tooltip: { trigger: 'axis', backgroundColor: 'rgba(255,255,255,0.9)', textStyle: { color: '#2D3436' } },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
      xAxis: { type: 'category', boundaryGap: false, data: stats.value.trend.dates, axisLine: { lineStyle: { color: '#FFD3B6' } }, axisLabel: { color: '#636e72' } },
      yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#FFEAA7' } } },
      series: [{
        name: '营业额', type: 'line', smooth: true,
        itemStyle: { color: '#FF9F43' },
        lineStyle: { width: 4, shadowColor: 'rgba(255, 159, 67, 0.4)', shadowBlur: 10 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 159, 67, 0.4)' }, { offset: 1, color: 'rgba(255, 159, 67, 0.0)' }
          ])
        },
        data: stats.value.trend.values
      }]
    })
  }

  // 2. 饼图
  if (pieChartRef.value) {
    if (pieChart) pieChart.dispose()
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      color: candyColors,
      tooltip: { trigger: 'item' },
      legend: { bottom: '0%', left: 'center', itemWidth: 12, itemHeight: 12, textStyle: { color: '#636e72' } },
      series: [{
        name: '品类占比', type: 'pie', radius: ['45%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 3 },
        label: { show: false },
        labelLine: { show: false },
        data: stats.value.categoryShare
      }]
    })
  }

  // 3. 柱状图 (薄荷绿)
  if (barChartRef.value) {
    if (barChart) barChart.dispose()
    barChart = echarts.init(barChartRef.value)
    barChart.setOption({
      color: candyColors,
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
      xAxis: { type: 'category', data: stats.value.hourlyStats.hours, axisLine: { lineStyle: { color: '#FFD3B6' } }, axisLabel: { color: '#636e72' } },
      yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#FFEAA7' } } },
      series: [{
        name: '订单量', type: 'bar', barWidth: '50%',
        itemStyle: { 
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#55E6C1' }, { offset: 1, color: '#1DD1A1' }
          ]),
          borderRadius: [8, 8, 0, 0] 
        },
        data: stats.value.hourlyStats.counts
      }]
    })
  }
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', () => {
    lineChart && lineChart.resize()
    pieChart && pieChart.resize()
    barChart && barChart.resize()
  })
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background-color: #F7F1E3; /* 奶茶色 */
  min-height: 100vh;
}
.mb-4 { margin-bottom: 24px; }

/* 卡片通用 - 糖果风 */
.el-card {
  border: 2px solid #FFF5EB; /* 浅橙色边框 */
  border-radius: 20px; /* 大圆角 */
  box-shadow: 0 8px 20px rgba(233, 212, 172, 0.4); /* 暖色阴影 */
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background-color: #FFFFFF;
}
.el-card:hover {
  transform: translateY(-4px) scale(1.01);
  box-shadow: 0 12px 24px rgba(233, 212, 172, 0.6);
}

/* 核心指标卡 */
.metric-card {
  height: 130px;
  position: relative;
  overflow: hidden;
  border: none; /* 指标卡不需要边框，因为有背景色 */
}
.metric-card :deep(.el-card__body) {
  padding: 20px 24px;
  height: 100%;
  box-sizing: border-box;
}

.metric-content {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
}

.metric-label { font-size: 15px; color: #2D3436; margin-bottom: 8px; font-weight: 600; opacity: 0.7; }
.metric-value { font-size: 32px; font-weight: 800; margin-bottom: 8px; font-family: 'Baloo 2', 'Varela Round', sans-serif; }
.metric-sub { font-size: 13px; font-weight: 500; display: flex; align-items: center; gap: 4px; }

.metric-icon-box {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  z-index: 1;
  background: rgba(255,255,255,0.3);
  backdrop-filter: blur(4px);
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
}

/* 多巴胺配色 */
.bg-lemon { background: #FFEAA7; }
.icon-lemon { color: #FD79A8; }

.bg-mint { background: #55E6C1; }
.icon-mint { color: #009432; }

.bg-soda { background: #74B9FF; }
.icon-soda { color: #0984E3; }

.bg-berry { background: #FF7675; }
.icon-berry { color: #D63031; }

.color-choco { color: #2D3436; }
.color-choco-light { color: rgba(45, 52, 54, 0.6); }
.color-orange { color: #FF9F43; }
.color-red { color: #FF6B6B; }

/* 图表卡片 */
.chart-card, .list-card { height: 100%; }
.chart-card :deep(.el-card__header), .list-card :deep(.el-card__header) {
  padding: 16px 24px;
  border-bottom: 2px dashed #FFF0DA;
}

.card-header { display: flex; align-items: center; }
.card-title { font-weight: 800; font-size: 18px; color: #2D3436; display: flex; align-items: center; gap: 10px; }
.title-dot { width: 12px; height: 12px; border-radius: 50%; display: inline-block; border: 2px solid #fff; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }

.bg-blue { background: #54A0FF; }
.bg-purple { background: #5f27cd; }
.bg-green { background: #1DD1A1; }
.bg-orange { background: #FF9F43; }
.bg-mint { background: #00b894; }
.bg-red { background: #FF6B6B; }

.chart-box { height: 320px; width: 100%; }
.list-card { min-height: 400px; }

/* 排行榜 */
.rank-badge {
  display: inline-block;
  width: 24px; height: 24px;
  line-height: 24px; text-align: center;
  border-radius: 8px;
  background-color: #F7F1E3;
  color: #B2BEC3;
  font-size: 13px;
  font-weight: 800;
}
.rank-badge.top-rank {
  background-color: #FF9F43;
  color: #FFFFFF;
  box-shadow: 0 2px 6px rgba(255, 159, 67, 0.4);
}
.font-bold { font-weight: 800; }
.stock-warning { color: #D63031; font-weight: 800; background: #FFD3B6; padding: 2px 6px; border-radius: 4px; }
.rounded-tag { border-radius: 8px; border: none; font-weight: 600; }
</style>