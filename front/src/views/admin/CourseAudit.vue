<template>
  <div class="course-audit">
    <h2>课程审核</h2>
    
    <!-- 课程列表 -->
    <el-card class="courses-card">
      <el-table :data="courses" style="width: 100%">
        <el-table-column prop="id" label="课程ID" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="teacher.name" label="教师" />
        <el-table-column prop="category" label="课程类型" />
        <el-table-column prop="level" label="难度级别" />
        <el-table-column prop="duration" label="课程时长(分钟)" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewCourse(scope.row)">查看详情</el-button>
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

const currentPage = ref(1)
const courses = ref<any[]>([])

// 获取待审核课程列表
const fetchPendingCourses = async () => {
  try {
    const response = await api.admin.getPendingCourses()
    courses.value = response
  } catch (error) {
    console.error('获取待审核课程失败:', error)
    ElMessage.error('获取待审核课程失败')
  }
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 查看课程详情
const viewCourse = (course: any) => {
  ElMessage.info(`课程详情: ${course.name}`)
}

// 通过课程
const approveCourse = async (courseId: number) => {
  try {
    await api.admin.auditCourse({ courseId: courseId.toString(), status: 'approved' })
    ElMessage.success('课程审核通过')
    await fetchPendingCourses()
  } catch (error) {
    console.error('课程审核失败:', error)
    ElMessage.error('课程审核失败')
  }
}

// 拒绝课程
const rejectCourse = async (courseId: number) => {
  try {
    await api.admin.auditCourse({ courseId: courseId.toString(), status: 'rejected' })
    ElMessage.success('课程已拒绝')
    await fetchPendingCourses()
  } catch (error) {
    console.error('课程拒绝失败:', error)
    ElMessage.error('课程拒绝失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchPendingCourses()
})
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
