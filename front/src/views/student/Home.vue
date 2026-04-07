<template>
  <div class="student-home">
    <h2>欢迎回来，{{ userStore.userInfo.name }}！</h2>
    
    <!-- 轮播图 -->
    <el-carousel :interval="5000" type="card" height="300px" class="home-carousel">
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
    
    <!-- 学习概览 -->
    <el-card class="overview-card">
      <template #header>
        <div class="card-header">
          <span>近一周学习时间</span>
        </div>
      </template>
      <div ref="chartRef" class="study-chart"></div>
    </el-card>



    <!-- 推荐课程 -->
    <el-card class="recommended-courses">
      <template #header>
        <div class="card-header">
          <span>推荐课程</span>
        </div>
      </template>
      <div class="course-list">
        <div v-for="course in recommendedCourses" :key="course.id" class="course-item">
          <div class="course-image">
            <img :src="`http://localhost:8080${course.image}`" :alt="course.title" />
          </div>
          <div class="course-info">
            <h3>{{ course.title }}</h3>
            <p>{{ course.description }}</p>
            <el-button type="primary" size="small" @click="goToVideoPlayer(course.id)">开始学习</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 最近学习 -->
    <el-card class="recent-studies">
      <template #header>
        <div class="card-header">
          <span>最近学习</span>
        </div>
      </template>
      <el-table :data="recentStudies" style="width: 100%">
        <el-table-column prop="courseTitle" label="课程名称" />
        <el-table-column label="最近学习时间">
          <template #default="scope">
            {{ formatTime(scope.row.lastStudyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="学习进度" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goToVideoPlayer(scope.row.courseId)">继续学习</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { Timer, Document, Check, Star } from '@element-plus/icons-vue'
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
let chartInstance: echarts.ECharts | null = null
const chartRef = ref<HTMLElement>()

const studyDates = ref<string[]>([])
const studyMinutes = ref<number[]>([])

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
  },
  {
    id: '5',
    title: '企业英语培训',
    description: '提升职场英语沟通能力，助力职业发展',
    image: businessEnglishImg,
    link: '/student/business-english',
    buttonText: '立即学习'
  }
])

// 推荐课程数据
const recommendedCourses = ref([
  {
    id: '2',
    title: '考研英语',
    description: '为考研学生设计的英语课程，涵盖词汇、阅读、写作等核心内容，帮助你在考研英语中取得优异成绩。',
    image: '/uploads/graduate-exam.jpg'
  },
  {
    id: '3',
    title: '四六级英语',
    description: '针对大学英语四六级考试的专项课程，提供听力、阅读、翻译、写作全方位训练，助你轻松通过考试。',
    image: '/uploads/cet-4-6.jpg'
  },
  {
    id: '4',
    title: '雅思托福',
    description: '为准备出国留学的学生提供的雅思托福考试培训，全面提升英语能力，助你取得理想成绩。',
    image: '/uploads/ielts-toefl.jpg'
  }
])

// 学习概览数据
const overviewData = ref({
  learningTime: 0,
  completedCourses: 0,
  completedExercises: 0,
  accuracy: '0%'
})

// 最近学习数据
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



// 从后端获取推荐课程
const fetchRecommendedCourses = async () => {
  try {
    loading.value = true
    const courses = await api.courses.getList()
    if (courses && courses.length > 0) {
      // 取前3个课程作为推荐课程
      recommendedCourses.value = courses.slice(0, 3)
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
    overviewData.value = data
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

// 从后端获取近一周学习时间
const fetchWeeklyStudyRecords = async () => {
  try {
    const data = await api.studyRecord.getWeekly()
    if (data && data.success) {
      studyDates.value = [...data.dates]
      studyMinutes.value = [...data.minutes]
      await nextTick()
      initChart()
    }
  } catch (error) {
    console.error('Failed to fetch weekly study records:', error)
  }
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(chartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>学习时长: {c} 分钟'
    },
    xAxis: {
      type: 'category',
      data: studyDates.value,
      axisLabel: {
        color: '#606266'
      },
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      }
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      axisLabel: {
        color: '#606266'
      },
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      },
      splitLine: {
        lineStyle: {
          color: '#f0f0f0'
        }
      }
    },
    series: [
      {
        name: '学习时长',
        type: 'line',
        data: studyMinutes.value,
        smooth: true,
        lineStyle: {
          color: '#409EFF',
          width: 3
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        itemStyle: {
          color: '#409EFF',
          borderColor: '#fff',
          borderWidth: 2
        },
        emphasis: {
          itemStyle: {
            color: '#409EFF',
            borderColor: '#fff',
            borderWidth: 3,
            shadowBlur: 10,
            shadowColor: 'rgba(64, 158, 255, 0.5)'
          }
        }
      }
    ],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    }
  }
  
  chartInstance.setOption(option)
}

// 处理窗口大小变化
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
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

// 轮播图点击事件
const carouselClick = (link: string) => {
  router.push(link)
}

// 跳转到考试课程页面
const goToExamCourses = (type: string) => {
  router.push(`/student/course-selection?type=${type}`)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchRecommendedCourses()
  fetchStudentOverview()
  fetchRecentStudies()
  fetchWeeklyStudyRecords()
  
  // 添加窗口大小变化监听
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.student-home {
  padding: 20px 0;
}

.student-home h2 {
  margin-bottom: 20px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-stats {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  margin: 10px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  width: calc(25% - 20px);
  min-width: 150px;
}

.stat-item .el-icon {
  font-size: 24px;
  color: #409EFF;
  margin-right: 10px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 12px;
  color: #606266;
}

.study-chart {
  width: 100%;
  height: 400px;
}

.course-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.course-item {
  width: calc(33.333% - 20px);
  min-width: 250px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.course-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.course-image {
  height: 150px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  padding: 15px;
}

.course-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
}

.course-info p {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}



.home-carousel {
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
}

.carousel-item {
  position: relative;
  height: 100%;
  background-color: #f0f0f0;
  cursor: pointer;
  transition: transform 0.3s ease;
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
}

.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.8));
  color: white;
  padding: 30px;
  z-index: 2;
}

.carousel-content h3 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: bold;
}

.carousel-content p {
  margin: 0 0 20px 0;
  font-size: 16px;
  opacity: 0.9;
}

.exam-english-modules {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}



.recent-studies {
  margin-top: 20px;
}

@media screen and (max-width: 768px) {
  .stat-item {
    width: calc(50% - 20px);
  }
  
  .course-item {
    width: 100%;
  }
  
  .exam-english-modules {
    flex-direction: column;
  }
  
  .exam-content {
    flex-direction: column;
  }
  
  .exam-image {
    flex: 0 0 150px;
    width: 100%;
  }
  
  .carousel-content h3 {
    font-size: 18px;
  }
  
  .carousel-content p {
    font-size: 14px;
  }
}
</style>