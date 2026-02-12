import { createRouter, createWebHistory } from 'vue-router'
import clientRoutes from './modules/client'

const routes = [
  ...clientRoutes,
  // 404 路由 (必须放在最后)
  { 
    path: '/:pathMatch(.*)*', 
    name: 'NotFound', 
    component: () => import('../views/NotFound.vue') 
  }
]

const router = createRouter({ history: createWebHistory(), routes })

// 路由守卫
router.beforeEach((to, from, next) => {
  // 动态设置标题
  const title = to.meta.title || '李先生的小店 - 超好吃的零食铺';
  document.title = `${title} | 李先生的小店`;

  // 前台鉴权 (只拦截 requiresAuth)
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth) {
    if (!token) {
      return next('/login')
    }
  } else if (to.path === '/' && !token) {
     // 如果访问根路径且未登录，强制跳转到登录页
     return next('/login')
  }
  next()
})

export default router