<template>
  <div class="student-home">
    <h2>欢迎回来，{{ userStore.userInfo.name }}！</h2>
    
    <!-- 轮播图 -->
    <el-carousel :interval="5000" height="400px" class="home-carousel">
      <el-carousel-item v-for="item in carouselItems" :key="item.id">
        <div class="carousel-item" @click="carouselClick(item.link)">
          <img :src="item.image" :alt="item.title" />
          <div class="carousel-content">
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
            <el-button type="primary" @click.stop="carouselClick(item.link)">{{ item.buttonText }}</el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
    

    


    <!-- 热门课程/新课推荐 -->
    <el-card class="recommended-courses">
      <template #header>
        <div class="card-header">
          <span>热门课程</span>
        </div>
      </template>
      <div class="course-list">
        <div v-for="course in filteredCourses" :key="course.id" class="course-item">
          <div class="course-image">
            <img :src="`http://localhost:8080${course.image}`" :alt="course.title" />
            <div v-if="course.isNew" class="course-badge new-badge">NEW</div>
          </div>
          <div class="course-info">
            <h3>{{ course.title }}</h3>
            <p>{{ course.description }}</p>
            <div class="course-meta">
              <span class="course-type">{{ course.category }}</span>
              <span class="course-students">{{ course.students }}人学习</span>
              <span class="course-rating">
                <el-icon class="star-icon"><Star /></el-icon>
                {{ course.rating }}
              </span>
            </div>
            <el-button type="primary" size="small" @click="goToVideoPlayer(course.id)">开始学习</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 学习激励/成就体系 -->
    <el-card class="achievements-card">
      <template #header>
        <div class="card-header">
          <span>学习成就</span>
        </div>
      </template>
      <div class="achievements-section">
        <div class="achievement-message">
          <p class="motivational-quote">"每天进步一点，离目标更近一步✨"</p>
        </div>
        <div class="badges-container">
          <div v-for="badge in badges" :key="badge.id" class="badge-item">
            <div class="badge-icon" :class="{ 'badge-locked': !badge.earned }">
              <el-icon :class="badge.icon"></el-icon>
            </div>
            <div class="badge-info">
              <div class="badge-name">{{ badge.name }}</div>
              <div v-if="!badge.earned" class="badge-progress">
                <el-progress :percentage="badge.progress" :stroke-width="6" />
                <span class="progress-text">{{ badge.progressText }}</span>
              </div>
              <div v-else class="badge-earned">已获得</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 最近学习 -->
    <el-card class="recent-studies">
      <template #header>
        <div class="card-header">
          <span>最近学习</span>
          <div class="recent-studies-actions">
            <el-button type="primary" size="small" @click="clearWatchHistory" class="clear-history-btn">清理历史</el-button>
          </div>
        </div>
      </template>
      <div v-if="filteredWatchHistory.length > 0" class="video-list">
        <div v-for="item in filteredWatchHistory.slice(0, 3)" :key="item.courseId" class="video-item">
          <div class="video-container">
            <video controls width="100%" height="200px" style="border-radius: 8px;">
              <source :src="`http://localhost:8080${item.videoUrl || item.chapterVideoUrl || ''}`" type="video/mp4">
              您的浏览器不支持视频播放
            </video>
          </div>
          <div class="video-title">
            <h3>{{ item.courseTitle }}</h3>
            <p style="color: #909399; font-size: 12px; margin-top: 5px;">
              {{ formatWatchTime(item.lastWatchTime) }}
            </p>
            <div class="video-actions">
              <el-button type="primary" size="small" @click="goToVideoPlayerFromHistory(item)" class="continue-btn">继续学习</el-button>
            </div>
          </div>
        </div>
      </div>
      <div v-else style="text-align: center; padding: 40px; color: #909399;">
        暂无最近学习记录
      </div>
    </el-card>
    
    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-logo">在线英语学习系统</div>
        <div class="footer-links">
          <a href="#">关于我们</a>
          <a href="#">隐私政策</a>
          <a href="#">使用条款</a>
          <a href="#">联系我们</a>
        </div>
        <div class="footer-copyright">© 2026 在线英语学习系统. 保留所有权利.</div>
      </div>
    </footer>
    
    <!-- 快捷入口悬浮按钮 -->
    <div class="floating-buttons">
      <div class="floating-button" @click="quickStartLearning" title="开始学习">
        <el-icon><Timer /></el-icon>
      </div>
      <div class="floating-button" @click="goToCommunity" title="学习社区">
        <el-icon><ChatDotRound /></el-icon>
      </div>
      <div class="floating-button" @click="goToProfile" title="个人中心">
        <el-icon><User /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { Timer, Document, Check, Star, ChatDotRound, User, Medal, ThumbsUp, Calendar } from '@element-plus/icons-vue'
