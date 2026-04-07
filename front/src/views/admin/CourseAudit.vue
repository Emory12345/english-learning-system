<template>
  <div class="course-audit">
    <h2>课程审核</h2>
    
    <!-- 课程列表 -->
    <el-card class="courses-card">
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="id" label="课程ID" />
        <el-table-column prop="title" label="课程名称" />
        <el-table-column prop="teacher" label="教师" />
        <el-table-column prop="type" label="课程类型" />
        <el-table-column prop="level" label="难度级别" />
        <el-table-column prop="submitTime" label="提交时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewCourse(scope.row.id)">查看详情</el-button>
            <el-button size="small" type="primary" @click="approveCourse(scope.row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="rejectCourse(scope.row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="courses.length"
          :page-size="10"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const currentPage = ref(1)

// 模拟课程数据
const courses = ref([
  {
    id: '1',
    title: '高级英语口语',
    teacher: '李四',
    type: '口语',
    level: '高级',
    submitTime: '2024-01-15 14:30'
  },
  {
    id: '2',
    title: '英语阅读进阶',
    teacher: '王五',
    type: '阅读',
    level: '中级',
    submitTime: '2024-01-15 13:20'
  },
  {
    id: '3',
    title: '商务英语写作',
    teacher: '赵六',
    type: '写作',
    level: '中级',
    submitTime: '2024-01-15 10:15'
  },
  {
    id: '4',
    title: '英语听力提高',
    teacher: '孙七',
    type: '听力',
    level: '初级',
    submitTime: '2024-01-14 16:45'
  },
  {
    id: '5',
    title: '英语语法精讲',
    teacher: '周八',
    type: '语法',
    level: '初级',
    submitTime: '2024-01-14 09:30'
  }
])

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 查看课程详情
const viewCourse = (courseId: string) => {
  // 实际项目中这里会跳转到课程详情页面
  ElMessage.info(`查看课程：${courseId}`)
}

// 通过课程
const approveCourse = (courseId: string) => {
  // 实际项目中这里会调用后端API
  ElMessage.success(`通过课程：${courseId}`)
}

// 拒绝课程
const rejectCourse = (courseId: string) => {
  // 实际项目中这里会调用后端API
  ElMessage.success(`拒绝课程：${courseId}`)
}
</script>

<style scoped>
.course-audit {
  padding: 20px 0;
}

.course-audit h2 {
  margin-bottom: 20px;
  color: #303133;
}

.courses-card {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>