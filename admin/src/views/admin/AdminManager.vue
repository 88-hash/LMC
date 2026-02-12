<template>
  <div class="manager-container animate__animated animate__fadeIn">
    <!-- 头部操作区 -->
    <div class="page-header">
      <h2 class="page-title">店员管理</h2>
      <el-button v-if="isManager" type="primary" :icon="Plus" @click="handleAdd">新增店员</el-button>
    </div>

    <!-- 表格区域 -->
    <el-card shadow="never" class="table-card">
      <el-table 
        v-loading="loading"
        :data="tableData" 
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        
        <el-table-column prop="name" label="姓名" width="120" />

        <el-table-column prop="phone" label="手机号" width="150" />

        <el-table-column prop="role" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.role === 'manager' || row.role === 'admin'" type="danger" effect="dark">店长</el-tag>
            <el-tag v-else type="primary" effect="light">店员</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="注册时间" min-width="160">
            <template #default="{ row }">
                {{ formatTime(row.createdAt) }}
            </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <template v-if="isManager && row.role !== 'admin' && row.role !== 'manager'">
                <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
                <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
            </template>
            <span v-else class="no-perm">无权操作</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码 (留空则不修改)" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增店员')
const submitLoading = ref(false)
const formRef = ref(null)

const currentUser = JSON.parse(localStorage.getItem('adminInfo') || '{}')
const isManager = computed(() => currentUser.role === 'manager' || currentUser.role === 'admin')

const form = reactive({
  id: null,
  name: '',
  phone: '',
  password: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: false, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

onMounted(() => {
  fetchData()
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/auth/list')
    if (res.code === 1) {
      tableData.value = res.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增店员'
  Object.assign(form, { id: null, name: '', phone: '', password: '' })
  // 新增时密码必填
  rules.password[0].required = true
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑店员'
  Object.assign(form, { id: row.id, name: row.name, phone: row.phone, password: '' })
  // 编辑时密码选填
  rules.password[0].required = false
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除店员 "${row.name}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.get(`/admin/auth/clerk/delete?id=${row.id}`)
      if (res.code === 1) {
        ElMessage.success('删除成功')
        fetchData()
      }
    } catch (e) {
      console.error(e)
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const url = form.id ? '/admin/auth/clerk/update' : '/admin/auth/clerk/add'
        const res = await request.post(url, form)
        
        if (res.code === 1) {
          ElMessage.success(form.id ? '修改成功' : '新增成功')
          dialogVisible.value = false
          fetchData()
        } else {
            ElMessage.error(res.message)
        }
      } catch (e) {
        console.error(e)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : ''
}
</script>

<style scoped>
.manager-container {
  padding: 10px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  border-left: 4px solid #409eff;
  padding-left: 10px;
}

.table-card {
  border-radius: 8px;
  border: none;
}

.no-perm {
    color: #999;
    font-size: 12px;
}
</style>
