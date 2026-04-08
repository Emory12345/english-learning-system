<template>
  <div class="video-player">
    <el-loading v-if="loading" fullscreen text="加载中..." />
    
    <div v-if="error" class="error-message">
      <el-alert
        :title="error"
        type="error"
        show-icon
        :closable="false"
      />
    </div>
    
    <div v-else-if="course.title">
      <h2>{{ course.title }}</h2>
      
      <div class="player-container">
        <!-- 视频播放器 -->
        <div class="video-wrapper">
          <el-video-player
            class="video-player"
            :src="getCurrentChapterVideo()"
            :poster="`http://localhost:8080${course.image}`"
            :autoplay="false"
            :controls="true"
          />
        </div>
        
        <!-- 课程信息 -->
        <div class="course-details">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>课程信息</span>
              </div>
            </template>
            <div class="course-info">
              <p><strong>课程类型：</strong>{{ course.type }}</p>
              <p><strong>难度级别：</strong>{{ course.level }}</p>
              <p><strong>课时数量：</strong>{{ course.duration }} 课时</p>
              <p><strong>课程描述：</strong>{{ course.description }}</p>
            </div>
          </el-card>
          
          <!-- 课程章节 -->
          <el-card class="chapters-card">
            <template #header>
              <div class="card-header">
                <span>课程章节</span>
              </div>
            </template>
            <div class="chapters-list">
              <div 
                v-for="chapter in course.chapters" 
                :key="chapter.id"
                class="chapter-item"
                :class="{ active: currentChapter === chapter.id }"
                @click="playChapter(chapter)"
              >
                <el-icon><VideoCamera /></el-icon>
                <div class="chapter-info">
                  <h4>{{ chapter.title }}</h4>
                  <span class="chapter-duration">{{ chapter.duration }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      
      <!-- 课程作业 -->
      <el-card class="homework-card">
        <template #header>
          <div class="card-header">
            <span>课程作业</span>
          </div>
        </template>
        <div class="homework-list">
          <div v-for="homework in course.homeworks" :key="homework.id" class="homework-item">
            <h4>{{ homework.title }}</h4>
            <p>{{ homework.description }}</p>
            <div class="homework-meta">
              <span>截止日期：{{ homework.deadline }}</span>
              <span>状态：{{ homework.status }}</span>
            </div>
            <el-button type="primary" size="small">提交作业</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { VideoCamera } from '@element-plus/icons-vue'
import { api } from '@/api'

const route = useRoute()
const courseId = route.params.courseId as string
const currentChapter = ref('1')
const loading = ref(true)
const error = ref('')

// 课程数据
const course = ref({
  id: courseId,
  title: '',
  description: '',
  type: '',
  level: '',
  duration: 0,
  image: '',
  videoUrl: '',
  chapters: [] as any[],
  homeworks: [] as any[]
})

// 获取当前章节的视频URL
const getCurrentChapterVideo = () => {
  const chapter = course.value.chapters.find((c: any) => c.id === currentChapter.value)
  return chapter?.videoUrl || course.value.videoUrl || ''
}

// 播放当前章节的视频
const playChapter = (chapter: any) => {
  currentChapter.value = chapter.id
  // 保存观看历史
  saveWatchHistory()
}

// 保存观看历史
const saveWatchHistory = () => {
  try {
    console.log('Saving watch history...')
    console.log('Course data:', course.value)
    const historyKey = 'watchHistory'
    let history = []
    // 从localStorage获取现有历史
    const existingHistory = localStorage.getItem(historyKey)
    if (existingHistory) {
      history = JSON.parse(existingHistory)
      console.log('Existing history:', history)
    }
    // 创建新的观看记录
    const newRecord = {
      courseId: course.value.id,
      courseTitle: course.value.title,
      courseImage: course.value.image,
      courseType: course.value.type,
      videoUrl: course.value.videoUrl || course.value.chapters?.[0]?.videoUrl || '',
      lastWatchTime: new Date().toISOString(),
      currentChapter: currentChapter.value
    }
    console.log('New record:', newRecord)
    // 移除已存在的相同课程记录
    history = history.filter((item: any) => item.courseId !== course.value.id)
    // 添加新记录到开头
    history.unshift(newRecord)
    // 只保留最近4条记录
    if (history.length > 4) {
      history = history.slice(0, 4)
    }
    // 保存到localStorage
    localStorage.setItem(historyKey, JSON.stringify(history))
    console.log('Watch history saved:', history)
  } catch (error) {
    console.error('Failed to save watch history:', error)
  }
}

onMounted(async () => {
  try {
    loading.value = true
    // 从后端获取课程详情
    const courseData = await api.courses.getDetail(courseId)
    course.value = courseData
    // 设置默认播放第一个章节
    if (courseData.chapters && courseData.chapters.length > 0) {
      currentChapter.value = courseData.chapters[0].id
    }
    // 保存观看历史
    saveWatchHistory()
  } catch (err: any) {
    error.value = err.message || '加载课程失败'
    console.error('Failed to load course:', err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.video-player {
  padding: 20px 0;
}

.video-player h2 {
  margin-bottom: 20px;
  color: #303133;
}

.player-container {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.video-wrapper {
  flex: 1;
  min-width: 600px;
}

.video-player {
  width: 100%;
  height: 400px;
}

.course-details {
  width: 350px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-info p {
  margin: 10px 0;
  font-size: 14px;
}

.chapters-card {
  margin-top: 20px;
}

.chapter-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.chapter-item:hover {
  background-color: #f5f7fa;
}

.chapter-item.active {
  background-color: #ecf5ff;
  color: #409EFF;
}

.chapter-item .el-icon {
  margin-right: 10px;
  font-size: 20px;
}

.chapter-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chapter-info h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
}

.chapter-duration {
  font-size: 12px;
  color: #909399;
}

.homework-card {
  margin-top: 20px;
}

.homework-item {
  padding: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.homework-item:last-child {
  border-bottom: none;
}

.homework-item h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
}

.homework-item p {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #606266;
}

.homework-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 12px;
  color: #909399;
}

@media screen and (max-width: 768px) {
  .player-container {
    flex-direction: column;
  }
  
  .video-wrapper {
    min-width: 100%;
  }
  
  .course-details {
    width: 100%;
  }
}
</style>