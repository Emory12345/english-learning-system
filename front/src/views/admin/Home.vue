<template>
  <div class="admin-home">
    <h2>教师审核</h2>

    <!-- 待审核教师列表 -->
    <el-card class="teachers-card">
      <template #header>
        <div class="card-header">
          <span>待审核教师</span>
        </div>
      </template>
      <el-table :data="pendingTeachers" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="certificate" label="教师资格证号" />
        <el-table-column prop="subject" label="教学科目" />
        <el-table-column prop="experience" label="教学年限" />
        <el-table-column prop="createdAt" label="申请时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="approveTeacher(scope.row.id)">批准</el-button>
            <el-button type="danger" size="small" @click="rejectTeacher(scope.row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

const userStore = useUserStore()

// 待审核教师数据
const pendingTeachers = ref([])

// 加载待审核教师列表
const loadPendingTeachers = async () => {
  try {
    // 调用后端API获取待审核教师列表
    const response = await api.get('/admin/teachers/pending')
    pendingTeachers.value = response
  } catch (error) {
    console.error('加载待审核教师失败:', error)
    ElMessage.error('加载待审核教师失败')
  }
}

// 批准教师
const approveTeacher = async (id: number) => {
  try {
    // 调用后端API批准教师
    await api.post(`/admin/teachers/approve/${id}`)
    ElMessage.success('教师批准成功')
    // 重新加载待审核教师列表
    loadPendingTeachers()
  } catch (error) {
    console.error('批准教师失败:', error)
    ElMessage.error('批准教师失败')
  }
}

// 拒绝教师
const rejectTeacher = async (id: number) => {
  try {
    // 调用后端API拒绝教师
    await api.post(`/admin/teachers/reject/${id}`)
    ElMessage.success('教师拒绝成功')
    // 重新加载待审核教师列表
    loadPendingTeachers()
  } catch (error) {
    console.error('拒绝教师失败:', error)
    ElMessage.error('拒绝教师失败')
  }
}

// 页面加载时获取待审核教师列表
onMounted(() => {
  loadPendingTeachers()
})
</script>

<style scoped>
.admin-home {
  padding: 20px 0;
}

.admin-home h2 {
  margin-bottom: 20px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activities-card {
  margin-top: 20px;
}
</style>