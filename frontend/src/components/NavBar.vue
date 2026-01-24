<template>
  <div class="nav-bar">
    <div class="left">
      <div v-if="showBack" class="back-btn" @click="router.back()">
        <el-icon :size="20"><ArrowLeft /></el-icon>
      </div>
    </div>
    <div class="center">
      <span class="title">{{ title }}</span>
    </div>
    <div class="right"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const showBack = computed(() => {
  const tabPages = ['Home', 'Cart', 'Order', 'Profile']
  return !tabPages.includes(route.name)
})

const title = computed(() => route.meta.title || '乐逸零食')
</script>

<style scoped>
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  z-index: 1000;
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.05);
}

.left, .right {
  width: 40px;
  display: flex;
  align-items: center;
}

.center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.back-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  cursor: pointer;
  color: #2c3e50;
}

.back-btn:active {
  background: rgba(0, 0, 0, 0.05);
}
</style>