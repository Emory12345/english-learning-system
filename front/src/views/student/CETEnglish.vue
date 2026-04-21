<template>
  <div class="cet-english">
    <!-- 顶部横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <img src="@/assets/images/slider-img-1.jpg" alt="FREE OPEN FOREVER" class="hero-image" />
        </div>
        <div class="hero-info">
          <h2>四六级英语工具页</h2>
          <p class="hero-subtitle">集中收录：真题试卷、词汇手册、听力音频、写作范文、阅读精析</p>
          <p class="hero-description">无社交干扰，无数据追踪，只为高效备考而设。</p>
          <el-button type="primary" class="get-started-btn">开始使用</el-button>
        </div>
      </div>
    </div>
    
    <!-- 免费资源部分 -->
    <div class="resources-section">
      <h2 class="resources-title">免费资源</h2>
      
      <div class="resources-grid">
        <!-- 四级单词 -->
        <div class="resource-card" @click="navigateToCET4Words">
          <div class="resource-icon cet4-icon">
            <img src="@/assets/images/cet4.jpg" alt="四级单词" />
          </div>
          <h3 class="resource-title">四级单词</h3>
          <p class="resource-description">大学英语四级考试核心词汇，包含发音和例句。</p>
        </div>
        
        <!-- 四级真题 -->
        <div class="resource-card" @click="navigateToCET4RealTests">
          <div class="resource-icon cet4-icon">
            <img src="@/assets/images/cet4.jpg" alt="四级真题" />
          </div>
          <h3 class="resource-title">四级真题</h3>
          <p class="resource-description">近年来所有大学英语四级考试真题、听力音频和答案解析。</p>
        </div>
        
        <!-- 六级单词 -->
        <div class="resource-card" @click="navigateToCET6Words">
          <div class="resource-icon cet6-icon">
            <img src="@/assets/images/cet6.jpg" alt="六级单词" />
          </div>
          <h3 class="resource-title">六级单词</h3>
          <p class="resource-description">大学英语六级考试核心词汇，包含发音和例句。</p>
        </div>
        
        <!-- 六级真题 -->
        <div class="resource-card" @click="navigateToCET6RealTests">
          <div class="resource-icon cet6-icon">
            <img src="@/assets/images/cet6.jpg" alt="六级真题" />
          </div>
          <h3 class="resource-title">六级真题</h3>
          <p class="resource-description">近年来所有大学英语六级考试真题、听力音频和答案解析。</p>
        </div>
      </div>
    </div>
    
    <!-- 为什么选择我们 -->
    <div class="why-choose">
      <h2 class="section-title">为什么选择我们？</h2>
      <div class="features-list">
        <div class="feature-item">
          <h4>专注英语学习领域</h4>
          <p>为考生提供最专业、最全面的学习资源</p>
        </div>
        <div class="feature-item">
          <h4>历年真题精选</h4>
          <p>覆盖大学英语四/六级考试</p>
        </div>
        <div class="feature-item">
          <h4>详细解析</h4>
          <p>深入理解出题思路</p>
        </div>
        <div class="feature-item">
          <h4>无门槛使用</h4>
          <p>所有四六级资源直接浏览/下载，无需额外操作，专注学习内容本身。</p>
        </div>
      </div>
    </div>
    
    <!-- 学习资源 -->
    <div class="resources-more">
      <h2 class="section-title">学习资源</h2>
      <div class="resources-list">
        <div class="resource-item">
          <h4>词汇手册</h4>
          <p>大学英语四/六级核心词汇，含发音与例句</p>
        </div>
        <div class="resource-item">
          <h4>历年真题</h4>
          <p>覆盖四/六级考试，提供原文与解析</p>
        </div>
      </div>
      <el-button type="primary" class="explore-btn">探索更多资源</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

// 导航到四级单词页面
const navigateToCET4Words = () => {
  router.push('/student/cet4-words')
}

// 导航到六级单词页面
const navigateToCET6Words = () => {
  router.push('/student/cet6-words')
}

