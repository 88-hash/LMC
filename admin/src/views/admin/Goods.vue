<template>
  <div class="goods-container animate__animated animate__fadeIn">
    <div class="page-header">
      <h2 class="page-title">商品管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增商品</el-button>
    </div>

    <el-card shadow="never" class="toolbar-card">
      <el-form inline>
        <el-form-item label="一级分类">
          <el-select v-model="filter.category1Id" clearable placeholder="全部一级" style="width: 180px" @change="handleFilterCategory1Change">
            <el-option v-for="item in level1List" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="二级分类">
          <el-select v-model="filter.category2Id" clearable placeholder="全部二级" style="width: 200px">
            <el-option v-for="item in filterLevel2Options" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">筛选</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />

        <el-table-column label="图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              :src="row.imageUrl || 'https://via.placeholder.com/100?text=NoImg'"
              :preview-src-list="[row.imageUrl]"
              style="width: 60px; height: 60px; border-radius: 4px"
              fit="cover"
            />
          </template>
        </el-table-column>

        <el-table-column prop="name" label="商品名称" min-width="160" show-overflow-tooltip />

        <el-table-column label="分类" min-width="180">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ getCategoryPath(row) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="price" label="价格" width="110">
          <template #default="{ row }">
            <span class="price-text">￥ {{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="stock" label="库存" width="90" align="center">
          <template #default="{ row }">
            <span :class="{ 'stock-warning': row.stock < 10 }">{{ row.stock }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="isOnSale" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isOnSale === 1 ? 'success' : 'danger'">{{ row.isOnSale === 1 ? '已上架' : '已下架' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" :icon="ChatLineSquare" @click="handleReview(row)">评价</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="640px" destroy-on-close top="5vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="一级分类" prop="category1Id">
              <el-select v-model="form.category1Id" placeholder="请选择一级分类" style="width: 100%" @change="handleFormCategory1Change">
                <el-option v-for="item in level1List" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="二级分类" prop="category2Id">
              <el-select v-model="form.category2Id" placeholder="请选择二级分类" style="width: 100%">
                <el-option v-for="item in formLevel2Options" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="条形码" prop="barCode">
          <el-input v-model="form.barCode" placeholder="扫描或输入条形码" />
        </el-form-item>

        <el-form-item label="图片链接" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL">
            <template #append>
              <el-popover placement="top" :width="200" trigger="hover">
                <template #reference>
                  <el-icon><Picture /></el-icon>
                </template>
                <img :src="form.imageUrl || 'https://via.placeholder.com/200'" style="width: 100%" />
              </el-popover>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="保质期" prop="expireDate">
          <el-date-picker v-model="form.expireDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>

        <el-form-item label="状态" prop="isOnSale">
          <el-radio-group v-model="form.isOnSale">
            <el-radio :label="1">上架销售</el-radio>
            <el-radio :label="0">下架仓库</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="reviewDrawerVisible" title="商品评价" size="400px">
      <div v-loading="reviewLoading">
        <el-empty v-if="reviews.length === 0" description="暂无评价" />
        <div v-else class="review-list">
          <div v-for="r in reviews" :key="r.id" class="review-item">
            <div class="r-head">
              <span class="r-user">{{ maskPhone(r.userPhone) }}</span>
              <el-rate v-model="r.rating" disabled size="small" text-color="#ff9900" />
            </div>
            <div class="r-content">{{ r.content }}</div>
            <div class="r-time">{{ formatTime(r.createdAt) }}</div>
          </div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ChatLineSquare, Delete, Edit, Picture, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const level1List = ref([])
const level2Map = ref({})

const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')
const submitLoading = ref(false)
const formRef = ref(null)

const reviewDrawerVisible = ref(false)
const reviewLoading = ref(false)
const reviews = ref([])

const filter = reactive({
  category1Id: null,
  category2Id: null
})

const form = reactive({
  id: null,
  name: '',
  category1Id: null,
  category2Id: null,
  price: 0,
  stock: 100,
  barCode: '',
  imageUrl: '',
  expireDate: null,
  isOnSale: 1,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category1Id: [{ required: true, message: '请选择一级分类', trigger: 'change' }],
  category2Id: [{ required: true, message: '请选择二级分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const level2FlatMap = computed(() => {
  const map = {}
  Object.values(level2Map.value).forEach(items => {
    ;(items || []).forEach(item => {
      map[item.id] = item
    })
  })
  return map
})

const filterLevel2Options = computed(() => {
  if (!filter.category1Id) {
    return Object.values(level2Map.value).flat()
  }
  return level2Map.value[filter.category1Id] || []
})

const formLevel2Options = computed(() => {
  if (!form.category1Id) return []
  return level2Map.value[form.category1Id] || []
})

onMounted(async () => {
  await fetchCategories()
  await fetchData()
})

async function fetchCategories() {
  const level1Res = await request.get('/category1')
  if (level1Res.code !== 1) return
  level1List.value = level1Res.data || []

  const map = {}
  await Promise.all(
    level1List.value.map(async item => {
      const res = await request.get('/category2', { params: { parentId: item.id } })
      map[item.id] = res.code === 1 ? (res.data || []) : []
    })
  )
  level2Map.value = map
}

async function fetchData() {
  loading.value = true
  try {
    const params = {}
    if (filter.category1Id) params.category1Id = filter.category1Id
    if (filter.category2Id) params.category2Id = filter.category2Id

    const endpoint = Object.keys(params).length > 0 ? '/goods' : '/goods/list'
    const res = await request.get(endpoint, { params })
    if (res.code === 1) {
      tableData.value = res.data || []
    }
  } finally {
    loading.value = false
  }
}

function handleFilterCategory1Change() {
  filter.category2Id = null
}

function resetFilter() {
  filter.category1Id = null
  filter.category2Id = null
  fetchData()
}

function getCategoryPath(row) {
  const category2Id = Number(row.category2Id || row.categoryId)
  const category2 = level2FlatMap.value[category2Id]
  if (!category2) return '未分类'
  const category1 = level1List.value.find(item => item.id === category2.parentId)
  return `${category1?.name || '未知一级'} / ${category2.name}`
}

function resetForm() {
  const firstLevel1 = level1List.value[0]?.id || null
  const firstLevel2 = firstLevel1 ? (level2Map.value[firstLevel1]?.[0]?.id || null) : null
  Object.assign(form, {
    id: null,
    name: '',
    category1Id: firstLevel1,
    category2Id: firstLevel2,
    price: 0,
    stock: 100,
    barCode: '',
    imageUrl: '',
    expireDate: null,
    isOnSale: 1,
    description: ''
  })
}

function handleAdd() {
  dialogTitle.value = '新增商品'
  resetForm()
  dialogVisible.value = true
}

function handleFormCategory1Change() {
  const options = level2Map.value[form.category1Id] || []
  form.category2Id = options.length ? options[0].id : null
}

function handleEdit(row) {
  dialogTitle.value = '编辑商品'
  const category2Id = Number(row.category2Id || row.categoryId)
  const category2 = level2FlatMap.value[category2Id]
  Object.assign(form, {
    id: row.id,
    name: row.name,
    category1Id: category2?.parentId || null,
    category2Id,
    price: row.price,
    stock: row.stock,
    barCode: row.barCode,
    imageUrl: row.imageUrl,
    expireDate: row.expireDate,
    isOnSale: row.isOnSale,
    description: row.description
  })
  dialogVisible.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除商品 "${row.name}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await request.get('/goods/delete', { params: { id: row.id } })
    if (res.code === 1) {
      ElMessage.success('删除成功')
      fetchData()
    }
  })
}

async function handleSubmit() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const payload = {
      id: form.id,
      name: form.name,
      category1Id: form.category1Id,
      category2Id: form.category2Id,
      categoryId: form.category2Id,
      price: form.price,
      stock: form.stock,
      barCode: form.barCode,
      imageUrl: form.imageUrl,
      expireDate: form.expireDate,
      isOnSale: form.isOnSale,
      description: form.description
    }
    const url = form.id ? '/goods/update' : '/goods/add'
    const res = await request.post(url, payload)
    if (res.code === 1) {
      ElMessage.success(form.id ? '修改成功' : '新增成功')
      dialogVisible.value = false
      fetchData()
    }
  } finally {
    submitLoading.value = false
  }
}

async function handleReview(row) {
  reviewDrawerVisible.value = true
  reviewLoading.value = true
  reviews.value = []
  try {
    const res = await request.get('/comment/list/goods', { params: { goodsId: row.id } })
    if (res.code === 1) {
      reviews.value = res.data || []
    }
  } finally {
    reviewLoading.value = false
  }
}

function maskPhone(phone) {
  if (!phone) return '匿名'
  try {
    return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  } catch (e) {
    return phone
  }
}

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 19)
}
</script>

<style scoped>
.review-item {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.r-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.r-user {
  font-size: 14px;
  font-weight: bold;
  color: #666;
}

.r-content {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  margin-bottom: 6px;
}

.r-time {
  font-size: 12px;
  color: #999;
  text-align: right;
}

.goods-container {
  padding: 10px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.toolbar-card {
  margin-bottom: 12px;
}

.table-card {
  border-radius: 8px;
  border: none;
}

.price-text {
  color: #f56c6c;
  font-weight: 600;
}

.stock-warning {
  color: #f56c6c;
  font-weight: bold;
}
</style>

