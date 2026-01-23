<template>
  <div class="goods-container animate__animated animate__fadeIn">
    <!-- 头部操作区 -->
    <div class="page-header">
      <h2 class="page-title">商品管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增商品</el-button>
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
        
        <el-table-column label="图片" width="100" align="center">
          <template #default="{ row }">
            <el-image 
              :src="row.imageUrl || 'https://via.placeholder.com/100?text=NoImg'" 
              :preview-src-list="[row.imageUrl]" 
              style="width: 60px; height: 60px; border-radius: 4px;"
              fit="cover"
            />
          </template>
        </el-table-column>

        <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />

        <el-table-column prop="categoryId" label="分类" width="120">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ getCategoryName(row.categoryId) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            <span class="price-text">¥ {{ row.price }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="stock" label="库存" width="100" align="center">
          <template #default="{ row }">
            <span :class="{ 'stock-warning': row.stock < 10 }">{{ row.stock }}</span>
          </template>
        </el-table-column>

        <el-table-column prop="isOnSale" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isOnSale === 1 ? 'success' : 'danger'">
              {{ row.isOnSale === 1 ? '已上架' : '已下架' }}
            </el-tag>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
      top="5vh"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option 
              v-for="cate in categoryList" 
              :key="cate.id" 
              :label="cate.name" 
              :value="cate.id" 
            />
          </el-select>
        </el-form-item>

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
          <el-date-picker
            v-model="form.expireDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="状态" prop="isOnSale">
          <el-radio-group v-model="form.isOnSale">
            <el-radio :label="1">上架销售</el-radio>
            <el-radio :label="0">下架仓库</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="简单描述">
          <el-input v-model="form.description" type="textarea" rows="3" />
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

    <!-- 评价抽屉 -->
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
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit, Delete, Picture, ChatLineSquare } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const loading = ref(false)
const tableData = ref([])
const categoryList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')
const submitLoading = ref(false)
const formRef = ref(null)

const reviewDrawerVisible = ref(false)
const reviewLoading = ref(false)
const reviews = ref([])

const form = reactive({
  id: null,
  name: '',
  categoryId: null,
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
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

onMounted(() => {
  fetchCategories()
  fetchData()
})

const fetchCategories = async () => {
  try {
    const res = await request.get('/category/list')
    if (res.code === 1) {
      categoryList.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request.get('/goods/list')
    if (res.code === 1) {
      tableData.value = res.data
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getCategoryName = (id) => {
  const cate = categoryList.value.find(c => c.id === id)
  return cate ? cate.name : '未分类'
}

const handleAdd = () => {
  dialogTitle.value = '新增商品'
  Object.assign(form, {
    id: null,
    name: '',
    categoryId: categoryList.value.length > 0 ? categoryList.value[0].id : null,
    price: 0,
    stock: 100,
    barCode: '',
    imageUrl: '',
    expireDate: null,
    isOnSale: 1,
    description: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑商品'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除商品 "${row.name}" 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.get(`/goods/delete?id=${row.id}`)
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
        const url = form.id ? '/goods/update' : '/goods/add'
        const res = await request.post(url, form)
        
        if (res.code === 1) {
          ElMessage.success(form.id ? '修改成功' : '新增成功')
          dialogVisible.value = false
          fetchData()
        }
      } catch (e) {
        console.error(e)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleReview = async (row) => {
    reviewDrawerVisible.value = true
    reviewLoading.value = true
    reviews.value = []
    try {
        const res = await request.get('/comment/list/goods', { params: { goodsId: row.id } })
        if(res.code === 1) {
            reviews.value = res.data || []
        }
    } catch(e) { console.error(e) }
    reviewLoading.value = false
}

const maskPhone = (phone) => {
    if(!phone) return '匿名'
    try {
        return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
    } catch(e) { return phone }
}

const formatTime = (t) => {
    if(!t) return ''
    return t.replace('T', ' ').substring(0, 19)
}
</script>

<style scoped>
.review-item { padding: 12px; border-bottom: 1px solid #f0f0f0; }
.r-head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.r-user { font-size: 14px; font-weight: bold; color: #666; }
.r-content { font-size: 14px; color: #333; line-height: 1.5; margin-bottom: 6px; }
.r-time { font-size: 12px; color: #999; text-align: right; }

.goods-container {
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

.price-text {
  color: #f56c6c;
  font-weight: 600;
}

.stock-warning {
  color: #f56c6c;
  font-weight: bold;
}
</style>
