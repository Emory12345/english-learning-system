<template>
  <div class="course-management">
    <h2>课程管理</h2>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="openAddDialog">添加课程</el-button>
    </div>

    <!-- 课程列表 -->
    <el-card class="courses-card">
      <el-table :data="courses" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="课程ID" width="80" />
        <el-table-column prop="name" label="课程名称" />
        <el-table-column prop="category" label="课程类型" />
        <el-table-column prop="level" label="难度级别" />
        <el-table-column prop="duration" label="课程时长(分钟)" />
        <el-table-column label="审核状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'pending'" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 'approved'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.status === 'rejected'" type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
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

    <!-- 添加/编辑课程对话框 -->
    <el-dialog
      :title="isEdit ? '编辑课程' : '添加课程'"
      v-model="dialogVisible"
      width="500px"
    >
      <el-form :model="courseForm" label-width="100px">
        <el-form-item label="课程名称">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input v-model="courseForm.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
        </el-form-item>
        <el-form-item label="课程类型">
          <el-select v-model="courseForm.category" placeholder="请选择课程类型">
            <el-option label="青少年英语" value="teenage" />
            <el-option label="考研英语" value="kaoyan" />
            <el-option label="四六级英语" value="cet" />
            <el-option label="雅思托福" value="ielts_toefl" />
            <el-option label="商务英语" value="business" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度级别">
          <el-select v-model="courseForm.level" placeholder="请选择难度级别">
            <el-option label="入门" value="beginner" />
            <el-option label="基础" value="basic" />
            <el-option label="进阶" value="intermediate" />
            <el-option label="高级" value="advanced" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程时长">
          <el-input-number v-model="courseForm.duration" :min="1" :max="1000" placeholder="分钟" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-input v-model="courseForm.coverImage" placeholder="请输入封面图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'

const currentPage = ref(1)
const courses = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const courseForm = ref({
  id: null,
  name: '',
  description: '',
  category: '',
  level: '',
  duration: 60,
  coverImage: ''
})

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

// 打开添加课程对话框
const openAddDialog = () => {
  isEdit.value = false
  courseForm.value = {
    id: null,
    name: '',
    description: '',
    category: '',
    level: '',
    duration: 60,
    coverImage: ''
  }
  dialogVisible.value = true
}

// 打开编辑课程对话框
const openEditDialog = (course: any) => {
  isEdit.value = true
  courseForm.value = {
    id: course.id,
    name: course.name,
    description: course.description,
    category: course.category,
    level: course.level,
    duration: course.duration,
    coverImage: course.coverImage
  }
  dialogVisible.value = true
}

// 提交课程
const handleSubmit = async () => {
  if (!courseForm.value.name) {
    ElMessage.warning('请输入课程名称')
    return
  }
  if (!courseForm.value.category) {
    ElMessage.warning('请选择课程类型')
    return
  }
  if (!courseForm.value.level) {
    ElMessage.warning('请选择难度级别')
    return
  }

  try {
    if (isEdit.value) {
      await api.courses.update(courseForm.value.id.toString(), courseForm.value)
      ElMessage.success('课程更新成功')
    } else {
      await api.courses.create(courseForm.value)
      ElMessage.success('课程发布成功，等待管理员审核')
    }
    dialogVisible.value = false
    await getTeacherCourses()
  } catch (error: any) {
    console.error('提交课程失败:', error)
    ElMessage.error('提交课程失败')
  }
}

// 删除课程
const handleDelete = async (courseId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个课程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.courses.delete(courseId.toString())
    ElMessage.success('删除成功')
    await getTeacherCourses()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除课程失败:', error)
      ElMessage.error('删除课程失败')
    }
  }
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