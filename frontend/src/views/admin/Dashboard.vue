<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片 -->
    <div class="welcome-card animate__animated animate__fadeInDown">
      <div class="welcome-text">
        <h2>早安，管理员！☀️</h2>
        <p>今天是美好的一天，祝您生意兴隆！</p>
      </div>
      <img src="https://img.freepik.com/free-vector/business-team-brainstorming-discussing-startup-project_74855-6909.jpg" class="welcome-img" alt="welcome" />
    </div>

    <!-- 数据指标卡片 -->
    <el-row :gutter="20" class="data-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="(item, index) in cards" :key="index">
        <div class="data-card animate__animated animate__fadeInUp" :style="{ animationDelay: index * 0.1 + 's' }">
          <div class="card-icon" :style="{ background: item.bgColor, color: item.color }">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="card-info">
            <div class="card-title">{{ item.title }}</div>
            <div class="card-num" :style="{ color: item.color }">
              <span v-if="item.prefix" class="prefix">{{ item.prefix }}</span>
              {{ item.value }}
              <span v-if="item.suffix" class="suffix">{{ item.suffix }}</span>
            </div>
          </div>
          <!-- 预警闪烁动画 -->
          <div v-if="item.isWarning && item.value > 0" class="warning-dot"></div>
        </div>
      </el-col>
    </el-row>

    <!-- 这里可以预留图表区域 -->
    <div class="chart-section animate__animated animate__fadeInUp" style="animation-delay: 0.4s">
      <el-empty description="销售趋势图表开发中..." />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Money, List, Timer, Warning } from '@element-plus/icons-vue'
import request from '../../utils/request'

const stats = ref({
  todaySales: 0,
  todayOrderCount: 0,
  pendingOrderCount: 0,
  lowStockCount: 0
})

// 卡片配置 (使用 computed 以便自动更新 value)
const cards = computed(() => [
  {
    title: '今日营业额',
    value: stats.value.todaySales,
    icon: Money,
    color: '#ff6b6b',
    bgColor: '#ffe2e2',
    prefix: '¥'
  },
  {
    title: '今日订单',
    value: stats.value.todayOrderCount,
    icon: List,
    color: '#409eff',
    bgColor: '#ecf5ff',
    suffix: '单'
  },
  {
    title: '待核销订单',
    value: stats.value.pendingOrderCount,
    icon: Timer,
    color: '#67c23a',
    bgColor: '#e1f3d8',
    suffix: '单'
  },
  {
    title: '库存预警',
    value: stats.value.lowStockCount,
    icon: Warning,
    color: '#e6a23c',
    bgColor: '#fdf6ec',
    suffix: '件',
    isWarning: true
  }
])

onMounted(async () => {
  try {
    const res = await request.get('/admin/stats/dashboard')
    if (res.code === 1) {
      stats.value = res.data
    }
  } catch (e) {
    console.error('获取统计数据失败', e)
  }
})
</script>

<style scoped>
.dashboard-container {
  padding: 10px;
}

/* 欢迎卡片 */
.welcome-card {
  background: white;
  border-radius: 12px;
  padding: 30px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.03);
  position: relative;
  overflow: hidden;
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 6px;
  height: 100%;
  background: #409eff;
}

.welcome-text h2 {
  font-size: 24px;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.welcome-text p {
  color: #909399;
  margin: 0;
}

.welcome-img {
  height: 120px;
  object-fit: contain;
}

/* 数据卡片 */
.data-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.03);
  transition: all 0.3s;
  position: relative;
  cursor: pointer;
}

.data-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.08);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
}

.card-info {
  flex: 1;
}

.card-title {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.card-num {
  font-size: 28px;
  font-weight: bold;
  font-family: 'DIN', sans-serif; /* 假设有数字字体，没有则回退 */
}

.prefix, .suffix {
  font-size: 14px;
  font-weight: normal;
  margin: 0 2px;
}

/* 预警动画 */
.warning-dot {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #f56c6c;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(245, 108, 108, 0.4); }
  70% { box-shadow: 0 0 0 10px rgba(245, 108, 108, 0); }
  100% { box-shadow: 0 0 0 0 rgba(245, 108, 108, 0); }
}

.chart-section {
  margin-top: 30px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  min-height: 400px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.03);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .welcome-img { display: none; }
  .data-row .el-col { margin-bottom: 20px; }
}
</style>
