<template>
  <div class="tab-bar">
    <router-link to="/" class="tab-item" active-class="active">
      <el-icon><House /></el-icon>
      <span>首页</span>
    </router-link>
    
    <router-link to="/cart" class="tab-item" active-class="active">
      <div class="icon-wrapper">
        <el-icon><ShoppingCart /></el-icon>
        <span v-if="cartCount > 0" class="badge">{{ cartCount }}</span>
      </div>
      <span>购物车</span>
    </router-link>
    
    <router-link to="/order" class="tab-item" active-class="active">
      <el-icon><List /></el-icon>
      <span>订单</span>
    </router-link>
    
    <router-link to="/profile" class="tab-item" active-class="active">
      <el-icon><User /></el-icon>
      <span>我的</span>
    </router-link>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { House, ShoppingCart, List, User } from '@element-plus/icons-vue'
import { useCartStore } from '../../stores/cart'

const cartStore = useCartStore()
const cartCount = computed(() => cartStore.totalCount)
</script>

<style scoped>
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  background: var(--color-bg);
  display: flex;
  justify-content: space-around;
  align-items: center;
  border: var(--border-strong);
  border-bottom: none;
  border-top-left-radius: var(--radius-card);
  border-top-right-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 100;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: rgba(0, 0, 0, 0.55);
  text-decoration: none;
  font-size: 10px;
  gap: 2px;
  transition: transform 0.12s ease, color 0.12s ease, filter 0.12s ease;
}

.tab-item .el-icon {
  font-size: 20px;
  margin-bottom: 2px;
}

.tab-item.active {
  color: var(--color-primary);
  filter: saturate(1.05);
}

.tab-item:active {
  transform: translateY(1px) scale(0.98);
  filter: brightness(0.96);
}

.tab-bar:has(.tab-item:active) {
  box-shadow: 0 4px 0 rgba(0, 0, 0, 0.9);
}

.icon-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.badge {
  position: absolute;
  top: -6px;
  right: -8px;
  background: var(--color-danger);
  color: #fff;
  font-size: 10px;
  height: 14px;
  min-width: 14px;
  line-height: 14px;
  text-align: center;
  border-radius: var(--radius-pill);
  padding: 0 4px;
  border: var(--border-strong);
}
</style>
