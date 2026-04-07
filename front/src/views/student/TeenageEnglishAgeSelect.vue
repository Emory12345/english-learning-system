<template>
  <div class="teenage-age-select">
    <h2>青少年英语</h2>
    <p class="subtitle">请选择适合您的年龄段，开始个性化学习之旅</p>
    
    <div class="age-cards">
      <el-card class="age-card" @click="goToAgeGroup('3-6')">
        <div class="age-icon">
          <el-icon><Trophy /></el-icon>
        </div>
        <h3>3-6岁</h3>
        <p class="age-desc">启蒙阶段</p>
        <ul class="feature-list">
          <li>基础单词认知</li>
          <li>简单日常对话</li>
          <li>趣味儿歌学习</li>
          <li>动画互动教学</li>
        </ul>
        <el-button type="primary" size="large" class="start-btn">开始学习</el-button>
      </el-card>
      
      <el-card class="age-card" @click="goToAgeGroup('7-15')">
        <div class="age-icon">
          <el-icon><Reading /></el-icon>
        </div>
        <h3>7-15岁</h3>
        <p class="age-desc">基础阶段</p>
        <ul class="feature-list">
          <li>系统语法学习</li>
          <li>词汇量扩展</li>
          <li>阅读理解训练</li>
          <li>口语表达提升</li>
        </ul>
        <el-button type="primary" size="large" class="start-btn">开始学习</el-button>
      </el-card>
      
      <el-card class="age-card" @click="goToAgeGroup('15-18')">
        <div class="age-icon">
          <el-icon><School /></el-icon>
        </div>
        <h3>15-18岁</h3>
        <p class="age-desc">进阶阶段</p>
        <ul class="feature-list">
          <li>高级语法应用</li>
          <li>学术英语训练</li>
          <li>写作技巧提升</li>
          <li>考试应试技巧</li>
        </ul>
        <el-button type="primary" size="large" class="start-btn">开始学习</el-button>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Trophy, Reading, School } from '@element-plus/icons-vue'

const router = useRouter()

const goToAgeGroup = (ageGroup: string) => {
  router.push(`/student/teenage-english-${ageGroup}`)
}
</script>

<style scoped>
.teenage-age-select {
  padding: 20px 0;
  max-width: 1200px;
  margin: 0 auto;
}

.teenage-age-select h2 {
  text-align: center;
  margin-bottom: 10px;
  color: #303133;
  font-size: 32px;
}

.subtitle {
  text-align: center;
  color: #606266;
  margin-bottom: 40px;
  font-size: 16px;
}

.age-cards {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
}

.age-card {
  width: 320px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  padding: 30px 20px;
}

.age-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.age-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #409EFF, #36cfc9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.age-icon .el-icon {
  font-size: 40px;
  color: white;
}

.age-card h3 {
  font-size: 28px;
  color: #303133;
  margin: 0 0 10px 0;
}

.age-desc {
  font-size: 18px;
  color: #409EFF;
  font-weight: 600;
  margin: 0 0 20px 0;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0 0 30px 0;
  text-align: left;
}

.feature-list li {
  padding: 8px 0;
  color: #606266;
  font-size: 14px;
  position: relative;
  padding-left: 20px;
}

.feature-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #67c23a;
  font-weight: bold;
}

.start-btn {
  width: 100%;
  margin-top: 10px;
}

@media screen and (max-width: 768px) {
  .age-cards {
    flex-direction: column;
    align-items: center;
  }
  
  .age-card {
    width: 100%;
    max-width: 350px;
  }
}
</style>