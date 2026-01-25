<template>
  <div class="notfound">
    <div class="box">
      <div class="emoji">🍪</div>
      <div class="title">页面走丢了</div>
      <div class="subtitle">别慌，点击下方按钮返回首页</div>
      <el-button type="primary" round @click="$router.push('/')">回到首页</el-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.notfound {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fb;
}
.box {
  text-align: center;
  background: #fff;
  padding: 30px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}
.emoji { font-size: 64px; margin-bottom: 12px; }
.title { font-size: 22px; font-weight: 800; color: #333; margin-bottom: 6px; }
.subtitle { color: #888; margin-bottom: 18px; }
</style>
