<template>
  <div class="admin-login-bg">
    <div class="login-card animate__animated animate__fadeInDown">
      <div class="header">
        <h2 class="title">LeYi Admin System</h2>
        <p class="subtitle">乐逸零食 · 后台管理系统</p>
      </div>
      
      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="管理员账号" 
            :prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="密码" 
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-button 
          type="primary" 
          class="login-btn" 
          :loading="loading"
          @click="handleLogin"
        >
          登 录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  phone: '13800000000',
  password: '123456'
})

const rules = {
  phone: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await request.post('/admin/login', {
          phone: form.phone,
          password: form.password
        })
        
        if (res.code === 1) {
          ElMessage.success('登录成功')
          localStorage.setItem('adminToken', res.data.token)
          localStorage.setItem('adminInfo', JSON.stringify(res.data))
          router.push('/admin/dashboard')
        }
      } catch (e) {
        console.error(e)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.admin-login-bg {
  height: 100vh;
  width: 100%;
  background-color: #2d3a4b;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: radial-gradient(#374659 1px, transparent 1px);
  background-size: 20px 20px;
}

.login-card {
  width: 400px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  color: white;
  font-size: 26px;
  font-weight: 700;
  margin: 0;
  letter-spacing: 1px;
}

.subtitle {
  color: #aeb9c6;
  font-size: 14px;
  margin-top: 10px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  font-weight: 600;
  letter-spacing: 2px;
}

/* 覆盖输入框样式以适配深色背景 */
:deep(.el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.2);
  box-shadow: none;
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
}

:deep(.el-input__inner) {
  color: white;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
  background-color: rgba(0, 0, 0, 0.3);
}
</style>
