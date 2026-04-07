<template>
  <div class="grade-statistics">
    <h2>成绩统计</h2>
    
    <!-- 课程选择 -->
    <el-card class="filter-card">
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="课程">
            <el-select v-model="filterForm.courseId" placeholder="请选择课程">
              <el-option v-for="course in courses" :key="course.id" :label="course.title" :value="course.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查看统计</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 统计概览 -->
    <el-card class="overview-card">
      <template #header>
        <div class="card-header">
          <span>统计概览</span>
        </div>
      </template>
      <div class="overview-stats">
        <div class="stat-item">
          <div class="stat-value">{{ stats.averageScore }}</div>
          <div class="stat-label">平均成绩</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.passRate }}%</div>
          <div class="stat-label">及格率</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.highScore }}</div>
          <div class="stat-label">最高分</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.lowScore }}</div>
          <div class="stat-label">最低分</div>
        </div>
      </div>
    </el-card>

    <!-- 成绩分布 -->
    <el-card class="distribution-card">
      <template #header>
        <div class="card-header">
          <span>成绩分布</span>
        </div>
      </template>
      <div class="distribution-chart">
        <!-- 这里可以使用 ECharts 等图表库实现 -->
        <div class="chart-placeholder">
          <p>成绩分布图表</p>
          <p>实际项目中这里会使用 ECharts 等图表库实现</p>
        </div>
      </div>
    </el-card>

    <!-- 学生成绩列表 -->
    <el-card class="grades-card">
      <template #header>
        <div class="card-header">
          <span>学生成绩列表</span>
        </div>
      </template>
      <el-table :data="studentGrades" style="width: 100%">
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="homeworkScore" label="作业成绩" />
        <el-table-column prop="practiceScore" label="练习成绩" />
        <el-table-column prop="examScore" label="考试成绩" />
        <el-table-column prop="totalScore" label="总评成绩" />
        <el-table-column prop="rank" label="排名" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

// 筛选表单
const filterForm = ref({
  courseId: '1'
})

// 模拟课程数据
const courses = ref([
  { id: '1', title: '初级英语口语' },
  { id: '2', title: '商务英语进阶' },
  { id: '3', title: '英语听力训练' }
])

// 模拟统计数据
const stats = reactive({
  averageScore: 85.5,
  passRate: 95,
  highScore: 98,
  lowScore: 65
})

// 模拟学生成绩数据
const studentGrades = ref([
  {
    studentName: '张三',
    homeworkScore: 90,
    practiceScore: 85,
    examScore: 95,
    totalScore: 92,
    rank: 1
  },
  {
    studentName: '李四',
    homeworkScore: 85,
    practiceScore: 80,
    examScore: 90,
    totalScore: 87,
    rank: 2
  },
  {
    studentName: '王五',
    homeworkScore: 75,
    practiceScore: 70,
    examScore: 80,
    totalScore: 75,
    rank: 3
  },
  {
    studentName: '赵六',
    homeworkScore: 95,
    practiceScore: 90,
    examScore: 85,
    totalScore: 90,
    rank: 4
  },
  {
    studentName: '孙七',
    homeworkScore: 80,
    practiceScore: 75,
    examScore: 85,
    totalScore: 80,
    rank: 5
  }
])

// 查看统计
const handleSearch = () => {
  // 实际项目中这里会根据选择的课程获取统计数据
  console.log('查看课程', filterForm.value.courseId, '的统计数据')
}
</script>

<style scoped>
.grade-statistics {
  padding: 20px 0;
}

.grade-statistics h2 {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-card {
  margin-bottom: 20px;
}

.overview-stats {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  width: calc(25% - 20px);
  min-width: 150px;
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
}

.distribution-card {
  margin-bottom: 20px;
}

.distribution-chart {
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.chart-placeholder {
  text-align: center;
  color: #606266;
}

.grades-card {
  margin-top: 20px;
}

@media screen and (max-width: 768px) {
  .filter-form {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .stat-item {
    width: calc(50% - 20px);
  }
}
</style>