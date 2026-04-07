<template>
  <div class="data-statistics">
    <h2>数据统计</h2>
    
    <!-- 统计概览 -->
    <el-card class="overview-card">
      <template #header>
        <div class="card-header">
          <span>统计概览</span>
        </div>
      </template>
      <div class="overview-stats">
        <div class="stat-item">
          <div class="stat-value">{{ stats.users }}</div>
          <div class="stat-label">用户总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.courses }}</div>
          <div class="stat-label">课程总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.teachers }}</div>
          <div class="stat-label">教师总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.students }}</div>
          <div class="stat-label">学生总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.totalStudyTime }}</div>
          <div class="stat-label">总学习时长（小时）</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.completedCourses }}</div>
          <div class="stat-label">已完成课程数</div>
        </div>
      </div>
    </el-card>

    <!-- 数据图表 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>数据图表</span>
        </div>
      </template>
      <div class="charts-container">
        <div class="chart-item">
          <h3>用户增长趋势</h3>
          <div class="chart-placeholder">
            <p>用户增长趋势图表</p>
            <p>实际项目中这里会使用 ECharts 等图表库实现</p>
          </div>
        </div>
        <div class="chart-item">
          <h3>课程类型分布</h3>
          <div class="chart-placeholder">
            <p>课程类型分布图表</p>
            <p>实际项目中这里会使用 ECharts 等图表库实现</p>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

// 模拟统计数据
const stats = reactive({
  users: 500,
  courses: 100,
  teachers: 20,
  students: 480,
  totalStudyTime: 10000,
  completedCourses: 500
})
</script>

<style scoped>
.data-statistics {
  padding: 20px 0;
}

.data-statistics h2 {
  margin-bottom: 20px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-card {
  margin-bottom: 20px;
}

.overview-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  text-align: center;
}

.chart-card {
  margin-top: 20px;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-item {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
}

.chart-item h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #606266;
}

@media screen and (max-width: 768px) {
  .charts-container {
    grid-template-columns: 1fr;
  }
  
  .chart-item {
    min-width: 100%;
  }
}
</style>