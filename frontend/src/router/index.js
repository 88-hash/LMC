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
      { path: 'cart', name: 'Cart', component: () => import('../views/Cart.vue'), meta: { requiresAuth: true } },
      { path: 'confirm', name: 'OrderConfirm', component: () => import('../views/OrderConfirm.vue'), meta: { requiresAuth: true } },
      { path: 'cashier', name: 'Cashier', component: () => import('../views/Cashier.vue'), meta: { requiresAuth: true } },
      { path: 'order', name: 'Order', component: () => import('../views/Order.vue'), meta: { requiresAuth: true } },
      { path: 'comment/add/:orderId', name: 'CommentAdd', component: () => import('../views/CommentAdd.vue'), meta: { requiresAuth: true } },
      { path: 'my-comments', name: 'MyComments', component: () => import('../views/MyComments.vue'), meta: { requiresAuth: true } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { requiresAuth: true } },
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
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue'), meta: { title: '控制台', requiresAuth: true } },
      { path: 'category', name: 'AdminCategory', component: () => import('../views/admin/Category.vue'), meta: { title: '分类管理', requiresAuth: true } },
      { path: 'goods', name: 'AdminGoods', component: () => import('../views/admin/Goods.vue'), meta: { title: '商品管理', requiresAuth: true } },
      { path: 'orders', name: 'AdminOrders', component: () => import('../views/admin/AdminOrders.vue'), meta: { title: '订单管理', requiresAuth: true } },
      { path: 'verify', name: 'AdminVerify', component: () => import('../views/admin/Verify.vue'), meta: { title: '核销中心', requiresAuth: true } },
      { path: 'manager', name: 'AdminManager', component: () => import('../views/admin/AdminManager.vue'), meta: { title: '店员管理', requiresAuth: true } }
    ]
  },
  
  // 404 路由 (必须放在最后)
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('../views/NotFound.vue') }
]

const router = createRouter({ history: createWebHistory(), routes })

// 路由守卫
router.beforeEach((to, from, next) => {
  // 动态设置标题
  const title = to.meta.title || '乐逸零食 - 超好吃的零食铺';
  document.title = `${title} | 乐逸零食`;

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
