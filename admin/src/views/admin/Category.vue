<template>
  <div class="category-page">
    <div class="page-header">
      <h2 class="page-title">分类管理（一级 / 二级）</h2>
      <el-button type="primary" :icon="Plus" @click="openLevel1Dialog">新增一级分类</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :span="11">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-head">
              <span>一级分类</span>
              <el-tag type="info">{{ level1List.length }}</el-tag>
            </div>
          </template>

          <el-table
            v-loading="level1Loading"
            :data="level1List"
            row-key="id"
            highlight-current-row
            @current-change="handleLevel1CurrentChange"
            style="width: 100%"
          >
            <el-table-column prop="name" label="名称" min-width="140" />
            <el-table-column prop="sort" label="排序" width="80" align="center" />
            <el-table-column label="操作" width="140" align="center">
              <template #default="{ row }">
                <el-button link type="primary" :icon="Edit" @click="openLevel1EditDialog(row)">编辑</el-button>
                <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="13">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-head">
              <span>二级分类</span>
              <div class="head-actions">
                <span class="parent-name">{{ currentLevel1Name }}</span>
                <el-button type="primary" link :disabled="!currentLevel1Id" @click="openLevel2Dialog">新增二级分类</el-button>
              </div>
            </div>
          </template>

          <el-table v-loading="level2Loading" :data="level2List" row-key="id" style="width: 100%">
            <el-table-column prop="name" label="名称" min-width="160" />
            <el-table-column prop="sort" label="排序" width="80" align="center" />
            <el-table-column label="操作" width="140" align="center">
              <template #default="{ row }">
                <el-button link type="primary" :icon="Edit" @click="openLevel2EditDialog(row)">编辑</el-button>
                <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="!level2Loading && !level2List.length" description="当前一级分类下暂无二级分类" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="460px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item v-if="dialogLevel === 2" label="所属一级">
          <el-input :model-value="currentLevel1Name" disabled />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Delete, Edit, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const level1Loading = ref(false)
const level2Loading = ref(false)
const submitLoading = ref(false)
const level1List = ref([])
const level2List = ref([])
const currentLevel1Id = ref(null)

const dialogVisible = ref(false)
const dialogLevel = ref(1)
const dialogMode = ref('add')
const formRef = ref(null)
const form = reactive({
  id: null,
  parentId: 0,
  name: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const currentLevel1Name = computed(() => {
  const target = level1List.value.find(item => item.id === currentLevel1Id.value)
  return target ? target.name : '请先选择一级分类'
})

const dialogTitle = computed(() => {
  const levelText = dialogLevel.value === 1 ? '一级分类' : '二级分类'
  const modeText = dialogMode.value === 'add' ? '新增' : '编辑'
  return `${modeText}${levelText}`
})

onMounted(async () => {
  await loadLevel1()
})

async function loadLevel1() {
  level1Loading.value = true
  try {
    const res = await request.get('/category1')
    if (res.code === 1) {
      level1List.value = res.data || []
      if (level1List.value.length > 0) {
        const existing = level1List.value.find(item => item.id === currentLevel1Id.value)
        currentLevel1Id.value = existing ? existing.id : level1List.value[0].id
        await loadLevel2(currentLevel1Id.value)
      } else {
        currentLevel1Id.value = null
        level2List.value = []
      }
    }
  } finally {
    level1Loading.value = false
  }
}

async function loadLevel2(parentId) {
  if (!parentId) {
    level2List.value = []
    return
  }
  level2Loading.value = true
  try {
    const res = await request.get('/category2', { params: { parentId } })
    if (res.code === 1) {
      level2List.value = res.data || []
    }
  } finally {
    level2Loading.value = false
  }
}

function handleLevel1CurrentChange(row) {
  if (!row?.id) return
  currentLevel1Id.value = row.id
  loadLevel2(row.id)
}

function resetForm() {
  form.id = null
  form.parentId = dialogLevel.value === 1 ? 0 : Number(currentLevel1Id.value)
  form.name = ''
  form.sort = 0
}

function openLevel1Dialog() {
  dialogMode.value = 'add'
  dialogLevel.value = 1
  resetForm()
  dialogVisible.value = true
}

function openLevel2Dialog() {
  if (!currentLevel1Id.value) {
    ElMessage.warning('请先选择一级分类')
    return
  }
  dialogMode.value = 'add'
  dialogLevel.value = 2
  resetForm()
  dialogVisible.value = true
}

function openLevel1EditDialog(row) {
  dialogMode.value = 'edit'
  dialogLevel.value = 1
  form.id = row.id
  form.parentId = 0
  form.name = row.name
  form.sort = row.sort ?? 0
  dialogVisible.value = true
}

function openLevel2EditDialog(row) {
  dialogMode.value = 'edit'
  dialogLevel.value = 2
  form.id = row.id
  form.parentId = row.parentId || Number(currentLevel1Id.value)
  form.name = row.name
  form.sort = row.sort ?? 0
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (dialogLevel.value === 2 && !form.parentId) {
    ElMessage.warning('二级分类必须绑定一级分类')
    return
  }

  submitLoading.value = true
  try {
    const url = dialogMode.value === 'add' ? '/category/add' : '/category/update'
    const payload = {
      id: form.id,
      parentId: dialogLevel.value === 1 ? 0 : Number(form.parentId),
      name: form.name,
      sort: Number(form.sort) || 0
    }
    const res = await request.post(url, payload)
    if (res.code === 1) {
      ElMessage.success(dialogMode.value === 'add' ? '新增成功' : '修改成功')
      dialogVisible.value = false
      await loadLevel1()
      if (dialogLevel.value === 2) {
        await loadLevel2(Number(payload.parentId))
      }
    }
  } finally {
    submitLoading.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确认删除分类「${row.name}」吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await request.get('/category/delete', { params: { id: row.id, parentId: row.parentId ?? 0 } })
    if (res.code === 1) {
      ElMessage.success('删除成功')
      await loadLevel1()
      if (currentLevel1Id.value) {
        await loadLevel2(currentLevel1Id.value)
      }
    }
  })
}
</script>

<style scoped>
.category-page {
  padding: 10px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.panel-card {
  min-height: 560px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  font-weight: 600;
}

.head-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.parent-name {
  font-size: 12px;
  color: #909399;
}
</style>

