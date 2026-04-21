<template>
  <div class="teenage-age-select">
    <!-- 装饰元素 -->
    <div class="decorations">
      <div class="decoration balloon-b">
        B
      </div>
      <div class="decoration balloon-c">
        C
      </div>
      <div class="decoration letter-a">
        A
      </div>
      <div class="decoration letter-p">
        P
      </div>
      <div class="decoration letter-c">
        C
      </div>
      <div class="decoration book-stack">
        📚
      </div>
      <div class="decoration balloon-heart">
        ❤️
      </div>
      <div class="decoration balloon-v">
        V
      </div>
      <div class="decoration star">
        ⭐
      </div>
      <div class="decoration star small">
        ⭐
      </div>
      <div class="decoration cute-animal">
        🐶
      </div>
      <div class="decoration cute-animal small">
        🐱
      </div>
      <div class="decoration book-open">
        📖
      </div>
    </div>
    
    <h2>青少年英语</h2>
    <p class="subtitle">请选择适合您的年龄段，开始个性化学习之旅</p>
    
    <div class="age-cards">
      <div class="age-card card-3-6" @click="goToAgeGroup('3-6')">
        <div class="age-icon">
          🐶
        </div>
        <h3>3-6岁</h3>
        <p class="age-desc">启蒙阶段</p>
        <div class="card-icon">
          🐱
        </div>
        <ul class="feature-list">
          <li>基础单词认知</li>
          <li>简单日常对话</li>
          <li>趣味儿歌学习</li>
          <li>动画互动教学</li>
        </ul>
        <el-button type="primary" size="large" class="start-btn">开始学习</el-button>
      </div>
      
      <div class="age-card card-7-15" @click="goToAgeGroup('7-15')">
        <div class="age-icon">
          📚
        </div>
        <h3>7-15岁</h3>
        <p class="age-desc">基础阶段</p>
        <div class="card-icon">
          📖
        </div>
        <ul class="feature-list">
          <li>系统语法学习</li>
          <li>词汇量扩展</li>
          <li>阅读理解训练</li>
          <li>口语表达提升</li>
        </ul>
        <el-button type="primary" size="large" class="start-btn">开始学习</el-button>
      </div>
      
      <div class="age-card card-15-18" @click="goToAgeGroup('15-18')">
        <div class="age-icon">
          🏫
        </div>
        <h3>15-18岁</h3>
        <p class="age-desc">进阶阶段</p>
        <div class="card-icon">
          🎓
        </div>
        <ul class="feature-list">
          <li>高级语法应用</li>
          <li>学术英语训练</li>
          <li>写作技巧提升</li>
          <li>考试应试技巧</li>
        </ul>
        <el-button type="primary" size="large" class="start-btn">开始学习</el-button>
      </div>
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

<style>
.teenage-age-select {
  padding: 40px 0;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  min-height: 100vh;
  background: linear-gradient(135deg, #e6f7ff, #b3d9ff);
  overflow: hidden;
  width: 100%;
}

/* 确保整个页面背景都是蓝色 */
body {
  margin: 0;
  padding: 0;
  background: linear-gradient(135deg, #e6f7ff, #b3d9ff);
}

/* 确保父容器也有蓝色背景 */
#app {
  background: linear-gradient(135deg, #e6f7ff, #b3d9ff);
}

/* 装饰元素 */
.decorations {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.decoration {
  position: absolute;
  font-size: 24px;
  animation: float 6s ease-in-out infinite;
}

.decoration.balloon-b {
  top: 10%;
  left: 5%;
  background: #ff99cc;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  animation-delay: 0s;
}

.decoration.balloon-c {
  top: 15%;
  right: 8%;
  background: #99ccff;
  border-radius: 50%;
  width: 35px;
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  animation-delay: 1s;
}

.decoration.letter-a {
  bottom: 10%;
  left: 10%;
  font-size: 30px;
  color: #ff9933;
  font-weight: bold;
  animation-delay: 2s;
}

.decoration.letter-p {
  bottom: 15%;
  right: 12%;
  font-size: 28px;
  color: #66cc99;
  font-weight: bold;
  animation-delay: 3s;
}

.decoration.letter-c {
  top: 20%;
  left: 15%;
  font-size: 26px;
  color: #cc66ff;
  font-weight: bold;
  animation-delay: 4s;
}

.decoration.book-stack {
  bottom: 20%;
  left: 15%;
  font-size: 30px;
  animation-delay: 5s;
}

.decoration.balloon-heart {
  top: 25%;
  right: 15%;
  font-size: 24px;
  animation-delay: 0.5s;
}

.decoration.balloon-v {
  bottom: 25%;
  right: 10%;
  font-size: 28px;
  color: #ff6666;
  font-weight: bold;
  animation-delay: 1.5s;
}

.decoration.star {
  top: 30%;
  left: 8%;
  font-size: 20px;
  animation-delay: 2.5s;
}

.decoration.star.small {
  top: 35%;
  right: 10%;
  font-size: 16px;
  animation-delay: 3.5s;
}

.decoration.cute-animal {
  bottom: 30%;
  left: 8%;
  font-size: 24px;
  animation-delay: 4.5s;
}

.decoration.cute-animal.small {
  top: 40%;
  right: 12%;
  font-size: 20px;
  animation-delay: 5.5s;
}

.decoration.book-open {
  bottom: 10%;
  right: 18%;
  font-size: 28px;
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

.teenage-age-select h2 {
  text-align: center;
  margin-bottom: 10px;
  color: #303133;
  font-size: 36px;
  font-weight: bold;
  position: relative;
  z-index: 1;
}

.subtitle {
  text-align: center;
  color: #606266;
  margin-bottom: 60px;
  font-size: 18px;
  position: relative;
  z-index: 1;
}

.age-cards {
  display: flex;
  justify-content: center;
  gap: 40px;
  flex-wrap: wrap;
  position: relative;
  z-index: 1;
}

.age-card {
  width: 320px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  padding: 30px 20px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.age-card.card-3-6 {
  background: linear-gradient(135deg, #ffebf0, #ffd6e0);
}

.age-card.card-7-15 {
  background: linear-gradient(135deg, #e6e6ff, #d6d6ff);
}

.age-card.card-15-18 {
  background: linear-gradient(135deg, #e6f7ff, #b3d9ff);
}

.age-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.age-icon {
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 28px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-icon {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 24px;
  opacity: 0.7;
}

.age-card h3 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 10px 0;
  font-weight: bold;
}

.age-desc {
  font-size: 16px;
  color: #606266;
  font-weight: 600;
  margin: 0 0 20px 0;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0 0 30px 0;
  text-align: left;
  background: rgba(255, 255, 255, 0.8);
  padding: 15px;
  border-radius: 8px;
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
  color: #409EFF;
  font-weight: bold;
}

.start-btn {
  width: 100%;
  margin-top: 10px;
  background: #409EFF;
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.start-btn:hover {
  background: #3a8ee6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
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
  
  .decoration {
    font-size: 20px;
  }
  
  .decoration.balloon-b,
  .decoration.balloon-c {
    width: 30px;
    height: 30px;
    font-size: 16px;
  }
}
</style>