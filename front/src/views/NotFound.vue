<template>
  <div class="not-found">
    <h2>404 - 页面不存在</h2>
    <p>您访问的页面不存在或已被删除</p>
    <el-button type="primary" @click="goHome">返回首页</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh;
  text-align: center;
}

.not-found h2 {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 20px;
}

.not-found p {
  font-size: 18px;
  color: #606266;
  margin-bottom: 30px;
}
</style>