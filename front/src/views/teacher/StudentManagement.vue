<template>
  <div class="student-management">
    <h2>学生管理</h2>
    
    <!-- 课程选择 -->
    <el-card class="filter-card">
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="课程">
            <el-select v-model="filterForm.courseId" placeholder="请选择课程">
              <el-option label="全部" value="" />
              <el-option v-for="course in courses" :key="course.id" :label="course.title" :value="course.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input v-model="filterForm.keyword" placeholder="搜索学生姓名或邮箱" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 学生列表 -->
    <el-card class="students-card">
      <el-table :data="filteredStudents" style="width: 100%">
        <el-table-column prop="id" label="学生ID" />
        <el-table-column prop="name" label="学生姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="courseTitle" label="课程名称" />
        <el-table-column prop="enrollTime" label="报名时间" />
        <el-table-column prop="progress" label="学习进度" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewStudent(scope.row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="students.length"
          :page-size="10"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const currentPage = ref(1)

// 筛选表单
const filterForm = ref({
  courseId: '',
  keyword: ''
})

// 模拟课程数据
const courses = ref([
  { id: '1', title: '初级英语口语' },
  { id: '2', title: '商务英语进阶' },
  { id: '3', title: '英语听力训练' }
])

// 模拟学生数据
const students = ref([
  {
    id: '1',
    name: '张三',
    email: 'zhangsan@example.com',
    courseId: '1',
    courseTitle: '初级英语口语',
    enrollTime: '2024-01-01',
    progress: '60%'
  },
  {
    id: '2',
    name: '李四',
    email: 'lisi@example.com',
    courseId: '2',
    courseTitle: '商务英语进阶',
    enrollTime: '2024-01-02',
    progress: '30%'
  },
  {
    id: '3',
    name: '王五',
    email: 'wangwu@example.com',
    courseId: '3',
    courseTitle: '英语听力训练',
    enrollTime: '2024-01-03',
    progress: '10%'
  },
  {
    id: '4',
    name: '赵六',
    email: 'zhaoliu@example.com',
    courseId: '1',
    courseTitle: '初级英语口语',
    enrollTime: '2024-01-04',
    progress: '80%'
  },
  {
    id: '5',
    name: '孙七',
    email: 'sunqi@example.com',
    courseId: '2',
    courseTitle: '商务英语进阶',
    enrollTime: '2024-01-05',
    progress: '40%'
  }
])

// 筛选后的学生
const filteredStudents = computed(() => {
  return students.value.filter(student => {
    const matchesCourse = !filterForm.value.courseId || student.courseId === filterForm.value.courseId
    const matchesKeyword = !filterForm.value.keyword || 
      student.name.toLowerCase().includes(filterForm.value.keyword.toLowerCase()) || 
      student.email.toLowerCase().includes(filterForm.value.keyword.toLowerCase())
    return matchesCourse && matchesKeyword
  })
})

// 搜索学生
const handleSearch = () => {
  currentPage.value = 1
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 查看学生详情
const viewStudent = (studentId: string) => {
  // 实际项目中这里会跳转到学生详情页面
  ElMessage.info(`查看学生：${studentId}`)
}
</script>

<style scoped>
.student-management {
  padding: 20px 0;
}

.student-management h2 {
  margin-bottom: 20px;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  align-items: center;
}

.students-card {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media screen and (max-width: 768px) {
  .filter-form {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
}
</style>