// 导航到四级真题页面
const navigateToCET4RealTests = () => {
  router.push('/student/cet4-real-tests')
}

// 导航到六级真题页面
const navigateToCET6RealTests = () => {
  router.push('/student/cet6-real-tests')
}
</script>

<style scoped>
/* 全局字体设置 */
* {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  box-sizing: border-box;
}

/* 顶部横幅 */
.hero-section {
  background-color: #6a1b9a;
  padding: 40px 0;
  color: white;
  position: relative;
  overflow: hidden;
}

.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.hero-text {
  flex: 1;
}

.hero-image {
  max-width: 100%;
  height: auto;
  max-height: 150px;
  object-fit: contain;
}

.hero-info {
  flex: 1;
  text-align: right;
}

.hero-info h2 {
  font-size: 24px;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.hero-subtitle {
  font-size: 16px;
  margin: 0 0 16px 0;
  color: #e0e0e0;
  font-weight: 400;
}

.hero-description {
  font-size: 14px;
  margin: 0 0 24px 0;
  color: #e0e0e0;
  line-height: 1.5;
  font-weight: 400;
}

.get-started-btn {
  background-color: #ff9800;
  border: none;
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 4px;
  transition: background-color 0.3s;
  cursor: pointer;
}

.get-started-btn:hover {
  background-color: #f57c00;
}

/* 免费资源部分 */
.resources-section {
  padding: 40px 0;
  background-color: #f5f5f5;
}

.resources-title {
  font-size: 24px;
  text-align: center;
  margin: 0 0 8px 0;
  color: #333;
  font-weight: 600;
}

.resources-subtitle {
  font-size: 14px;
  text-align: center;
  margin: 0 0 32px 0;
  color: #666;
  font-weight: 400;
}

.resources-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.resource-card {
  background-color: white;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 280px;
}

.resource-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.resource-icon {
  width: 64px;
  height: 64px;
  background-color: #f0f0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.resource-icon.cet4-icon,
.resource-icon.cet6-icon {
  border-radius: 0;
  width: 100%;
  height: 120px;
  margin-bottom: 16px;
}

.resource-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.resource-title {
  font-size: 16px;
  margin: 0 0 8px 0;
  color: #333;
  font-weight: 600;
}

.resource-description {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
  margin: 0;
  font-weight: 400;
}

/* 为什么选择我们 */
.why-choose {
  padding: 40px 0;
  background-color: #f9f9f9;
}

.section-title {
  font-size: 24px;
  text-align: center;
  margin: 0 0 30px 0;
  color: #333;
  font-weight: 600;
}

.features-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 30px;
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.feature-item h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.feature-item p {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  font-weight: 400;
}

/* 学习资源 */
.resources-more {
  background-color: #6a1b9a;
  color: white;
  padding: 40px 20px;
  text-align: center;
}

.resources-more .section-title {
  color: white;
}

.resources-list {
  display: flex;
  justify-content: space-between;
  max-width: 1000px;
  margin: 0 auto 30px;
  padding: 0 20px;
}

.resource-item {
  flex: 0 0 45%;
  text-align: center;
}

.resource-item h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: white;
  font-weight: 600;
}

.resource-item p {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.5;
  font-weight: 400;
}

.explore-btn {
  font-size: 16px;
  font-weight: 600;
  padding: 10px 30px;
  background-color: white;
  color: #6a1b9a;
  border: none;
  transition: background-color 0.3s;
}

.explore-btn:hover {
  background-color: #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .resources-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .features-list {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .resources-list {
    flex-direction: row;
  }
  
  .resource-item {
    flex: 0 0 45%;
  }
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    text-align: center;
  }
  
  .hero-info {
    text-align: center;
    margin-top: 20px;
  }
  
  .resources-grid {
    grid-template-columns: 1fr;
  }
  
  .features-list {
    grid-template-columns: 1fr;
  }
  
  .resources-list {
    flex-direction: column;
  }
  
  .resource-item {
    flex: 1 1 100%;
    margin-bottom: 20px;
  }
}
</style>
