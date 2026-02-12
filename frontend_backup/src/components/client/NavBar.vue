<template>
  <div class="nav-bar">
    <div class="left">
      <span class="logo">🍿</span>
      <span class="title">乐逸零食</span>
    </div>
    <div class="right">
      <!-- 只有未登录时显示登录按钮 -->
      <el-button v-if="!userStore.token" type="primary" size="small" @click="$router.push('/login')" round>
        登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '../../stores/user'
const userStore = useUserStore()
</script>

<style scoped>
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 46px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  z-index: 100;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
  color: var(--color-primary);
}

.logo {
  font-size: 20px;
}
</style>