import { api } from '@/api'
import * as echarts from 'echarts'
import teenageEnglishImg from '@/assets/images/teenage-english.jpg'
import graduateExamImg from '@/assets/images/graduate-exam.jpg'
import cet46Img from '@/assets/images/cet-4-6.jpg'
import ieltsToeflImg from '@/assets/images/ielts-toefl.jpg'
import businessEnglishImg from '@/assets/images/business-english.jpg'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

// 学习概览数据
const todayStudyTime = ref<number>(0)
const totalStudyTime = ref<number>(0)
const completedCourses = ref<number>(0)
const accuracy = ref<number>(0)

// 轮播图数据
const carouselItems = ref([
  {
    id: '1',
    title: '青少年英语',
    description: '专为青少年设计的英语课程，选择适合年龄段，轻松掌握英语',
    image: teenageEnglishImg,
    link: '/student/teenage-english',
    buttonText: '开始学习'
  },
  {
    id: '2',
    title: '考研英语冲刺',
    description: '专业考研英语课程，助你成功上岸',
    image: graduateExamImg,
    link: '/student/kaoyan-english',
    buttonText: '查看课程'
  },
  {
    id: '3',
    title: '四六级备考',
    description: '全方位四六级英语培训，轻松通过考试',
    image: cet46Img,
    link: '/student/cet-english',
    buttonText: '立即报名'
  },
  {
    id: '4',
    title: '雅思托福备考',
    description: '专业出国留学考试培训，帮助你拿到理想分数',
    image: ieltsToeflImg,
    link: '/student/ielts-toefl-english',
    buttonText: '开始备考'
  }
])

// 推荐课程数据
const recommendedCourses = ref<any[]>([
  {
    id: '1',
    title: '初级英语口语',
    description: '从零开始学习英语口语，掌握日常交流必备词汇和句型',
    image: '/api/images/teenage-english.jpg',
    category: '青少年',
    students: 1250,
    rating: 4.8,
    isNew: false
  },
  {
    id: '2',
    title: '考研英语词汇突破',
    description: '针对考研英语的高频词汇和短语，帮助你快速提升词汇量',
    image: '/api/images/graduate-exam.jpg',
    category: '考研',
    students: 890,
    rating: 4.9,
    isNew: false
  },
  {
    id: '3',
    title: '四六级听力技巧',
    description: '掌握四六级听力技巧，提高听力理解能力和答题速度',
    image: '/api/images/cet-4-6.jpg',
    category: '四六级',
    students: 1560,
    rating: 4.7,
    isNew: true
  },
  {
    id: '4',
    title: '雅思口语满分攻略',
    description: '雅思口语考试技巧和话题准备，助你取得理想分数',
    image: '/api/images/ielts-toefl.jpg',
    category: '雅思托福',
    students: 780,
    rating: 4.9,
    isNew: false
  },
  {
    id: '5',
    title: '商务英语谈判技巧',
    description: '掌握商务英语谈判的核心词汇和表达，提升职场竞争力',
    image: '/api/images/business-english.jpg',
    category: '企业英语',
    students: 650,
    rating: 4.8,
    isNew: true
  },
  {
    id: '6',
    title: '青少年英语阅读提升',
    description: '通过趣味阅读材料，提高青少年的英语阅读能力和理解能力',
    image: '/api/images/teenage-english.jpg',
    category: '青少年',
    students: 920,
    rating: 4.6,
    isNew: false
  }
])







