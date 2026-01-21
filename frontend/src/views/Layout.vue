<template>
  <div class="app-layout">
    <!-- 顶部导航 -->
    <header class="app-header">
      <div class="header-inner">
        <!-- Logo -->
        <div class="logo">
          <span class="emoji">🍪</span>
          <h1 class="title">乐逸零食</h1>
        </div>

        <!-- 搜索框 -->
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索您想吃的零食..."
            class="search-input"
            :prefix-icon="Search"
          />
        </div>

        <!-- 右侧操作区 -->
        <div class="actions">
          <div class="action-item" @click="goCart">
            <el-badge :value="cartCount" class="cart-badge" :hidden="cartCount===0">
              <el-icon :size="24"><ShoppingCart /></el-icon>
            </el-badge>
            <span class="action-text">购物车</span>
          </div>

          <div class="action-item" @click="goOrders">
            <el-icon :size="24"><List /></el-icon>
            <span class="action-text">我的订单</span>
          </div>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-avatar">
              <el-avatar :size="36" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体内容 -->
    <main class="app-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, ShoppingCart, List } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const searchKeyword = ref('')
const cartStore = useCartStore()
const cartCount = ref(0)

const goCart = () => { router.push('/cart') }
const goOrders = () => { router.push('/order') }

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.clear()
      router.push('/login')
      ElMessage.success('已退出登录')
    })
  }
}

onMounted(async () => {
  await cartStore.refresh()
  cartCount.value = cartStore.count
})
</script>

<style scoped>
.app-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.app-header {
  height: 64px;
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.emoji {
  font-size: 28px;
  margin-right: 8px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0;
}

.search-bar {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background-color: #f5f7fa;
  box-shadow: none;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  background-color: white;
  box-shadow: 0 0 0 1px #ff8e53 inset;
}

.actions {
  display: flex;
  align-items: center;
  gap: 30px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;
}

.action-item:hover {
  color: #ff8e53;
}

.action-text {
  font-size: 12px;
  margin-top: 4px;
}

.user-avatar {
  cursor: pointer;
  margin-left: 10px;
}

.app-main {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
