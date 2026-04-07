<template>
  <div class="teacher-home">
    <h2>欢迎回来，{{ userStore.userInfo.name }}！</h2>
    
    <!-- 工作概览 -->
    <el-card class="overview-card">
      <template #header>
        <div class="card-header">
          <span>工作概览</span>
        </div>
      </template>
      <div class="overview-stats">
        <div class="stat-item">
          <el-icon><Document /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.courses }}</div>
            <div class="stat-label">授课课程</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon><User /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.students }}</div>
            <div class="stat-label">学生人数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon><Check /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingHomeworks }}</div>
            <div class="stat-label">待批改作业</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon><Timer /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.teachingHours }}</div>
            <div class="stat-label">授课时长（小时）</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 待批改作业 -->
    <el-card class="homework-card">
      <template #header>
        <div class="card-header">
          <span>待批改作业</span>
          <el-button type="primary" size="small" @click="goToHomeworkCorrection">查看全部</el-button>
        </div>
      </template>
      <el-table :data="pendingHomeworks" style="width: 100%">
        <el-table-column prop="courseTitle" label="课程名称" />
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="submitTime" label="提交时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" size="small" @click="correctHomework(scope.row.id)">批改</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 最近课程 -->
    <el-card class="courses-card">
      <template #header>
        <div class="card-header">
          <span>最近课程</span>
          <el-button type="primary" size="small" @click="goToCourseManagement">查看全部</el-button>
        </div>
      </template>
      <div class="course-list">
        <div v-for="course in recentCourses" :key="course.id" class="course-item">
          <h3>{{ course.title }}</h3>
          <div class="course-meta">
            <span>{{ course.studentCount }} 名学生</span>
            <span>{{ course.nextClass }}</span>
          </div>
          <el-button size="small" @click="manageCourse(course.id)">管理课程</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { Document, User, Check, Timer } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

// 模拟统计数据
const stats = {
  courses: 5,
  students: 120,
  pendingHomeworks: 8,
  teachingHours: 120
}

// 模拟待批改作业数据
const pendingHomeworks = [
  {
    id: '1',
    courseTitle: '初级英语口语',
    studentName: '张三',
    submitTime: '2024-01-15 14:30'
  },
  {
    id: '2',
    courseTitle: '商务英语进阶',
    studentName: '李四',
    submitTime: '2024-01-15 13:20'
  },
  {
    id: '3',
    courseTitle: '英语听力训练',
    studentName: '王五',
    submitTime: '2024-01-15 10:15'
  }
]

// 模拟最近课程数据
const recentCourses = [
  {
    id: '1',
    title: '初级英语口语',
    studentCount: 30,
    nextClass: '今天 14:00'
  },
  {
    id: '2',
    title: '商务英语进阶',
    studentCount: 25,
    nextClass: '明天 09:00'
  },
  {
    id: '3',
    title: '英语听力训练',
    studentCount: 20,
    nextClass: '明天 16:30'
  }
]

// 跳转到作业批改页面
const goToHomeworkCorrection = () => {
  router.push('/teacher/homework-correction')
}

// 跳转到课程管理页面
const goToCourseManagement = () => {
  router.push('/teacher/course-management')
}

// 批改作业
const correctHomework = (homeworkId: string) => {
  // 实际项目中这里会跳转到作业批改详情页面
  console.log('批改作业:', homeworkId)
}

// 管理课程
const manageCourse = (courseId: string) => {
  // 实际项目中这里会跳转到课程管理详情页面
  console.log('管理课程:', courseId)
}
</script>

<style scoped>
.teacher-home {
  padding: 20px 0;
}

.teacher-home h2 {
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

.homework-card {
  margin-top: 20px;
}

.courses-card {
  margin-top: 20px;
}

.course-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.course-item {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  width: calc(33.333% - 20px);
  min-width: 250px;
  transition: all 0.3s;
}

.course-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.course-item h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 14px;
  color: #606266;
}

@media screen and (max-width: 768px) {
  .stat-item {
    width: calc(50% - 20px);
  }
  
  .course-item {
    width: 100%;
  }
}
</style>