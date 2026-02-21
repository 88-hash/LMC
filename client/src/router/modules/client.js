const clientRoutes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../../views/client/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../../views/client/Layout.vue'),
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../../views/client/Home.vue'),
        meta: { title: '乐逸零食' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('../../views/client/Cart.vue'),
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'confirm',
        name: 'OrderConfirm',
        component: () => import('../../views/client/OrderConfirm.vue'),
        meta: { title: '确认订单', requiresAuth: true }
      },
      {
        path: 'cashier',
        name: 'Cashier',
        component: () => import('../../views/client/Cashier.vue'),
        meta: { title: '收银台', requiresAuth: true }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('../../views/client/Order.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'comment/add/:orderId',
        name: 'CommentAdd',
        component: () => import('../../views/client/CommentAdd.vue'),
        meta: { title: '发表评价', requiresAuth: true }
      },
      {
        path: 'my-comments',
        name: 'MyComments',
        component: () => import('../../views/client/MyComments.vue'),
        meta: { title: '我的评价', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../../views/client/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'goods/:id',
        name: 'GoodsDetail',
        component: () => import('../../views/client/GoodsDetail.vue'),
        meta: { title: '商品详情' }
      }
    ]
  }
]

export default clientRoutes
