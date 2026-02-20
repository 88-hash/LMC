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

  // Admin 站点下的所有接口都应优先携带 adminToken。
  let token = null
  const isAdminPage = window.location.pathname.startsWith('/admin')
  if (isAdminPage) {
    token = localStorage.getItem('adminToken')
  }
  if (!token) {
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
    const msg = data.message || data.msg || '网络异常'
    ElMessage.error(msg)
    return Promise.reject(msg)
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
