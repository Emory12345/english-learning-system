<template>
  <div class="course-management">
    <h2>课程管理</h2>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="addCourse">添加课程</el-button>
    </div>

    <!-- 课程列表 -->
    <el-card class="courses-card">
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="id" label="课程ID" />
        <el-table-column prop="title" label="课程名称" />
        <el-table-column prop="type" label="课程类型" />
        <el-table-column prop="level" label="难度级别" />
        <el-table-column prop="duration" label="课时数量" />
        <el-table-column prop="studentCount" label="学生人数" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="editCourse(scope.row.id)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCourse(scope.row.id)">删除</el-button>
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
    title: '初级英语口语',
    type: '口语',
    level: '初级',
    duration: 20,
    studentCount: 30
  },
  {
    id: '2',
    title: '商务英语进阶',
    type: '口语',
    level: '中级',
    duration: 30,
    studentCount: 25
  },
  {
    id: '3',
    title: '英语听力训练',
    type: '听力',
    level: '中级',
    duration: 25,
    studentCount: 20
  },
  {
    id: '4',
    title: '英语阅读技巧',
    type: '阅读',
    level: '中级',
    duration: 22,
    studentCount: 18
  },
  {
    id: '5',
    title: '英语写作提升',
    type: '写作',
    level: '高级',
    duration: 28,
    studentCount: 15
  }
])

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 添加课程
const addCourse = () => {
  // 实际项目中这里会打开添加课程的弹窗
  ElMessage.info('添加课程功能')
}

// 编辑课程
const editCourse = (courseId: string) => {
  // 实际项目中这里会打开编辑课程的弹窗
  ElMessage.info(`编辑课程：${courseId}`)
}

// 删除课程
const deleteCourse = (courseId: string) => {
  // 实际项目中这里会弹出确认框
  ElMessage.info(`删除课程：${courseId}`)
}
</script>

<style scoped>
.course-management {
  padding: 20px 0;
}

.course-management h2 {
  margin-bottom: 20px;
  color: #303133;
}

.action-buttons {
  margin-bottom: 20px;
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