import { createRouter, createWebHistory } from 'vue-router'
import clientRoutes from './modules/client'

const routes = [
  ...clientRoutes,
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const title = to.meta.title || '李先生的小店 - 超好吃的零食铺'
  document.title = `${title} | 李先生的小店`

  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }

  next()
})

export default router
