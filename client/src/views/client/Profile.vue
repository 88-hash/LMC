<template>
  <div class="profile-page">
    <section v-if="pageLoading" class="state-card">
      <div class="state-title">正在加载资料...</div>
      <div class="state-skeleton">
        <span></span>
        <span></span>
        <span></span>
      </div>
    </section>

    <section v-else-if="!token" class="state-card">
      <div class="state-title">你还没登录</div>
      <p class="state-desc">登录后可同步昵称、头像与订单信息。</p>
      <div class="state-actions">
        <button type="button" class="sticker-btn sticker-btn-primary" @click="goLogin">去登录</button>
        <button type="button" class="sticker-btn sticker-btn-secondary" @click="retryLoad">重试</button>
      </div>
    </section>

    <section v-else-if="pageError" class="state-card">
      <div class="state-title">资料加载失败</div>
      <p class="state-desc">{{ pageError }}</p>
      <div class="state-actions">
        <button type="button" class="sticker-btn sticker-btn-primary" @click="retryLoad">重试</button>
        <button type="button" class="sticker-btn sticker-btn-secondary" @click="goLogin">去登录</button>
      </div>
    </section>

    <template v-else>
      <section class="profile-card">
        <div class="avatar-wrap">
          <img v-if="displayProfile.avatar" :src="displayProfile.avatar" alt="avatar" class="avatar" />
          <div v-else class="avatar avatar-fallback">{{ displayInitial }}</div>
        </div>

        <div class="profile-main">
          <p class="name">{{ displayProfile.name || '未命名用户' }}</p>
          <p class="phone">{{ displayProfile.phone || '未绑定手机号' }}</p>
        </div>

        <button type="button" class="sticker-icon-btn" @click="openEditor">
          <el-icon><Edit /></el-icon>
          <span>编辑</span>
        </button>
      </section>

      <section class="menu-list">
        <button type="button" class="menu-item" @click="go('/cart')">
          <div class="menu-left">
            <span class="menu-icon icon-cart"><el-icon><ShoppingCart /></el-icon></span>
            <span class="menu-label">购物车</span>
          </div>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </button>

        <button type="button" class="menu-item" @click="go('/order')">
          <div class="menu-left">
            <span class="menu-icon icon-order"><el-icon><List /></el-icon></span>
            <span class="menu-label">我的订单</span>
          </div>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </button>

        <button type="button" class="menu-item" @click="go('/my-comments')">
          <div class="menu-left">
            <span class="menu-icon icon-comment"><el-icon><ChatLineSquare /></el-icon></span>
            <span class="menu-label">我的评价</span>
          </div>
          <el-icon class="menu-arrow"><ArrowRight /></el-icon>
        </button>
      </section>

      <section class="logout-wrap">
        <button type="button" class="sticker-btn sticker-btn-danger" @click="handleLogout">退出登录</button>
      </section>
    </template>

    <el-dialog
      v-model="dialogVisible"
      title="编辑资料"
      width="92%"
      destroy-on-close
      align-center
      class="profile-dialog"
    >
      <div class="dialog-content">
        <el-form label-width="64px">
          <el-form-item label="昵称">
            <el-input v-model="draft.name" maxlength="20" placeholder="请输入昵称" />
          </el-form-item>
        </el-form>

        <div class="avatar-editor">
          <p class="section-title">内置头像</p>
          <div class="avatar-grid">
            <button
              v-for="item in builtInAvatars"
              :key="item.id"
              type="button"
              class="avatar-option"
              :class="{ active: draft.avatar === item.url }"
              @click="selectBuiltIn(item.url)"
            >
              <img :src="item.url" :alt="item.id" />
            </button>
          </div>

          <p class="section-title">本地上传头像</p>
          <div class="upload-row">
            <input
              ref="uploadInputRef"
              class="upload-input"
              type="file"
              accept="image/*"
              @change="onUploadChange"
            />
            <button type="button" class="sticker-btn sticker-btn-secondary" @click="triggerUpload">
              选择图片
            </button>
            <span class="upload-tip">支持 jpg/png/webp，建议小于 2MB</span>
          </div>

          <div class="preview-card">
            <p class="preview-label">预览</p>
            <img v-if="draft.avatar" :src="draft.avatar" alt="preview" class="preview-avatar" />
            <div v-else class="preview-avatar preview-fallback">?</div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <button type="button" class="sticker-btn sticker-btn-secondary" @click="dialogVisible = false">取消</button>
          <button type="button" class="sticker-btn sticker-btn-primary" :disabled="saving" @click="saveProfile">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { List, ShoppingCart, ArrowRight, ChatLineSquare, Edit } from '@element-plus/icons-vue'
