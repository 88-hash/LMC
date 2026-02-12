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
  background: #fff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  border-top: 1px solid #f0f0f0;
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 100;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  text-decoration: none;
  font-size: 10px;
  gap: 2px;
}

.tab-item .el-icon {
  font-size: 20px;
  margin-bottom: 2px;
}

.tab-item.active {
  color: var(--color-primary);
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
  border-radius: 7px;
  padding: 0 4px;
  border: 1px solid #fff;
}
</style>