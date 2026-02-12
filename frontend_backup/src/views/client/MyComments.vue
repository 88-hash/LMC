<template>
  <div class="my-comments-page">
    <div class="header">
        <h2>我的评价</h2>
    </div>
    <div class="content" v-loading="loading">
        <el-empty v-if="comments.length === 0" description="暂无评价" />
        <div v-else class="comment-list">
            <div v-for="c in comments" :key="c.id" class="comment-card">
                <div class="goods-info">
                    <img :src="c.goodsImage" class="goods-img" />
                    <span class="goods-name">{{ c.goodsName }}</span>
                </div>
                <div class="rating-row">
                    <el-rate v-model="c.rating" disabled show-score text-color="#ff9900" />
                    <span class="time">{{ formatTime(c.createdAt) }}</span>
                </div>
                <div class="content-text">{{ c.content }}</div>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const comments = ref([])
const loading = ref(false)

const load = async () => {
    loading.value = true
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo.phone) {
        const res = await request.get('/comment/list/user', { params: { userPhone: userInfo.phone } })
        if (res.code === 1) {
            comments.value = res.data || []
        }
    }
    loading.value = false
}

const formatTime = (t) => {
    if(!t) return ''
    return t.replace('T', ' ').substring(0, 19)
}

onMounted(load)
</script>

<style scoped>
.my-comments-page {
    min-height: 100vh;
    background-color: #f7f1e3;
    padding: 20px;
}
.header { text-align: center; margin-bottom: 20px; color: #333; }
.comment-card {
    background: #fff;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 16px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.goods-info { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; border-bottom: 1px solid #f0f0f0; padding-bottom: 10px; }
.goods-img { width: 40px; height: 40px; border-radius: 6px; object-fit: cover; }
.goods-name { font-weight: bold; color: #555; font-size: 14px; }
.rating-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
.time { font-size: 12px; color: #999; }
.content-text { color: #333; line-height: 1.5; }
</style>
