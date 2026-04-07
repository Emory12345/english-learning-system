<template>
  <div class="admin-home">
    <h2>教师审核</h2>
    
    <!-- 审核统计 -->
    <el-card class="overview-card">
      <template #header>
        <div class="card-header">
          <span>审核统计</span>
        </div>
      </template>
      <div class="overview-stats">
        <div class="stat-item">
          <el-icon><User /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalTeachers }}</div>
            <div class="stat-label">教师总数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon><Check /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingTeachers }}</div>
            <div class="stat-label">待审核教师</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon><CircleCheck /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.approvedTeachers }}</div>
            <div class="stat-label">已批准教师</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon><Close /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.rejectedTeachers }}</div>
            <div class="stat-label">已拒绝教师</div>
          </div>
        </div>
      </div>
    </el-card>

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
        <el-table-column prop="username" label="用户名" />
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
import { ref, onMounted, reactive } from 'vue'
import { useUserStore } from '../../stores/user'
import { User, Check, CircleCheck, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

const userStore = useUserStore()

// 统计数据
const stats = reactive({
  totalTeachers: 0,
  pendingTeachers: 0,
  approvedTeachers: 0,
  rejectedTeachers: 0
})

// 待审核教师数据
const pendingTeachers = ref([])

// 加载待审核教师列表
const loadPendingTeachers = async () => {
  try {
    // 调用后端API获取待审核教师列表
    const response = await api.get('/admin/teachers/pending')
    pendingTeachers.value = response
    
    // 加载统计数据
    const statsResponse = await api.get('/admin/teachers/statistics')
    stats.totalTeachers = statsResponse.totalTeachers
    stats.pendingTeachers = statsResponse.pendingTeachers
    stats.approvedTeachers = statsResponse.approvedTeachers
    stats.rejectedTeachers = statsResponse.rejectedTeachers
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

.activities-card {
  margin-top: 20px;
}

@media screen and (max-width: 768px) {
  .stat-item {
    width: calc(50% - 20px);
  }
}
</style>