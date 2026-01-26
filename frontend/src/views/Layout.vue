<template>
  <div class="app-layout app-shell">
    <NavBar />
    
    <main class="app-main content-area">
      <router-view v-slot="{ Component }">
        <transition name="slide-fade" mode="out-in">
          <component :is="Component" :key="$route.path" />
        </transition>
      </router-view>
    </main>

    <TabBar v-if="showTabBar" />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import TabBar from '../components/TabBar.vue'
import { useCartStore } from '../stores/cart'

const route = useRoute()
const cartStore = useCartStore()

// 仅在主要页面显示 TabBar，或者一直显示？
// 通常二级页面（如订单详情）不显示 TabBar。
// 这里简单逻辑：一直显示，或者根据 meta 配置。
// 让我们根据路由名判断，只在 Tab 页显示 TabBar，避免遮挡底部操作按钮（如“立即支付”）
const showTabBar = computed(() => {
  return ['Home', 'Cart', 'Order', 'Profile'].includes(route.name)
})

onMounted(() => {
  cartStore.refresh()
})
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  background-color: var(--color-bg);
}

.app-main {
  padding-top: 46px; /* NavBar Height */
  padding-bottom: calc(50px + env(safe-area-inset-bottom)); /* TabBar Height */
  min-height: 100vh;
  overflow-x: hidden;
}

/* Slide Fade Animation */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from {
  transform: translateX(20px);
  opacity: 0;
}

.slide-fade-leave-to {
  transform: translateX(-20px);
  opacity: 0;
}
</style>
