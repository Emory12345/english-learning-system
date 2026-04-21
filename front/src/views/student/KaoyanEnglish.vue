<template>
  <div class="kaoyan-english">
    <!-- 欢迎信息区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h2>晚上好，{{ userInfo.name }}！</h2>
        <p>欢迎回到学习中心，今天也要加油哦！</p>
        <el-button type="primary" class="continue-btn">继续学习</el-button>
      </div>
    </div>
    
    <!-- 状态卡片 -->
    <div class="status-cards">
      <div class="status-card">
        <div class="status-icon learning-goal">
          <el-icon><Flag /></el-icon>
        </div>
        <h3>学习目标</h3>
        <p>考研英语</p>
      </div>
      <div class="status-card">
        <div class="status-icon membership">
          <el-icon><User /></el-icon>
        </div>
        <h3>当前内容</h3>
        <p>词汇手册 + 历年真题</p>
      </div>
      <div class="status-card">
        <div class="status-icon expiry">
          <el-icon><Calendar /></el-icon>
        </div>
        <h3>访问权限</h3>
        <p>无限制使用</p>
      </div>
    </div>
    
    <!-- 学习模块 -->
    <div class="learning-modules">
      <h2>学习模块</h2>
      <p class="module-desc">选择模块开始今天的学习</p>
      
      <div class="module-grid">
        <div class="module-card">
          <div class="module-icon reading">
            <el-icon><Reading /></el-icon>
          </div>
          <h3>阅读理解</h3>
          <p class="module-subtitle">历年真题</p>
          <p class="module-desc">历年真题阅读理解，配有详细解析</p>
          <el-button type="primary" class="module-btn">开始学习 →</el-button>
        </div>
        
        <div class="module-card">
          <div class="module-icon translation">
            <el-icon><Document /></el-icon>
          </div>
          <h3>翻译练习</h3>
          <p class="module-subtitle">真题训练</p>
          <p class="module-desc">英译中专项训练，提升翻译能力</p>
          <el-button type="primary" class="module-btn">开始学习 →</el-button>
        </div>
        
        <div class="module-card">
          <div class="module-icon writing">
            <el-icon><Edit /></el-icon>
          </div>
          <h3>写作练习</h3>
          <p class="module-subtitle">范文分析</p>
          <p class="module-desc">大小作文真题，附带高分范文</p>
          <el-button type="primary" class="module-btn">开始学习 →</el-button>
        </div>
        
        <div class="module-card" @click="navigateToWords">
          <div class="module-icon vocabulary">
            <el-icon><Reading /></el-icon>
          </div>
          <h3>单词学习</h3>
          <p class="module-subtitle">核心词汇</p>
          <p class="module-desc">考研核心词汇，详细释义，例句解析</p>
          <el-button type="primary" class="module-btn">开始学习 →</el-button>
        </div>
        
        <div class="module-card">
          <div class="module-icon exam">
            <el-icon><Document /></el-icon>
          </div>
          <h3>真题练习</h3>
          <p class="module-subtitle">历年真题</p>
          <p class="module-desc">历年考研英语真题，附带详细解析</p>
          <el-button type="primary" class="module-btn">开始学习 →</el-button>
        </div>
      </div>
    </div>
    
    <!-- 学习小贴士 -->
    <div class="learning-tips">
      <h3><el-icon><Star /></el-icon> 学习小贴士</h3>
      <div class="tips-container">
        <div class="tip-item">
          <div class="tip-number blue">1</div>
          <h4>阅读理解</h4>
          <p>先读问题，带着问题阅读文章</p>
        </div>
        <div class="tip-item">
          <div class="tip-number green">2</div>
          <h4>翻译练习</h4>
          <p>先理解整句意思，再逐词翻译</p>
        </div>
        <div class="tip-item">
          <div class="tip-number purple">3</div>
          <h4>写作练习</h4>
          <p>多背范文，积累高分句型</p>
        </div>
        <div class="tip-item">
          <div class="tip-number pink">4</div>
          <h4>单词学习</h4>
          <p>及时记录生词，定期复习巩固</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Flag, User, Calendar, Reading, Document, Edit, Notebook, List, Star } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const userInfo = userStore.userInfo

// 导航到单词学习页面
const navigateToWords = () => {
  router.push('/student/kaoyan-words')
}
</script>

<style scoped>
.kaoyan-english {
  font-family: 'Arial', sans-serif;
}

/* 欢迎信息区域 */
.welcome-section {
  background-color: #1890ff;
  color: white;
  padding: 30px 20px;
}

.welcome-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-content h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.welcome-content p {
  margin: 0 0 15px 0;
  font-size: 16px;
}

.continue-btn {
  background-color: white;
  color: #1890ff;
  border: none;
  border-radius: 4px;
  padding: 8px 20px;
  font-size: 14px;
  cursor: pointer;
}

/* 状态卡片 */
.status-cards {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  gap: 20px;
  margin-top: -20px;
  position: relative;
  z-index: 1;
}

.status-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  flex: 1;
  display: flex;
  align-items: center;
  gap: 15px;
}

.status-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.status-icon.learning-goal {
  background-color: #e6f7ff;
  color: #1890ff;
}

.status-icon.membership {
  background-color: #f6ffed;
  color: #52c41a;
}

.status-icon.expiry {
  background-color: #fff7e6;
  color: #faad14;
}

.status-card h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.status-card p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 学习模块 */
.learning-modules {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.learning-modules h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #333;
}

.learning-modules .module-desc {
  margin: 0 0 30px 0;
  font-size: 16px;
  color: #666;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.module-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  text-align: center;
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.module-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.module-icon {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin: 0 auto 15px;
}

.module-icon.reading {
  background-color: #e6f7ff;
  color: #1890ff;
}

.module-icon.translation {
  background-color: #f6ffed;
  color: #52c41a;
}

.module-icon.writing {
  background-color: #fff7e6;
  color: #faad14;
}

.module-icon.journal {
  background-color: #fff0f6;
  color: #ff4d4f;
}

.module-icon.exercises {
  background-color: #f0f5ff;
  color: #722ed1;
}

.module-icon.vocabulary {
  background-color: #e8f5e9;
  color: #4caf50;
}

.module-icon.exam {
  background-color: #f0f0f0;
  color: #666;
}

.module-card h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.module-card .module-subtitle {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #999;
}

.module-card .module-desc {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.module-btn {
  font-size: 14px;
  font-weight: 600;
  padding: 6px 16px;
}

/* 响应式设计 */
@media screen and (max-width: 1200px) {
  .module-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media screen and (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .status-cards {
    flex-direction: column;
  }
  
  .module-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 学习小贴士 */
.learning-tips {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 40px;
}

.learning-tips h3 {
  margin: 0 0 30px 0;
  font-size: 20px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 10px;
}

.tips-container {
  display: flex;
  gap: 30px;
  justify-content: space-between;
}

.tip-item {
  flex: 1;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tip-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: white;
  margin-bottom: 15px;
}

.tip-number.blue {
  background-color: #1890ff;
}

.tip-number.green {
  background-color: #52c41a;
}

.tip-number.purple {
  background-color: #722ed1;
}

.tip-number.pink {
  background-color: #ff4d4f;
}

.tip-item h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
}

.tip-item p {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .tips-container {
    flex-direction: column;
  }
}

@media screen and (max-width: 480px) {
  .module-grid {
    grid-template-columns: 1fr;
  }
}
</style>
