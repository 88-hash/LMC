<template>
  <div class="profile-container">
    <div class="profile-card animate__animated animate__fadeIn">
      <div class="avatar-area">
        <div class="avatar" :style="{ backgroundImage: userInfo.avatar ? `url(${userInfo.avatar})` : '' }">
          {{ !userInfo.avatar ? (userInfo.name?.charAt(0) || 'U') : '' }}
        </div>
      </div>
      <h2 class="username">{{ userInfo.name || '未登录' }}</h2>
      <p class="phone">{{ userInfo.phone || '' }}</p>
      
      <div class="menu-list">
        <div class="menu-item" @click="$router.push('/order')">
          <el-icon><List /></el-icon>
          <span>我的订单</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/cart')">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>
      
      <el-button type="danger" round class="logout-btn" @click="handleLogout">
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { List, ShoppingCart, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
}

.profile-card {
  background: white;
  border-radius: 20px;
  padding: 30px 20px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  margin-top: 20px;
}

.avatar-area {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  font-weight: bold;
  background-size: cover;
  background-position: center;
}

.username {
  font-size: 20px;
  color: #333;
  margin: 0 0 5px 0;
}

.phone {
  color: #999;
  font-size: 14px;
  margin: 0 0 30px 0;
}

.menu-list {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 5px 0;
  margin-bottom: 30px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:active {
  background: #eee;
}

.menu-item .el-icon {
  font-size: 20px;
  color: #555;
  margin-right: 12px;
}

.menu-item span {
  flex: 1;
  text-align: left;
  font-size: 16px;
  color: #333;
}

.menu-item .arrow {
  color: #ccc;
  margin-right: 0;
  font-size: 16px;
}

.logout-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 10px;
}
</style>