// 成就徽章
const badges = ref([
  {
    id: '1',
    name: '连续学习7天',
    icon: 'Calendar',
    earned: true,
    progress: 100,
    progressText: '已完成'
  },
  {
    id: '2',
    name: '四六级词汇通关',
    icon: 'ThumbsUp',
    earned: false,
    progress: 75,
    progressText: '还差25%'
  },
  {
    id: '3',
    name: '口语达人',
    icon: 'Medal',
    earned: false,
    progress: 40,
    progressText: '还差60%'
  }
])

// 观看历史
const watchHistory = ref<any[]>([])

// 最近学习数据（保留旧数据作为后备）
const recentStudies = ref([
  {
    courseId: '1',
    courseTitle: '初级英语口语',
    lastStudyTime: '2024-01-15 14:30',
    progress: '60%'
  },
  {
    courseId: '2',
    courseTitle: '商务英语进阶',
    lastStudyTime: '2024-01-14 09:15',
    progress: '30%'
  }
])

// 筛选后的课程
const filteredCourses = computed(() => {
  return recommendedCourses.value
})

// 筛选后的观看历史
const filteredWatchHistory = computed(() => {
  return watchHistory.value
})

// 从后端获取推荐课程
const fetchRecommendedCourses = async () => {
  try {
    loading.value = true
    const courses = await api.courses.getList()
    if (courses && courses.length > 0) {
      // 取前6个课程作为推荐课程
      recommendedCourses.value = courses.slice(0, 6)
    }
  } catch (error) {
    console.error('Failed to fetch recommended courses:', error)
  } finally {
    loading.value = false
  }
}

// 从后端获取学习概览数据
const fetchStudentOverview = async () => {
  try {
    const data = await api.student.getOverview()
    if (data) {
      todayStudyTime.value = data.learningTime || 45
      totalStudyTime.value = data.totalLearningTime || 7200
      completedCourses.value = data.completedCourses || 12
      accuracy.value = data.accuracy || 85
    }
  } catch (error) {
    console.error('Failed to fetch student overview:', error)
  }
}



// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 从localStorage读取观看历史
const fetchWatchHistory = () => {
  try {
    console.log('Fetching watch history from localStorage...')
    const history = localStorage.getItem('watchHistory')
    console.log('Raw history data:', history)
    if (history) {
      const parsedHistory = JSON.parse(history)
      console.log('Parsed history:', parsedHistory)
      watchHistory.value = parsedHistory
      console.log('Watch history loaded:', watchHistory.value)
    } else {
      console.log('No watch history found in localStorage')
      // 添加模拟数据
      watchHistory.value = [
        {
          courseId: '1',
          courseTitle: '初级英语口语',
          lastWatchTime: new Date().toISOString(),
          videoUrl: '/api/videos/english1.mp4',
          courseType: 'teenage'
        },
        {
          courseId: '3',
          courseTitle: '四六级听力技巧',
          lastWatchTime: new Date(Date.now() - 86400000).toISOString(),
          videoUrl: '/api/videos/cet4_listening.mp4',
          courseType: 'cet'
        }
      ]
    }
  } catch (error) {
    console.error('Failed to load watch history:', error)
  }
}

// 清理观看历史
const clearWatchHistory = () => {
  try {
    localStorage.removeItem('watchHistory')
    watchHistory.value = []
    console.log('Watch history cleared')
  } catch (error) {
    console.error('Failed to clear watch history:', error)
  }
}

// 格式化观看时间
const formatWatchTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

// 从历史记录跳转到视频播放页面
const goToVideoPlayerFromHistory = (item: any) => {
  // 根据课程类型和ID跳转到对应的页面
  switch (item.courseType) {
    case 'kaoyan':
      router.push('/student/kaoyan-english')
      break
    case 'cet':
      router.push('/student/cet-english')
      break
    case 'ielts-toefl':
      router.push('/student/ielts-toefl-english')
      break
    case 'business':
      router.push('/student/business-english')
      break
    case 'teenage-7-15':
      router.push('/student/teenage-english-7-15')
      break
    case 'teenage-15-18':
      router.push('/student/teenage-english-15-18')
      break
    default:
      router.push(`/student/video-player/${item.courseId}`)
  }
}

