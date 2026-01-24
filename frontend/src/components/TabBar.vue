<template>
  <div class="tab-bar">
    <div 
      v-for="item in tabs" 
      :key="item.path" 
      class="tab-item"
      :class="{ active: currentPath === item.path }"
      @click="router.push(item.path)"
    >
      <div class="icon-box">
        <el-icon :size="24">
          <component :is="item.icon" />
        </el-icon>
        <div v-if="item.path === '/cart' && cartCount > 0" class="badge">{{ cartCount }}</div>
      </div>
      <span class="label">{{ item.label }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { House, ShoppingCart, List, User } from '@element-plus/icons-vue'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const tabs = [
  { path: '/', label: '首页', icon: House },
  { path: '/cart', label: '购物车', icon: ShoppingCart },
  { path: '/order', label: '订单', icon: List },
  { path: '/profile', label: '我的', icon: User }
]

const currentPath = computed(() => {
  // 处理子路由高亮，例如 /cart/checkout 仍高亮购物车 (简单起见，精确匹配)
  // 如果当前是首页，path是 '/'
  if (route.path === '/') return '/'
  // 其他路由匹配前缀
  const matched = tabs.find(t => t.path !== '/' && route.path.startsWith(t.path))
  return matched ? matched.path : route.path
})

const cartCount = computed(() => cartStore.count)
</script>

<style scoped>
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px; /* Standard mobile tab bar height */
  background: white;
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.03);
  z-index: 1000;
  padding-bottom: env(safe-area-inset-bottom);
  height: calc(50px + env(safe-area-inset-bottom));
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-item.active {
  color: var(--color-primary);
}

.icon-box {
  position: relative;
  height: 24px;
  width: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.label {
  font-size: 10px;
  margin-top: 4px;
  font-weight: 500;
}

.badge {
  position: absolute;
  top: -5px;
  right: -8px;
  background-color: #ff4d4f;
  color: white;
  font-size: 10px;
  min-width: 16px;
  height: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  border: 2px solid white;
}
</style>