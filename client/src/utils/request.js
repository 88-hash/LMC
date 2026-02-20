import axios from 'axios'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false, minimum: 0.3 })

const request = axios.create({
  baseURL: '/api',
  timeout: 5000
})

request.interceptors.request.use(
  (config) => {
    NProgress.start()

    let token = null
    if (config.url && config.url.includes('/admin')) {
      token = localStorage.getItem('adminToken')
    } else {
      token = localStorage.getItem('token')
    }

    if (token) {
      config.headers['Authorization'] = token
    }

    return config
  },
  (error) => {
    NProgress.done()
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (res) => {
    NProgress.done()
    const data = res.data
    const silentError = Boolean(res.config?.silentError)

    if (data.code === 1) {
      return data
    }

    const msg = data.message || data.msg || '网络异常'
    if (!silentError) {
      ElMessage.error(msg)
    }
    return Promise.reject(new Error(msg))
  },
  (error) => {
    NProgress.done()

    const silentError = Boolean(error.config?.silentError)

    if (error.response && error.response.status === 401 && !silentError) {
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

    if (!silentError) {
      ElMessage.error(error.message || '网络异常')
    }

    return Promise.reject(error)
  }
)

export default request