// 从后端获取最近学习记录
const fetchRecentStudies = async () => {
  try {
    const data = await api.student.getRecentStudies()
    if (data) {
      recentStudies.value = data
    }
  } catch (error) {
    console.error('Failed to fetch recent studies:', error)
  }
}



// 跳转到课程选择页面
const goToCourseSelection = () => {
  router.push('/student/course-selection')
}

// 跳转到视频播放页面
const goToVideoPlayer = (courseId: string) => {
  // 根据课程ID跳转到对应的侧边栏页面
  switch (courseId) {
    case '2': // 考研英语
      router.push('/student/kaoyan-english')
      break
    case '3': // 四六级英语
      router.push('/student/cet-english')
      break
    case '4': // 雅思托福
      router.push('/student/ielts-toefl-english')
      break
    default:
      router.push(`/student/video-player/${courseId}`)
  }
}



// 跳转到考试课程页面
const goToExamCourses = (type: string) => {
  router.push(`/student/course-selection?type=${type}`)
}

// 快捷入口 - 开始学习
const quickStartLearning = () => {
  router.push('/student/course-selection')
}

// 快捷入口 - 学习社区
const goToCommunity = () => {
  router.push('/student/community')
}

// 快捷入口 - 个人中心
const goToProfile = () => {
  router.push('/student/profile')
}

// 轮播图点击事件
const carouselClick = (link: string) => {
  router.push(link)
}

// 组件挂载时获取数据
onMounted(() => {
  console.log('Home component mounted, fetching data...')
  // 先获取观看历史，确保数据在渲染前准备好
  fetchWatchHistory()
  
  // 然后获取其他数据
  fetchRecommendedCourses()
  fetchStudentOverview()
  // 注释掉后端最近学习记录的获取，因为我们使用localStorage
  // fetchRecentStudies()
})

// 组件卸载时清理
onUnmounted(() => {
})
</script>

<style scoped>
/* 全局样式变量 */
:root {
  --primary-color: #409EFF;
  --primary-light: #66b1ff;
  --primary-dark: #3a8ee6;
  --secondary-color: #13c2c2;
  --text-color: #303133;
  --text-light: #606266;
  --text-lighter: #909399;
  --background-color: #f5f7fa;
  --card-background: #ffffff;
  --border-color: #e4e7ed;
  --shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 4px 12px rgba(0, 0, 0, 0.15);
  --shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.2);
  --border-radius-sm: 4px;
  --border-radius-md: 8px;
  --border-radius-lg: 12px;
  --transition: all 0.3s ease;
}

.student-home {
  padding: 10px 0;
  font-family: 'Arial', sans-serif;
  position: relative;
  min-height: 100vh;
}

.student-home h2 {
  margin-bottom: 20px;
  color: var(--text-color);
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
  position: relative;
  display: inline-block;
}

.student-home h2::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-light));
  border-radius: 2px;
}

/* 卡片样式 */
.el-card {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  margin-bottom: 30px;
  overflow: hidden;
  transition: var(--transition);
  background-color: var(--card-background);
  border: 1px solid var(--border-color);
}

