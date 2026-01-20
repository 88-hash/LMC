<template>
  <div class="login-bg">
    <div class="login-card animate__animated animate__fadeInUp">
      <div class="logo-area">
        <div class="emoji">🍪</div>
        <h2>乐逸零食 · 顾客登录</h2>
        <p>开启您的美味之旅</p>
      </div>

      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form">
        <el-form-item prop="phone">
          <el-input
            v-model="loginForm.phone"
            placeholder="请输入手机号"
            size="large"
            :prefix-icon="Iphone"
          />
        </el-form-item>

        <el-form-item prop="code">
          <div class="code-row">
            <el-input
              v-model="loginForm.code"
              placeholder="请输入验证码"
              size="large"
              :prefix-icon="Key"
            />
            <el-button 
              type="primary" 
              plain 
              size="large" 
              :disabled="timer > 0"
              @click="handleSendCode"
              class="code-btn"
            >
              {{ timer > 0 ? `${timer}s后重试` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-button 
          type="primary" 
          size="large" 
          class="login-btn" 
          :loading="loading"
          @click="handleLogin"
        >
          立即登录
        </el-button>
      </el-form>

      <div class="footer-tips">
        未注册手机号验证后将自动创建账号
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Iphone, Key } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const timer = ref(0)

const loginForm = reactive({
  phone: '',
  code: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码为4位数字', trigger: 'blur' }
  ]
}

// 模拟发送验证码
const handleSendCode = () => {
  if (!/^1[3-9]\d{9}$/.test(loginForm.phone)) {
    return ElMessage.warning('请先输入正确的手机号')
  }
  
  ElMessage.success('验证码已发送：1234')
  timer.value = 60
  const interval = setInterval(() => {
    timer.value--
    if (timer.value <= 0) {
      clearInterval(interval)
    }
  }, 1000)
}

// 登录逻辑
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      if (loginForm.code !== '1234') {
        return ElMessage.error('验证码错误 (测试验证码: 1234)')
      }

      loading.value = true
      try {
        // 后端接口是 POST /user/login?phone=xxx
        // axios post 的第二个参数是 body，第三个参数是 config (包含 params)
        const res = await request.post('/user/login', null, {
          params: {
            phone: loginForm.phone
          }
        })

        if (res.code === 1) {
          ElMessage.success('登录成功，欢迎回来！')
          localStorage.setItem('token', res.data.token)
          // 也可以存一下用户信息
          localStorage.setItem('userInfo', JSON.stringify(res.data))
          
          router.push('/')
        }
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-bg {
  height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  /* 糖果色渐变背景 */
  background: linear-gradient(135deg, #fff1eb 0%, #ace0f9 100%);
  background-size: 200% 200%;
  animation: gradientBG 15s ease infinite;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.login-card {
  width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.logo-area {
  text-align: center;
  margin-bottom: 40px;
}

.emoji {
  font-size: 48px;
  margin-bottom: 10px;
  animation: bounce 2s infinite;
  display: inline-block;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.logo-area h2 {
  color: #2c3e50;
  font-size: 24px;
  margin: 0;
  font-weight: 600;
  letter-spacing: 1px;
}

.logo-area p {
  color: #95a5a6;
  font-size: 14px;
  margin-top: 8px;
}

.code-row {
  display: flex;
  gap: 12px;
}

.code-btn {
  width: 130px;
}

.login-btn {
  width: 100%;
  margin-top: 20px;
  background: linear-gradient(45deg, #ff6b6b, #ff8e53);
  border: none;
  font-weight: 600;
  letter-spacing: 2px;
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(255, 107, 107, 0.4);
}

.footer-tips {
  text-align: center;
  margin-top: 25px;
  font-size: 12px;
  color: #bdc3c7;
}

/* 覆盖 Element 样式 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e0e0e0 inset;
  padding: 8px 15px;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #ff8e53 inset !important;
}
</style>
