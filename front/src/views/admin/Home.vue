<template>
  <div class="admin-home">
    <h2>欢迎回来，{{ userStore.userInfo.name }}！</h2>
    
    <!-- 系统概览 -->
    <el-card class="overview-card">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>
      <div class="overview-stats">
        <div class="stat-item">
          <el-icon><User /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.users }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon><Document /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.courses }}</div>
            <div class="stat-label">课程总数</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon><Check /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingCourses }}</div>
            <div class="stat-label">待审核课程</div>
          </div>
        </div>
        <div class="stat-item">
          <el-icon><Timer /></el-icon>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalStudyTime }}</div>
            <div class="stat-label">总学习时长（小时）</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 最近活动 -->
    <el-card class="activities-card">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
        </div>
      </template>
      <el-table :data="recentActivities" style="width: 100%">
        <el-table-column prop="time" label="时间" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="user" label="操作人" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '../../stores/user'
import { User, Document, Check, Timer } from '@element-plus/icons-vue'

const userStore = useUserStore()

// 模拟统计数据
const stats = {
  users: 500,
  courses: 100,
  pendingCourses: 5,
  totalStudyTime: 10000
}

// 模拟最近活动数据
const recentActivities = [
  {
    time: '2024-01-15 14:30',
    type: '用户注册',
    description: '新用户注册',
    user: '张三'
  },
  {
    time: '2024-01-15 13:20',
    type: '课程提交',
    description: '教师提交新课程',
    user: '李四'
  },
  {
    time: '2024-01-15 10:15',
    type: '课程审核',
    description: '审核通过课程',
    user: '管理员'
  },
  {
    time: '2024-01-14 16:45',
    type: '用户登录',
    description: '用户登录系统',
    user: '王五'
  }
]
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