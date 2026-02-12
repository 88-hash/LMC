import axios from 'axios'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// NProgress 配置
NProgress.configure({ showSpinner: false, minimum: 0.3 })

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(config => {
  NProgress.start()
  
  // 智能鉴权：根据请求路径或页面路径判断使用哪个 Token
  // 如果当前是后台页面请求，优先使用 adminToken
  // 简单策略：尝试获取 adminToken，如果存在且请求的是后台接口，则使用
  // 但更稳妥的是：
  // 1. 如果请求 URL 包含 /admin，优先取 adminToken
  // 2. 否则取 token
  
  let token = null
  if (config.url.includes('/admin')) {
    token = localStorage.getItem('adminToken')
  } else {
    token = localStorage.getItem('token')
  }
  
  if (token) {
    config.headers['Authorization'] = token
  }
  return config
}, error => {
  NProgress.done()
  return Promise.reject(error)
})

// 响应拦截器
request.interceptors.response.use(res => {
  NProgress.done()
  const data = res.data
  if (data.code === 1) {
    return data
  } else {
    ElMessage.error(data.msg || '网络异常')
    return Promise.reject(data.msg)
  }
}, error => {
  NProgress.done()
  if (error.response && error.response.status === 401) {
    // 区分前后端跳转
    const isAdmin = window.location.pathname.startsWith('/admin')
    if (isAdmin) {
        localStorage.removeItem('adminToken')
        localStorage.removeItem('adminInfo')
        window.location.href = '/admin/login'
    } else {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
    }
  }
  ElMessage.error(error.message || '网络异常')
  return Promise.reject(error)
})

export default request
