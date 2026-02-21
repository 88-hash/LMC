<template>
  <div class="not-found-container">
    <div class="content animate__animated animate__bounceIn">
      <div class="emoji">🍪</div>
      <h1 class="title">哎呀，页面被贪吃鬼偷走了！</h1>
      <p class="desc">您访问的页面好像不存在，或者正在烤箱里烘焙中...</p>
      <el-button type="primary" size="large" round @click="$router.push('/home')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.not-found-container {
  height: 100vh;
  width: 100vw;
  background-color: #f7f1e3;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.emoji {
  font-size: 80px;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

.title {
  color: #2c3e50;
  font-size: 28px;
  margin-bottom: 10px;
  font-weight: 800;
}

.desc {
  color: #7f8c8d;
  margin-bottom: 30px;
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
  100% { transform: translateY(0px); }
}
</style>
