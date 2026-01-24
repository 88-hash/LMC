<template>
  <div class="profile-container">
    <div class="profile-card animate__animated animate__fadeIn">
      <div class="avatar-area">
        <div class="avatar" :style="{ backgroundImage: userInfo.avatar ? `url(${userInfo.avatar})` : '' }">
          {{ !userInfo.avatar ? (userInfo.name?.charAt(0) || 'U') : '' }}
          <div class="edit-btn" @click="handleEditProfile">
            <el-icon><Edit /></el-icon>
          </div>
        </div>
      </div>
      <h2 class="username">{{ userInfo.name || '未登录' }}</h2>
      <p class="phone">{{ userInfo.phone || '' }}</p>
      
      <div class="menu-list">
        <div class="menu-item" @click="$router.push('/order')">
          <el-icon><List /></el-icon>
          <span>我的订单</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/cart')">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="menu-item" @click="$router.push('/my-comments')">
          <el-icon><ChatLineSquare /></el-icon>
          <span>我的评价</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>
      
      <el-button type="danger" round class="logout-btn" @click="handleLogout">
        退出登录
      </el-button>
    </div>

    <!-- 编辑资料弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑资料"
      width="400px"
      destroy-on-close
    >
      <el-form label-width="60px">
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
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { List, ShoppingCart, ArrowRight, ChatLineSquare, Edit } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import request from '../utils/request'

const router = useRouter()
const userStore = useUserStore()
const userInfo = userStore.userInfo

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
            // Update Pinia store
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
.edit-btn {
    position: absolute;
    bottom: 0;
    right: 0;
    background: #409eff;
    color: white;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 14px;
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
.avatar-option.active { border-color: #409eff; box-shadow: 0 0 10px rgba(64,158,255,0.3); }

.profile-container {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px;
}

.profile-card {
  background: white;
  border-radius: 20px;
  padding: 30px 20px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  margin-top: 20px;
}

.avatar-area {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  font-weight: bold;
  background-size: cover;
  background-position: center;
}

.username {
  font-size: 20px;
  color: #333;
  margin: 0 0 5px 0;
}

.phone {
  color: #999;
  font-size: 14px;
  margin: 0 0 30px 0;
}

.menu-list {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 5px 0;
  margin-bottom: 30px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.menu-item:active {
  background: #eee;
}

.menu-item .el-icon {
  font-size: 20px;
  color: #555;
  margin-right: 12px;
}

.menu-item span {
  flex: 1;
  text-align: left;
  font-size: 16px;
  color: #333;
}

.menu-item .arrow {
  color: #ccc;
  margin-right: 0;
  font-size: 16px;
}

.logout-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 10px;
}
</style>