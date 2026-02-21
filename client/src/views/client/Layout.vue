<template>
  <div class="app-layout">
    <NavBar />

    <main class="app-main" :class="{ 'with-tabbar': showTabBar }">
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
import NavBar from '../../components/client/NavBar.vue'
import TabBar from '../../components/client/TabBar.vue'
import { useCartStore } from '../../stores/cart'

const route = useRoute()
const cartStore = useCartStore()

const TABBAR_PATHS = new Set(['/home', '/cart', '/order', '/profile'])

const showTabBar = computed(() => TABBAR_PATHS.has(route.path))

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
  padding-top: 46px;
  padding-bottom: env(safe-area-inset-bottom);
  min-height: 100vh;
}

.app-main.with-tabbar {
  padding-bottom: calc(58px + env(safe-area-inset-bottom));
}

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
