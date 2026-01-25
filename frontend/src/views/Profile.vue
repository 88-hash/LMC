<template>
  <div class="profile-page">
    <!-- 顶部波浪背景 -->
    <div class="header-bg">
      <div class="wave"></div>
    </div>

    <div class="profile-content animate__animated animate__fadeInUp">
      <!-- 个人信息卡片 -->
      <div class="user-card">
        <div class="avatar-wrapper">
          <div class="avatar" :style="{ backgroundImage: userInfo.avatar ? `url(${userInfo.avatar})` : '' }">
            {{ !userInfo.avatar ? (userInfo.name?.charAt(0) || 'U') : '' }}
          </div>
          <div class="edit-btn" @click="handleEditProfile">
            <el-icon><Edit /></el-icon>
          </div>
        </div>
        <h2 class="username">{{ userInfo.name || '未登录' }}</h2>
        <p class="phone">{{ userInfo.phone || '点击编辑完善资料' }}</p>
      </div>
      
      <!-- 菜单卡片组 -->
      <div class="menu-grid">
        <div class="menu-card" @click="$router.push('/order')">
          <div class="icon-box" style="background: #e3f2fd; color: #409eff">
            <el-icon><List /></el-icon>
          </div>
          <span class="label">我的订单</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>

        <div class="menu-card" @click="$router.push('/cart')">
          <div class="icon-box" style="background: #fff0f0; color: #ff6b6b">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <span class="label">购物车</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>

        <div class="menu-card" @click="$router.push('/my-comments')">
          <div class="icon-box" style="background: #e8f5e9; color: #67c23a">
            <el-icon><ChatLineSquare /></el-icon>
          </div>
          <span class="label">我的评价</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        
      </div>
      
      <div class="logout-section">
        <el-button class="logout-btn" @click="handleLogout">
          退出登录
        </el-button>
      </div>
    </div>

    <!-- 编辑资料弹窗 (保持不变) -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑资料"
      width="80%"
      center
      align-center
      destroy-on-close
      class="custom-dialog"
    >
      <el-form label-width="50px">
        <el-form-item label="昵称">
          <el-input v-model="form.name" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="头像">
          <div class="avatar-grid">
            <img 
                v-for="(url, idx) in presetAvatars" 
                :key="idx"
                :src="url"
                class="avatar-option"
                :class="{ active: form.avatar === url }"
                @click="selectAvatar(url)"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button round @click="dialogVisible = false">取消</el-button>
          <el-button round type="primary" @click="handleSave" :loading="loading">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// ... (script part remains mostly same, just ensuring imports are correct)
import { ref, reactive, toRefs } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { List, ShoppingCart, ArrowRight, ChatLineSquare, Edit } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { storeToRefs } from 'pinia'
import request from '../utils/request'

const router = useRouter()
const userStore = useUserStore()
const { userInfo } = storeToRefs(userStore)

const dialogVisible = ref(false)
const loading = ref(false)
const form = reactive({
    name: '',
    avatar: ''
})

const presetAvatars = [
    'https://api.dicebear.com/7.x/adventurer/svg?seed=Felix',
    'https://api.dicebear.com/7.x/adventurer/svg?seed=Bella',
    'https://api.dicebear.com/7.x/adventurer/svg?seed=Coco',
    'https://api.dicebear.com/7.x/adventurer/svg?seed=Max',
    'https://api.dicebear.com/7.x/adventurer/svg?seed=Luna',
    'https://api.dicebear.com/7.x/adventurer/svg?seed=Oliver'
]

const handleEditProfile = () => {
    form.name = userInfo.value.name
    form.avatar = userInfo.value.avatar
    dialogVisible.value = true
}

const selectAvatar = (url) => {
    form.avatar = url
}

const handleSave = async () => {
    if (!form.name) {
        ElMessage.warning('请输入昵称')
        return
    }
    loading.value = true
    try {
        const res = await request.post('/user/update', form)
        if (res.code === 1) {
            ElMessage.success('修改成功')
            userStore.setUser({
                ...userInfo.value,
                name: res.data.name,
                avatar: res.data.avatar
            })
            dialogVisible.value = false
        } else {
            ElMessage.error(res.message)
        }
    } catch(e) {
        console.error(e)
    } finally {
        loading.value = false
    }
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--color-bg);
  position: relative;
  overflow-x: hidden;
}

.header-bg {
  height: 200px;
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%);
  border-bottom-left-radius: 40px;
  border-bottom-right-radius: 40px;
  position: relative;
  box-shadow: 0 10px 30px rgba(255, 154, 158, 0.3);
}

.profile-content {
  padding: 0 20px;
  margin-top: -60px;
  position: relative;
  z-index: 10;
  padding-bottom: 80px;
}

.user-card {
  background: white;
  border-radius: 24px;
  padding: 30px 20px;
  text-align: center;
  box-shadow: var(--shadow-float);
  margin-bottom: 24px;
}

.avatar-wrapper {
  position: relative;
  width: 90px;
  height: 90px;
  margin: 0 auto 16px;
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #f0f0f0;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: #999;
  background-size: cover;
  background-position: center;
}

.edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  background: var(--color-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  border: 2px solid white;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.username {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin: 0 0 4px;
}

.phone {
  font-size: 13px;
  color: #999;
  margin: 0;
}

.menu-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu-card {
  background: white;
  border-radius: 16px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  box-shadow: var(--shadow-card);
  transition: transform 0.2s;
  cursor: pointer;
}

.menu-card:active {
  transform: scale(0.98);
}

.icon-box {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
}

.label {
  flex: 1;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.arrow {
  color: #ccc;
}

.logout-section {
  margin-top: 30px;
  padding: 0 20px;
}

.logout-btn {
  width: 100%;
  height: 48px;
  border-radius: 24px;
  background: white;
  border: 1px solid #fee;
  color: #ff4d4f;
  font-weight: 600;
  box-shadow: var(--shadow-card);
}

.avatar-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
    margin-top: 10px;
}

.avatar-option {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    cursor: pointer;
    border: 2px solid transparent;
    transition: all 0.2s;
}

.avatar-option:hover { transform: scale(1.1); }
.avatar-option.active { border-color: var(--color-primary); box-shadow: 0 0 10px rgba(255,107,107,0.3); }
</style>
