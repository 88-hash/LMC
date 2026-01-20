import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // C端路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('../views/Home.vue')
      }
    ]
  },
  
  // B端(管理后台)路由
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/admin/Login.vue')
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'category',
        name: 'AdminCategory',
        component: () => import('../views/admin/Category.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'goods',
        name: 'AdminGoods',
        component: () => import('../views/admin/Goods.vue'),
        meta: { title: '商品管理' }
      },
      // 占位路由，防止点击菜单报错
      { path: 'orders', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '订单管理' } },
      { path: 'verify', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '核销中心' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 判断是去后台还是前台
  if (to.path.startsWith('/admin')) {
    // 后台逻辑
    if (to.path === '/admin/login') {
      next()
    } else {
      const adminToken = localStorage.getItem('adminToken')
      if (!adminToken) {
        next('/admin/login')
      } else {
        next()
      }
    }
  } else {
    // 前台逻辑
    if (to.path === '/login') {
      next()
    } else {
      const token = localStorage.getItem('token')
      if (!token) {
        next('/login')
      } else {
        next()
      }
    }
  }
})

export default router
