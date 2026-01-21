import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 前台登录
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },

  // 前台主布局与子路由
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue') },
      { path: 'cart', name: 'Cart', component: () => import('../views/Cart.vue') },
      { path: 'order', name: 'Order', component: () => import('../views/Order.vue') },
      { path: 'category', name: 'Category', component: () => import('../views/admin/Category.vue') },
      { path: 'goods', name: 'Goods', component: () => import('../views/admin/Goods.vue') }
    ]
  },

  // 后台登录
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/admin/Login.vue') },

  // 后台主布局与子路由
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '控制台' } },
      { path: 'category', name: 'AdminCategory', component: () => import('../views/admin/Category.vue'), meta: { title: '分类管理' } },
      { path: 'goods', name: 'AdminGoods', component: () => import('../views/admin/Goods.vue'), meta: { title: '商品管理' } },
      { path: 'orders', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '订单管理' } },
      { path: 'verify', name: 'AdminVerify', component: () => import('../views/admin/Verify.vue'), meta: { title: '核销中心' } }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

// 路由守卫（前台/后台分离校验）
router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/admin')) {
    if (to.path === '/admin/login') return next()
    const adminToken = localStorage.getItem('adminToken')
    return adminToken ? next() : next('/admin/login')
  } else {
    if (to.path === '/login') return next()
    const token = localStorage.getItem('token')
    return token ? next() : next('/login')
  }
})

export default router