.el-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.el-card__header {
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-card__header span {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  letter-spacing: 0.5px;
}

.el-card__body {
  padding: 24px;
}

/* 轮播图样式 */
.home-carousel {
  margin-bottom: 40px;
  overflow: hidden;
  position: relative;
  border-radius: 0;
  box-shadow: none;
}

.carousel-item {
  position: relative;
  height: 400px;
  background-color: #f0f0f0;
  overflow: hidden;
  cursor: pointer;
  transition: var(--transition);
}

.carousel-item:hover {
  transform: scale(1.02);
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  z-index: 1;
  position: relative;
  transition: transform 0.5s ease;
}

.carousel-content {
  position: absolute;
  left: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-end;
  color: #ffffff;
  z-index: 2;
  text-align: left;
  padding: 30px 40px;
  max-width: 60%;
}

.carousel-content h3 {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  line-height: 1.2;
  opacity: 1;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.carousel-content p {
  margin: 0 0 24px 0;
  font-size: 16px;
  opacity: 1;
  line-height: 1.4;
  max-width: 800px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.carousel-content .el-button {
  font-size: 14px;
  padding: 10px 24px;
  border-radius: var(--border-radius-md);
  font-weight: 500;
  transition: var(--transition);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  background-color: rgba(255, 255, 255, 0.95);
  border-color: rgba(255, 255, 255, 0.95);
  color: #000000;
  opacity: 1;
}

.carousel-content .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
  background-color: rgba(255, 255, 255, 1);
  border-color: rgba(255, 255, 255, 1);
  opacity: 1;
}

/* 轮播指示器样式 */
.el-carousel__indicators {
  bottom: 20px;
}

.el-carousel__button {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  margin: 0 6px;
  transition: all 0.3s ease;
}

.el-carousel__button--active {
  background-color: #FFD700;
  width: 24px;
  border-radius: 6px;
}





/* 课程标签 */
.course-tabs {
  margin-left: 20px;
}

/* 推荐课程 */
.course-list {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
}

.course-item {
  width: calc(33.333% - 24px);
  min-width: 300px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  transition: var(--transition);
  background-color: var(--card-background);
  box-shadow: var(--shadow-sm);
  position: relative;
}

.course-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
}

.course-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-4px);
}

.course-image {
  height: 200px;
  overflow: hidden;
  position: relative;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.course-item:hover .course-image img {
  transform: scale(1.1);
}

.course-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  border-radius: var(--border-radius-md);
  font-size: 12px;
  font-weight: 600;
  z-index: 10;
}

.new-badge {
  background-color: #67c23a;
  color: white;
  box-shadow: var(--shadow-sm);
}

.course-info {
  padding: 24px;
}

.course-info h3 {
  margin: 0 0 16px 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
  line-height: 1.4;
  height: 56px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-info p {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: var(--text-light);
  line-height: 1.6;
  height: 48px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  font-size: 12px;
  color: var(--text-lighter);
}

.course-type {
  background-color: #ecf5ff;
  color: var(--primary-color);
  padding: 2px 8px;
  border-radius: var(--border-radius-sm);
  font-weight: 500;
}

.course-students {
  display: flex;
  align-items: center;
}

.course-rating {
  display: flex;
  align-items: center;
  color: #e6a23c;
}

.star-icon {
  margin-right: 4px;
}

.course-info .el-button {
  width: 100%;
  border-radius: var(--border-radius-md);
  font-weight: 500;
  transition: var(--transition);
}

.course-info .el-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

/* 学习成就 */
.achievements-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.achievement-message {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #f0f9ff, #e6f7ff);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.motivational-quote {
  font-size: 18px;
  font-weight: 500;
  color: var(--text-color);
  margin: 0;
  line-height: 1.4;
}

.badges-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.badge-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
}

.badge-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.badge-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
  font-size: 28px;
  box-shadow: var(--shadow-sm);
}

.badge-icon.badge-locked {
  background: linear-gradient(135deg, #c0c4cc, #dcdfe6);
  opacity: 0.8;
}

.badge-info {
  flex: 1;
}

.badge-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 8px;
}

.badge-progress {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.progress-text {
  font-size: 12px;
  color: var(--text-light);
  text-align: right;
}

.badge-earned {
  font-size: 14px;
  color: #67c23a;
  font-weight: 500;
}

/* 最近学习 */
.recent-studies-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select {
  width: 150px;
}

.clear-history-btn {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.clear-history-btn:hover {
  background: var(--primary-light);
  border-color: var(--primary-light);
}

.video-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.video-item {
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  transition: var(--transition);
  background-color: var(--card-background);
  box-shadow: var(--shadow-sm);
  position: relative;
}

.video-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--secondary-color), var(--primary-color));
}

