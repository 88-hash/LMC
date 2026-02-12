const adminRoutes = [
  // 后台登录
  { 
    path: '/admin/login', 
    name: 'AdminLogin', 
    component: () => import('../../views/admin/Login.vue') 
  },

  // 后台主布局与子路由
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../../views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { 
        path: 'dashboard', 
        name: 'AdminDashboard', 
        component: () => import('../../views/admin/Dashboard.vue'), 
        meta: { title: '控制台', requiresAuth: true } 
      },
      { 
        path: 'category', 
        name: 'AdminCategory', 
        component: () => import('../../views/admin/Category.vue'), 
        meta: { title: '分类管理', requiresAuth: true } 
      },
      { 
        path: 'goods', 
        name: 'AdminGoods', 
        component: () => import('../../views/admin/Goods.vue'), 
        meta: { title: '商品管理', requiresAuth: true } 
      },
      { 
        path: 'orders', 
        name: 'AdminOrders', 
        component: () => import('../../views/admin/AdminOrders.vue'), 
        meta: { title: '订单管理', requiresAuth: true } 
      },
      { 
        path: 'verify', 
        name: 'AdminVerify', 
        component: () => import('../../views/admin/Verify.vue'), 
        meta: { title: '核销中心', requiresAuth: true } 
      },
      { 
        path: 'manager', 
        name: 'AdminManager', 
        component: () => import('../../views/admin/AdminManager.vue'), 
        meta: { title: '店员管理', requiresAuth: true } 
      }
    ]
  }
]

export default adminRoutes