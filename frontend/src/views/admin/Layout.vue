<template>
  <div class="admin-layout">
    <!-- 侧边栏 ("奶油威化饼风格") -->
    <aside class="sidebar">
      <div class="logo-area">
        <h1>🍪 乐逸零食 Pro</h1>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="admin-menu"
        background-color="transparent"
        text-color="#8854D0"
        active-text-color="#FFFFFF"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/admin/goods">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/category">
          <el-icon><Files /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/verify">
          <el-icon><Ticket /></el-icon>
          <span>核销中心</span>
        </el-menu-item>
        <el-menu-item index="/admin/manager">
          <el-icon><UserFilled /></el-icon>
          <span>店员管理</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 右侧内容区 -->
    <div class="main-container">
      <header class="navbar">
        <div class="left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="avatar-wrapper">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <div class="user-info-box">
                <span class="username">{{ adminName }}</span>
                <el-tag size="small" effect="plain" round :type="roleName === '店长' ? 'danger' : 'info'" class="role-tag">
                  {{ roleName }}
                </el-tag>
              </div>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Odometer, Goods, Files, List, Ticket, ArrowDown, UserFilled } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)
const currentRouteName = computed(() => route.meta.title || '当前页面')

const adminInfo = JSON.parse(localStorage.getItem('adminInfo') || '{}')
const adminName = ref(adminInfo.name || '超级店长')

const roleName = computed(() => {
  const role = adminInfo.role
  if (role === 'manager' || role === 'admin') return '店长'
  return '店员'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出后台管理系统吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('adminToken')
      localStorage.removeItem('adminInfo')
      router.push('/admin/login')
      ElMessage.success('已退出')
    })
  }
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  width: 100%;
  background-color: #F7F1E3; /* 奶茶色背景 */
}

/* 侧边栏美化 - 奶油威化风格 */
.sidebar {
  width: 240px;
  background-color: #FFFBF0; /* 暖奶油色 */
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 16px rgba(255, 159, 67, 0.1); /* 暖色阴影 */
  z-index: 1001;
  border-right: 2px solid #FFF0DA;
}

.logo-area {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 2px dashed #FFD3B6; /* 虚线分割，更像饼干包装 */
}

.logo-area h1 {
  font-size: 22px;
  margin: 0;
  font-weight: 800;
  color: #FF6B6B; /* 西瓜红 */
  letter-spacing: 1px;
  text-shadow: 1px 1px 0px #FFD3B6;
}

.admin-menu {
  border-right: none;
  flex: 1;
  padding-top: 16px;
}

/* 菜单项美化 - 果汁软糖风格 */
.el-menu-item {
  height: 54px;
  line-height: 54px;
  margin: 8px 12px;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.el-menu-item:hover {
  background-color: #FFF0DA !important; /* 浅橙色悬停 */
  color: #FF9F43;
}

.el-menu-item.is-active {
  background: linear-gradient(135deg, #FF9F43 0%, #FF6B6B 100%) !important; /* 橙红渐变 */
  color: #FFFFFF;
  box-shadow: 0 4px 10px rgba(255, 107, 107, 0.4); /* 弥散光 */
  transform: translateY(-1px);
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  height: 70px;
  background: #FFFFFF;
  border-bottom: 2px solid #F0E6D2;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
}

.avatar-wrapper {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #2D3436; /* 深巧克力色 */
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 20px;
  background-color: #FFFBF0;
  border: 1px solid #FFEAA7;
  transition: all 0.3s;
}

.avatar-wrapper:hover {
  background: #FFEAA7;
  transform: scale(1.02);
}

.app-main {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background-color: #F7F1E3; /* 奶茶色 */
  background-image: radial-gradient(#EAD8B1 1px, transparent 1px); /* 波点纹理 */
  background-size: 20px 20px;
}

/* 页面切换动画 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1); /* 弹跳效果 */
}

.fade-transform-enter-from {
  opacity: 0;
  transform: scale(0.95) translateY(10px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: scale(1.05);
}

.user-info-box {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-left: 8px;
  line-height: 1.2;
}

.role-tag {
  transform: scale(0.85);
  transform-origin: left center;
  margin-top: 2px;
}
</style>