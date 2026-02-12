import { createRouter, createWebHistory } from 'vue-router'
import clientRoutes from './modules/client'
import adminRoutes from './modules/admin'

const routes = [
  ...clientRoutes,
  ...adminRoutes,
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

  // 后台鉴权
  if (to.path.startsWith('/admin')) {
    if (to.path === '/admin/login') return next()
    const adminToken = localStorage.getItem('adminToken')
    return adminToken ? next() : next('/admin/login')
  } 
  
  // 前台鉴权 (只拦截 requiresAuth)
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      return next('/login')
    }
  }
  next()
})

export default router