.video-item:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-4px);
}

.video-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.video-container video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.video-item:hover .video-container video {
  transform: scale(1.1);
}

.video-title {
  padding: 24px;
}

.video-title h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  line-height: 1.4;
  height: 48px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-actions {
  margin-top: 16px;
}

.continue-btn {
  width: 100%;
}

/* 页脚 */
.footer {
  margin-top: 60px;
  padding: 40px 0;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-top: 1px solid var(--border-color);
}

.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.footer-logo {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-color);
  letter-spacing: 1px;
}

.footer-links {
  display: flex;
  gap: 24px;
}

.footer-links a {
  color: var(--text-light);
  text-decoration: none;
  transition: var(--transition);
  font-size: 14px;
}

.footer-links a:hover {
  color: var(--primary-color);
}

.footer-copyright {
  font-size: 12px;
  color: var(--text-lighter);
  text-align: center;
}

/* 快捷入口悬浮按钮 */
.floating-buttons {
  position: fixed;
  bottom: 30px;
  right: 30px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 1000;
}

.floating-button {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition: var(--transition);
  border: none;
}

.floating-button:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: var(--shadow-lg);
  background: linear-gradient(135deg, var(--primary-light), var(--primary-color));
}

/* 按钮样式 */
.el-button--primary {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border-color: var(--primary-color);
  border-radius: var(--border-radius-md);
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  transition: var(--transition);
  box-shadow: var(--shadow-sm);
}

.el-button--primary:hover {
  background: linear-gradient(135deg, var(--primary-light), var(--primary-color));
  border-color: var(--primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.el-button--danger {
  border-radius: var(--border-radius-md);
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 500;
  transition: var(--transition);
  box-shadow: var(--shadow-sm);
}

.el-button--danger:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .course-item {
    width: calc(50% - 24px);
  }
  
  .overview-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .badges-container {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .carousel-content {
    padding: 40px;
  }
  
  .carousel-content h3 {
    font-size: 28px;
  }
}

@media screen and (max-width: 768px) {
  .student-home {
    padding: 15px 0;
  }
  
  .student-home h2 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .el-card__header,
  .el-card__body {
    padding: 16px;
  }
  
  .carousel-item {
    height: 300px;
  }
  
  .carousel-content {
    padding: 0 20px;
  }
  
  .carousel-content h3 {
    font-size: 20px;
  }
  
  .carousel-content p {
    font-size: 14px;
    margin-bottom: 20px;
  }
  
  .carousel-content .el-button {
    font-size: 13px;
    padding: 8px 20px;
  }
  
  .overview-stats {
    grid-template-columns: 1fr;
  }
  
  .badges-container {
    grid-template-columns: 1fr;
  }
  
  .course-item {
    width: 100%;
    min-width: unset;
  }
  
  .course-image {
    height: 180px;
  }
  
  .course-info {
    padding: 16px;
  }
  
  .course-info h3 {
    font-size: 18px;
  }
  
  .video-list {
    grid-template-columns: 1fr;
  }
  
  .video-title {
    padding: 16px;
  }
  

  
  .recent-studies-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .filter-select {
    width: 100%;
  }
  
  .floating-buttons {
    bottom: 20px;
    right: 20px;
  }
  
  .floating-button {
    width: 48px;
    height: 48px;
    font-size: 20px;
  }
  
  .footer-links {
    flex-wrap: wrap;
    justify-content: center;
  }
}

@media screen and (max-width: 480px) {
  .carousel-item {
    height: 250px;
  }
  
  .carousel-content h3 {
    font-size: 16px;
  }
  
  .carousel-content p {
    font-size: 12px;
    margin-bottom: 16px;
  }
  
  .carousel-content .el-button {
    font-size: 12px;
    padding: 6px 16px;
  }
  
  .course-tabs {
    margin-left: 0;
    margin-top: 12px;
    width: 100%;
  }
  
  .el-card__header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>