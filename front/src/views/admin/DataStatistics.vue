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
          <div class="stat-value">{{ userStats.totalUsers || 0 }}</div>
          <div class="stat-label">用户总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ userStats.studentCount || 0 }}</div>
          <div class="stat-label">学生总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ userStats.teacherCount || 0 }}</div>
          <div class="stat-label">教师总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ userStats.adminCount || 0 }}</div>
          <div class="stat-label">管理员数</div>
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
          <div ref="userGrowthChart" class="chart-container"></div>
        </div>
        <div class="chart-item">
          <h3>角色分布</h3>
          <div ref="roleDistributionChart" class="chart-container"></div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'
import * as echarts from 'echarts'

// 用户统计数据
const userStats = reactive({
  totalUsers: 0,
  studentCount: 0,
  teacherCount: 0,
  adminCount: 0
})

// 用户增长数据
const userGrowthData = reactive({
  dates: [] as string[],
  newStudents: [] as number[],
  newTeachers: [] as number[]
})

// 图表引用
const userGrowthChart = ref<HTMLElement | null>(null)
const roleDistributionChart = ref<HTMLElement | null>(null)

// 初始化图表
const initCharts = () => {
  // 用户增长趋势图表
  if (userGrowthChart.value) {
    const chart = echarts.init(userGrowthChart.value)
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['学生', '教师']
      },
      xAxis: {
        type: 'category',
        data: userGrowthData.dates
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '学生',
          data: userGrowthData.newStudents,
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#409EFF'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgba(64, 158, 255, 0.3)'
              },
              {
                offset: 1,
                color: 'rgba(64, 158, 255, 0.1)'
              }
            ])
          }
        },
        {
          name: '教师',
          data: userGrowthData.newTeachers,
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#67C23A'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgba(103, 194, 58, 0.3)'
              },
              {
                offset: 1,
                color: 'rgba(103, 194, 58, 0.1)'
              }
            ])
          }
        }
      ]
    }
    chart.setOption(option)
    
    // 响应式调整
    window.addEventListener('resize', () => {
      chart.resize()
    })
  }
  
  // 角色分布图表
  if (roleDistributionChart.value) {
    const chart = echarts.init(roleDistributionChart.value)
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [{
        name: '角色分布',
        type: 'pie',
        radius: '50%',
        data: [
          { value: userStats.studentCount, name: '学生' },
          { value: userStats.teacherCount, name: '教师' },
          { value: userStats.adminCount, name: '管理员' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    }
    chart.setOption(option)
    
    // 响应式调整
    window.addEventListener('resize', () => {
      chart.resize()
    })
  }
}

// 获取用户统计数据
const fetchUserStats = async () => {
  try {
    const response = await api.users.getStats()
    userStats.totalUsers = response.totalUsers
    userStats.studentCount = response.studentCount
    userStats.teacherCount = response.teacherCount
    userStats.adminCount = response.adminCount
  } catch (error) {
    console.error('获取用户统计数据失败:', error)
    ElMessage.error('获取用户统计数据失败')
  }
}

// 获取用户增长数据
const fetchUserGrowth = async () => {
  try {
    const response = await api.users.getGrowth()
    userGrowthData.dates = response.dates
    userGrowthData.newStudents = response.newStudents
    userGrowthData.newTeachers = response.newTeachers
  } catch (error) {
    console.error('获取用户增长数据失败:', error)
    ElMessage.error('获取用户增长数据失败')
  }
}

// 页面加载时获取数据并初始化图表
onMounted(async () => {
  await fetchUserStats()
  await fetchUserGrowth()
  
  // 延迟初始化图表，确保DOM已渲染
  setTimeout(initCharts, 100)
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

.chart-container {
  height: 300px;
  width: 100%;
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