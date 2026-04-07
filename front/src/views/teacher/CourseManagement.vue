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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

const currentPage = ref(1)
const courses = ref<any[]>([])
const loading = ref(false)

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 获取教师课程列表
const getTeacherCourses = async () => {
  loading.value = true
  try {
    const response = await api.courses.getTeacherCourses()
    courses.value = response
  } catch (error: any) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
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

// 页面加载时获取课程列表
onMounted(() => {
  getTeacherCourses()
})
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