import { useUserStore } from '../../stores/user'
import { storeToRefs } from 'pinia'
import request from '../../utils/request'

const router = useRouter()
const userStore = useUserStore()
const { userInfo, token } = storeToRefs(userStore)

const dialogVisible = ref(false)
const saving = ref(false)
const pageLoading = ref(true)
const pageError = ref('')
const uploadInputRef = ref(null)
const localAvatar = ref('')

const draft = reactive({
  name: '',
  avatar: '',
  avatarSource: 'builtin'
})

const colorSet = [
  ['#FFE4E1', '#FF6B6B', '#111111'],
  ['#E8F4FF', '#3B82F6', '#111111'],
  ['#FFF8D9', '#F59E0B', '#111111'],
  ['#E6FFED', '#16A34A', '#111111'],
  ['#F3E8FF', '#9333EA', '#111111'],
  ['#FFEAF2', '#E11D48', '#111111'],
  ['#E6FFFA', '#0D9488', '#111111'],
  ['#F1F5F9', '#334155', '#111111']
]

const buildBuiltInAvatar = (name, bg, accent, text) => {
  const initial = name.slice(0, 1).toUpperCase()
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120"><rect width="120" height="120" rx="28" fill="${bg}"/><circle cx="60" cy="46" r="24" fill="${accent}"/><rect x="24" y="76" width="72" height="24" rx="12" fill="${accent}"/><text x="60" y="54" text-anchor="middle" font-size="18" font-family="Arial" font-weight="800" fill="${text}">${initial}</text></svg>`
  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`
}

const builtInAvatars = ['Ari', 'Bo', 'Cici', 'Dodo', 'Ena', 'Fufu', 'Gigi', 'Hana'].map((name, index) => ({
  id: `built-in-${index + 1}`,
  url: buildBuiltInAvatar(name, colorSet[index][0], colorSet[index][1], colorSet[index][2])
}))

const localAvatarKey = computed(() => {
  const uid = userInfo.value?.id || 'guest'
  return `profile_local_avatar_${uid}`
})

const displayProfile = computed(() => {
  return {
    id: userInfo.value?.id,
    name: userInfo.value?.name || '',
    phone: userInfo.value?.phone || '',
    avatar: localAvatar.value || userInfo.value?.avatar || ''
  }
})

const displayInitial = computed(() => {
  const name = displayProfile.value.name || 'U'
  return String(name).slice(0, 1).toUpperCase()
})

const go = (path) => {
  router.push(path)
}

const goLogin = () => {
  router.push('/login')
}

const loadLocalAvatar = () => {
  if (!token.value) {
    localAvatar.value = ''
    return
  }
  localAvatar.value = localStorage.getItem(localAvatarKey.value) || ''
}

const persistLocalAvatar = (avatarData) => {
  if (!token.value) return
  localStorage.setItem(localAvatarKey.value, avatarData)
  localAvatar.value = avatarData
}

const clearLocalAvatar = () => {
  if (!token.value) return
  localStorage.removeItem(localAvatarKey.value)
  localAvatar.value = ''
}

const retryLoad = async () => {
  await bootstrapPage()
}

const bootstrapPage = async () => {
  pageLoading.value = true
  pageError.value = ''
  try {
    loadLocalAvatar()
  } catch (error) {
    pageError.value = '读取用户资料失败，请稍后重试'
  } finally {
    setTimeout(() => {
      pageLoading.value = false
    }, 160)
  }
}

const openEditor = () => {
  if (!token.value) {
    ElMessage.warning('请先登录')
    goLogin()
    return
  }

  draft.name = displayProfile.value.name || ''
  draft.avatar = displayProfile.value.avatar || builtInAvatars[0].url
  draft.avatarSource = builtInAvatars.some(item => item.url === draft.avatar) ? 'builtin' : 'upload'
  dialogVisible.value = true
}

const selectBuiltIn = (url) => {
  draft.avatar = url
  draft.avatarSource = 'builtin'
}

const triggerUpload = () => {
  uploadInputRef.value?.click()
}

const onUploadChange = (event) => {
  const file = event.target?.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    event.target.value = ''
    return
  }

  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('图片请控制在 2MB 内')
    event.target.value = ''
    return
  }

  const reader = new FileReader()
  reader.onload = () => {
    const result = String(reader.result || '')
    if (!result) return
    draft.avatar = result
    draft.avatarSource = 'upload'
  }
  reader.readAsDataURL(file)
  event.target.value = ''
}

const saveProfile = async () => {
  if (!token.value) {
    ElMessage.warning('请先登录')
    goLogin()
    return
  }

  const nextName = draft.name.trim()
  if (!nextName) {
    ElMessage.warning('请输入昵称')
    return
  }
  if (!draft.avatar) {
    ElMessage.warning('请选择头像')
    return
  }

  saving.value = true

  const isUploadAvatar = draft.avatarSource === 'upload'
  const payload = { name: nextName }
  if (!isUploadAvatar) {
    payload.avatar = draft.avatar
  }

  let apiSuccess = false
  let apiData = null

  try {
    const res = await request.post('/user/update', payload, { silentError: true })
    if (res.code !== 1) {
      throw new Error(res.message || '保存失败')
    }
    apiSuccess = true
    apiData = res.data || {}
  } catch (error) {
    apiSuccess = false
  }

  if (isUploadAvatar) {
    persistLocalAvatar(draft.avatar)
  } else if (apiSuccess) {
    clearLocalAvatar()
  } else {
    persistLocalAvatar(draft.avatar)
  }

  userStore.setUser({
    ...(userInfo.value || {}),
    ...(apiData || {}),
    name: apiData?.name || nextName,
    avatar: (!isUploadAvatar && (apiData?.avatar || draft.avatar)) || userInfo.value?.avatar || ''
  })

  if (apiSuccess) {
    if (isUploadAvatar) {
      ElMessage.success('已保存：昵称走接口，上传头像本地持久化')
    } else {
      ElMessage.success('资料保存成功')
    }
  } else {
    ElMessage.success('接口不可用，已本地保存资料')
  }

  dialogVisible.value = false
  saving.value = false
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

watch(
  () => token.value,
  () => {
    bootstrapPage()
  }
)

onMounted(() => {
  bootstrapPage()
})
</script>

<style scoped>
.profile-page {
  min-height: 100%;
  max-width: 100%;
  overflow-x: hidden;
  padding: 10px 12px calc(96px + env(safe-area-inset-bottom));
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: var(--color-bg, #fffbe6);
}

.profile-page,
.profile-page * {
  box-sizing: border-box;
}

.profile-card,
.menu-item,
.state-card {
  border: var(--border-strong, 2px solid #000);
  border-radius: var(--radius-card, 20px);
  background: #fff;
  box-shadow: var(--shadow-card, 0 6px 0 #000);
}

.profile-card {
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar-wrap {
  flex-shrink: 0;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  border: var(--border-strong, 2px solid #000);
  box-shadow: 0 4px 0 #000;
  object-fit: cover;
  background: #f0f0f0;
}

.avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #444;
  font-size: 30px;
  font-weight: 900;
}

.profile-main {
  flex: 1;
  min-width: 0;
}

.name {
  margin: 0;
  color: #111;
  font-size: 20px;
  line-height: 1.2;
  font-weight: 900;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.phone {
  margin: 6px 0 0;
  color: rgba(0, 0, 0, 0.6);
  font-size: 12px;
  font-weight: 700;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sticker-icon-btn {
  flex-shrink: 0;
  min-height: 42px;
  border: var(--border-strong, 2px solid #000);
  border-radius: 999px;
  background: #ffe46f;
  box-shadow: 0 4px 0 #000;
  color: #111;
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 900;
}

.sticker-icon-btn:active {
  transform: translateY(2px);
  box-shadow: 0 2px 0 #000;
}

.menu-list {
  display: grid;
  gap: 10px;
}

.menu-item {
  width: 100%;
  min-height: 62px;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  text-align: left;
}

.menu-item:active {
  transform: translateY(2px);
  box-shadow: 0 2px 0 #000;
}

.menu-left {
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.menu-icon {
  width: 38px;
  height: 38px;
  border: var(--border-strong, 2px solid #000);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 3px 0 #000;
  font-size: 18px;
}

.icon-cart {
  background: #ffe4e6;
  color: #e11d48;
}

.icon-order {
  background: #e0ecff;
  color: #2563eb;
}

.icon-comment {
  background: #dcfce7;
  color: #16a34a;
}

.menu-label {
  font-size: 17px;
  font-weight: 900;
  color: #111;
}

.menu-arrow {
  color: rgba(0, 0, 0, 0.4);
  font-size: 18px;
}

.logout-wrap {
  padding-top: 6px;
}

.state-card {
  padding: 18px 14px;
  text-align: center;
}

.state-title {
  margin: 0;
  font-size: 20px;
  font-weight: 900;
  color: #111;
}

.state-desc {
  margin: 8px 0 0;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.62);
  font-weight: 700;
}

.state-actions {
  margin-top: 14px;
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}

.state-skeleton {
  margin-top: 12px;
  display: grid;
  gap: 8px;
}

.state-skeleton span {
  height: 10px;
  border-radius: 999px;
  background: linear-gradient(100deg, #f0efea 30%, #f8f7f1 50%, #f0efea 70%);
  background-size: 220% 100%;
  animation: shimmer 1.2s linear infinite;
}

.state-skeleton span:nth-child(1) {
  width: 68%;
  justify-self: center;
}

.state-skeleton span:nth-child(2) {
  width: 45%;
  justify-self: center;
}

.state-skeleton span:nth-child(3) {
  width: 82%;
  justify-self: center;
}

.sticker-btn {
  min-height: 42px;
  min-width: 120px;
  border: var(--border-strong, 2px solid #000);
  border-radius: 999px;
  box-shadow: 0 4px 0 #000;
  background: #fff;
  color: #111;
  font-size: 14px;
  font-weight: 900;
  padding: 8px 14px;
}

.sticker-btn:active {
  transform: translateY(2px);
  box-shadow: 0 2px 0 #000;
}

.sticker-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.sticker-btn-primary {
  background: #ffe46f;
}

.sticker-btn-secondary {
  background: #e8f1ff;
}

.sticker-btn-danger {
  width: 100%;
  color: #b42318;
  background: #ffe4e6;
}

.dialog-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.avatar-editor {
  border: var(--border-strong, 2px solid #000);
  border-radius: 16px;
  background: #fffdf5;
  box-shadow: 0 4px 0 #000;
  padding: 10px;
}

.section-title {
  margin: 0 0 8px;
  font-size: 13px;
  font-weight: 900;
  color: #111;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
  margin-bottom: 10px;
}

.avatar-option {
  border: var(--border-strong, 2px solid transparent);
  border-radius: 14px;
  padding: 2px;
  background: #fff;
}

.avatar-option.active {
  border-color: #000;
  box-shadow: 0 3px 0 #000;
}

.avatar-option img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: 10px;
  display: block;
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.upload-input {
  display: none;
}

.upload-tip {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.52);
  font-weight: 700;
}

.preview-card {
  margin-top: 10px;
  border: var(--border-strong, 2px solid #000);
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 3px 0 #000;
  padding: 10px;
  text-align: center;
}

.preview-label {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 800;
  color: rgba(0, 0, 0, 0.6);
}

.preview-avatar {
  width: 84px;
  height: 84px;
  border-radius: 18px;
  border: var(--border-strong, 2px solid #000);
  box-shadow: 0 4px 0 #000;
  object-fit: cover;
}

.preview-fallback {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #555;
  font-size: 28px;
  font-weight: 900;
  background: #f4f4f4;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  flex-wrap: wrap;
}

@keyframes shimmer {
  from {
    background-position: 180% 0;
  }
  to {
    background-position: -20% 0;
  }
}
</style>
