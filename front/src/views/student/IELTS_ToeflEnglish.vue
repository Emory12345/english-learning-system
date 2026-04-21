<template>
  <div class="ielts-toefl-english">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="exam-switcher">
        <el-button 
          :type="activeExam === 'IELTS' ? 'primary' : 'default'"
          :class="['exam-btn', activeExam === 'IELTS' ? 'active' : '']"
          @click="activeExam = 'IELTS'"
        >
          雅思 IELTS
        </el-button>
        <el-button 
          :type="activeExam === 'TOEFL' ? 'primary' : 'default'"
          :class="['exam-btn', activeExam === 'TOEFL' ? 'active' : '']"
          @click="activeExam = 'TOEFL'"
        >
          托福 TOEFL
        </el-button>
      </div>
      <div class="exam-info">
        <div class="target-score">
          <span class="label">目标分：</span>
          <span class="value">{{ activeExam === 'IELTS' ? '7.0' : '100' }}</span>
        </div>
        <div class="exam-date">
          <span class="label">预计考试：</span>
          <span class="value">{{ activeExam === 'IELTS' ? '2024-06-15' : '2024-06-29' }}</span>
        </div>
      </div>
    </div>
    
    <div class="main-container">
      <!-- 左侧功能菜单 -->
      <div class="left-menu">
        <el-menu
          :default-active="activeMenu"
          class="side-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="overview">
            <el-icon><House /></el-icon>
            <span>首页（概览）</span>
          </el-menu-item>
          <el-menu-item index="level-test">
            <el-icon><Check /></el-icon>
            <span>水平测试</span>
          </el-menu-item>
          <el-sub-menu index="training">
            <template #title>
              <el-icon><Search /></el-icon>
              <span>分项训练</span>
            </template>
            <el-menu-item index="listening">
              <el-icon><Search /></el-icon>
              <span>听力</span>
            </el-menu-item>
            <el-menu-item index="speaking">
              <el-icon><Search /></el-icon>
              <span>口语</span>
            </el-menu-item>
            <el-menu-item index="reading">
              <el-icon><Search /></el-icon>
              <span>阅读</span>
            </el-menu-item>
            <el-menu-item index="writing">
              <el-icon><Edit /></el-icon>
              <span>写作</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="mock-exam">
            <el-icon><Document /></el-icon>
            <span>真题模考</span>
          </el-menu-item>
          <el-menu-item index="materials">
            <el-icon><Document /></el-icon>
            <span>备考资料</span>
          </el-menu-item>
          <el-menu-item index="wrong-answers">
            <el-icon><Warning /></el-icon>
            <span>我的错题</span>
          </el-menu-item>
        </el-menu>
      </div>
      
      <!-- 右侧内容区 -->
      <div class="right-content">
        <!-- 备考概览页 -->
        <div v-if="activeMenu === 'overview'" class="overview-page">
          <!-- 模块一：备考进度驾驶舱 -->
          <div class="dashboard-section">
            <h2>备考进度驾驶舱</h2>
            <div class="dashboard-grid">
              <!-- 学习日历/热力图 -->
              <el-card class="dashboard-card">
                <template #header>
                  <div class="card-header">
                    <span>学习日历</span>
                  </div>
                </template>
                <div class="calendar-heatmap">
                  <div class="calendar-grid">
                    <div v-for="day in 30" :key="day" class="calendar-day" :class="getCalendarDayClass(day)"></div>
                  </div>
                  <div class="calendar-legend">
                    <span>活跃度：</span>
                    <div class="legend-item"><div class="legend-color" style="background-color: #EBEDF0"></div><span>无</span></div>
                    <div class="legend-item"><div class="legend-color" style="background-color: #C6E48B"></div><span>低</span></div>
                    <div class="legend-item"><div class="legend-color" style="background-color: #7BC96F"></div><span>中</span></div>
                    <div class="legend-item"><div class="legend-color" style="background-color: #239A3B"></div><span>高</span></div>
                    <div class="legend-item"><div class="legend-color" style="background-color: #196127"></div><span>很高</span></div>
                  </div>
                </div>
              </el-card>
              
              <!-- 能力雷达图 -->
              <el-card class="dashboard-card">
                <template #header>
                  <div class="card-header">
                    <span>能力雷达图</span>
                  </div>
                </template>
                <div class="radar-chart" ref="radarChartRef" style="height: 250px;"></div>
              </el-card>
              
              <!-- 智能推荐 -->
              <el-card class="dashboard-card recommendation-card">
                <template #header>
                  <div class="card-header">
                    <span>智能推荐</span>
                  </div>
                </template>
                <div class="recommendation-content">
                  <div class="recommendation-item">
                    <el-icon class="recommendation-icon"><Search /></el-icon>
                    <div class="recommendation-text">
                      <h4>{{ activeExam === 'IELTS' ? '阅读判断题正确率低' : '听力细节题正确率低' }}</h4>
                      <p>{{ activeExam === 'IELTS' ? '推荐练习《剑桥17-Test 2》' : '推荐练习《TPO 54》' }}</p>
                    </div>
                    <el-button type="primary" size="small">开始练习</el-button>
                  </div>
                  <div class="recommendation-item">
                    <el-icon class="recommendation-icon"><Timer /></el-icon>
                    <div class="recommendation-text">
                      <h4>今日学习计划</h4>
                      <p>建议学习时间：60分钟</p>
                    </div>
                    <el-button type="success" size="small">开始学习</el-button>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
          
          <!-- 模块二：分项技能训练 -->
          <div class="training-section">
            <h2>分项技能训练</h2>
            <div class="training-grid">
              <!-- 听力 -->
              <el-card class="training-card" :class="activeExam === 'IELTS' ? 'ielts-card' : 'toefl-card'">
                <div class="training-icon listening-icon">
                  <el-icon><Search /></el-icon>
                </div>
                <h3>听力</h3>
                <div class="training-options">
                  <el-button type="text" class="training-option">{{ activeExam === 'IELTS' ? '精听训练' : '盲听训练' }}</el-button>
                  <el-button type="text" class="training-option">真题练习</el-button>
                  <el-button type="text" class="training-option">场景词汇</el-button>
                </div>
              </el-card>
              
              <!-- 阅读 -->
              <el-card class="training-card" :class="activeExam === 'IELTS' ? 'ielts-card' : 'toefl-card'">
                <div class="training-icon reading-icon">
                  <el-icon><Search /></el-icon>
                </div>
                <h3>阅读</h3>
                <div class="training-options">
                  <el-button type="text" class="training-option">题型突破</el-button>
                  <el-button type="text" class="training-option">{{ activeExam === 'IELTS' ? '长难句分析' : '段落折叠' }}</el-button>
                  <el-button type="text" class="training-option">{{ activeExam === 'IELTS' ? '高亮划线' : '关键词搜索' }}</el-button>
                </div>
              </el-card>
              
              <!-- 写作 -->
              <el-card class="training-card" :class="activeExam === 'IELTS' ? 'ielts-card' : 'toefl-card'">
                <div class="training-icon writing-icon">
                  <el-icon><Edit /></el-icon>
                </div>
                <h3>写作</h3>
                <div class="training-options">
                  <el-button type="text" class="training-option">{{ activeExam === 'IELTS' ? 'Task 1图表' : '综合写作' }}</el-button>
                  <el-button type="text" class="training-option">{{ activeExam === 'IELTS' ? 'Task 2范文' : '独立写作' }}</el-button>
                </div>
              </el-card>
              
              <!-- 口语 -->
              <el-card class="training-card" :class="activeExam === 'IELTS' ? 'ielts-card' : 'toefl-card'">
                <div class="training-icon speaking-icon">
                  <el-icon><Search /></el-icon>
                </div>
                <h3>口语</h3>
                <div class="training-options">
                  <el-button type="text" class="training-option">{{ activeExam === 'IELTS' ? 'Part 1题库' : '独立口语' }}</el-button>
                  <el-button type="text" class="training-option">发音纠正</el-button>
                </div>
              </el-card>
            </div>
          </div>
          
          <!-- 模块三：真题与模考 -->
          <div class="exam-section">
            <h2>真题与模考</h2>
            <div class="exam-grid">
              <!-- 热门真题推荐 -->
              <el-card class="exam-card">
                <template #header>
                  <div class="card-header">
                    <span>热门真题推荐</span>
                  </div>
                </template>
                <div class="exam-list">
                    <div v-for="(exam, index) in 热门真题" :key="index" class="exam-item">
                    <div class="exam-info">
                      <h4>{{ exam.title }}</h4>
                      <div class="exam-meta">
                        <el-tag size="small" type="info" class="exam-tag">{{ exam.type }}</el-tag>
                        <span class="exam-hot">热度: {{ exam.hot }}</span>
                      </div>
                    </div>
                    <el-button type="primary" size="small">开始练习</el-button>
                  </div>
                </div>
              </el-card>
              
              <!-- 全真模考入口 -->
              <el-card class="mock-exam-card" :class="activeExam === 'IELTS' ? 'ielts-card' : 'toefl-card'">
                <div class="mock-exam-content">
                  <h3>全真模考</h3>
                  <p>{{ activeExam === 'IELTS' ? '限时3小时，还原考场环境' : '限时4小时，还原考场环境' }}</p>
                  <el-button type="primary" size="large" class="mock-exam-btn">开始模考</el-button>
                  <div class="mock-exam-tips">
                    <el-icon><Warning /></el-icon>
                    <span>建议在安静环境中进行，确保网络稳定</span>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
        </div>
        
        <!-- 其他页面内容 -->
        <div v-else class="other-page">
          <h2>{{ getPageTitle(activeMenu) }}</h2>
          <el-empty description="页面开发中，敬请期待" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue'
import * as echarts from 'echarts'
import { 
  House, 
  Check, 
  Search, 
  Edit, 
  Document, 
  Warning, 
  Timer 
} from '@element-plus/icons-vue'

// 激活的考试类型
const activeExam = ref('IELTS')

// 激活的菜单
const activeMenu = ref('overview')

// 图表引用
const radarChartRef = ref<HTMLElement>()
let radarChart: echarts.ECharts | null = null

// 热门真题数据
const 热门真题 = [
  {
    title: activeExam.value === 'IELTS' ? '剑桥雅思 17 Test 1' : 'TPO 54',
    type: '听力',
    hot: 98
  },
  {
    title: activeExam.value === 'IELTS' ? '剑桥雅思 16 Test 3' : 'TPO 53',
    type: '阅读',
    hot: 95
  },
  {
    title: activeExam.value === 'IELTS' ? '剑桥雅思 15 Test 2' : 'TPO 52',
    type: '写作',
    hot: 92
  }
]

// 处理菜单选择
const handleMenuSelect = (index: string) => {
  activeMenu.value = index
}

// 获取页面标题
const getPageTitle = (menuIndex: string) => {
  const titles: Record<string, string> = {
    'overview': '备考概览',
    'level-test': '水平测试',
    'listening': '听力训练',
    'speaking': '口语训练',
    'reading': '阅读训练',
    'writing': '写作训练',
    'mock-exam': '真题模考',
    'materials': '备考资料',
    'wrong-answers': '我的错题'
  }
  return titles[menuIndex] || '页面标题'
}

// 获取日历天的类名
const getCalendarDayClass = (day: number) => {
  const activity = Math.floor(Math.random() * 5)
  return `activity-${activity}`
}

// 初始化能力雷达图
const initRadarChart = () => {
  if (!radarChartRef.value) return
  
  radarChart = echarts.init(radarChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      data: ['当前分数', '目标分数'],
      bottom: 10
    },
    radar: {
      indicator: [
        { name: '听力', max: activeExam.value === 'IELTS' ? 9 : 30 },
        { name: '口语', max: activeExam.value === 'IELTS' ? 9 : 30 },
        { name: '阅读', max: activeExam.value === 'IELTS' ? 9 : 30 },
        { name: '写作', max: activeExam.value === 'IELTS' ? 9 : 30 }
      ]
    },
    series: [
      {
        name: '能力分数',
        type: 'radar',
        data: [
          {
            value: activeExam.value === 'IELTS' ? [6.0, 5.5, 6.5, 6.0] : [22, 20, 24, 22],
            name: '当前分数',
            itemStyle: {
              color: activeExam.value === 'IELTS' ? '#409EFF' : '#E6A23C'
            },
            areaStyle: {
              color: activeExam.value === 'IELTS' ? 'rgba(64, 158, 255, 0.2)' : 'rgba(230, 162, 60, 0.2)'
            }
          },
          {
            value: activeExam.value === 'IELTS' ? [7.0, 7.0, 7.0, 7.0] : [26, 26, 26, 26],
            name: '目标分数',
            itemStyle: {
              color: activeExam.value === 'IELTS' ? '#67C23A' : '#F56C6C'
            },
            areaStyle: {
              color: activeExam.value === 'IELTS' ? 'rgba(103, 194, 58, 0.2)' : 'rgba(245, 108, 108, 0.2)'
            }
          }
        ]
      }
    ]
  }
  
  radarChart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  radarChart?.resize()
}

// 监听考试类型变化
const updateRadarChart = () => {
  nextTick(() => {
    if (radarChart) {
      radarChart.dispose()
    }
    initRadarChart()
  })
}

// 监听activeExam变化
import { watch } from 'vue'
watch(activeExam, updateRadarChart)

onMounted(() => {
  nextTick(() => {
    initRadarChart()
    window.addEventListener('resize', handleResize)
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  radarChart?.dispose()
})
</script>

<style scoped>
.ielts-toefl-english {
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* 顶部导航栏 */
.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.exam-switcher {
  display: flex;
  gap: 12px;
}

.exam-btn {
  border-radius: 20px;
  padding: 8px 20px;
  font-weight: 500;
}

.exam-btn.active {
  color: white;
}

.exam-info {
  display: flex;
  gap: 24px;
  font-size: 14px;
}

.exam-info .label {
  color: #606266;
}

.exam-info .value {
  color: #303133;
  font-weight: 500;
}

/* 主容器 */
.main-container {
  display: flex;
  padding: 0 24px 24px;
  gap: 24px;
}

/* 左侧菜单 */
.left-menu {
  width: 200px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.side-menu {
  border-right: none;
}

.side-menu .el-menu-item {
  height: 56px;
  line-height: 56px;
  margin: 0 12px;
  border-radius: 4px;
}

.side-menu .el-sub-menu__title {
  height: 56px;
  line-height: 56px;
  margin: 0 12px;
  border-radius: 4px;
}

/* 右侧内容区 */
.right-content {
  flex: 1;
  min-width: 0;
}

/* 概览页 */
.overview-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 备考进度驾驶舱 */
.dashboard-section h2,
.training-section h2,
.exam-section h2 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 16px;
  color: #303133;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.dashboard-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-weight: 500;
  color: #303133;
}

/* 学习日历 */
.calendar-heatmap {
  padding: 16px 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  margin-bottom: 16px;
}

.calendar-day {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  background-color: #EBEDF0;
}

.calendar-day.activity-1 {
  background-color: #C6E48B;
}

.calendar-day.activity-2 {
  background-color: #7BC96F;
}

.calendar-day.activity-3 {
  background-color: #239A3B;
}

.calendar-day.activity-4 {
  background-color: #196127;
}

.calendar-legend {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #606266;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

/* 智能推荐 */
.recommendation-card {
  grid-column: 1 / -1;
}

.recommendation-content {
  display: flex;
  gap: 24px;
  padding: 8px 0;
}

.recommendation-item {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.recommendation-icon {
  font-size: 24px;
  color: #409EFF;
}

.recommendation-text h4 {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #303133;
}

.recommendation-text p {
  font-size: 12px;
  color: #606266;
  margin: 0;
}

/* 分项技能训练 */
.training-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.training-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 24px;
  text-align: center;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  min-height: 280px;
}

.training-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.training-card.ielts-card {
  border-top: 3px solid #409EFF;
}

.training-card.toefl-card {
  border-top: 3px solid #F56C6C;
}

.training-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 24px;
}

.training-icon.listening-icon {
  background-color: #ECF5FF;
  color: #409EFF;
}

.training-icon.reading-icon {
  background-color: #F0F9EB;
  color: #67C23A;
}

.training-icon.writing-icon {
  background-color: #FEF3E2;
  color: #E6A23C;
}

.training-icon.speaking-icon {
  background-color: #FDF0F6;
  color: #F56C6C;
}

.training-card h3 {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 16px;
  color: #303133;
}

.training-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex-grow: 1;
  justify-content: space-around;
}

.training-option {
  font-size: 14px;
  color: #606266;
  padding: 4px 0;
}

/* 真题与模考 */
.exam-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 16px;
}

.exam-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.exam-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px 0;
}

.exam-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #EBEEF5;
}

.exam-item:last-child {
  border-bottom: none;
}

.exam-info h4 {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #303133;
}

.exam-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.exam-tag {
  font-size: 10px;
}

.exam-hot {
  color: #606266;
}

.mock-exam-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
}

.mock-exam-card.ielts-card {
  border-left: 4px solid #409EFF;
}

.mock-exam-card.toefl-card {
  border-left: 4px solid #F56C6C;
}

.mock-exam-content {
  text-align: center;
  max-width: 200px;
}

.mock-exam-content h3 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #303133;
}

.mock-exam-content p {
  font-size: 14px;
  color: #606266;
  margin-bottom: 24px;
}

.mock-exam-btn {
  width: 100%;
  border-radius: 20px;
  padding: 10px 0;
  margin-bottom: 16px;
}

.mock-exam-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

/* 其他页面 */
.other-page {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 48px;
  text-align: center;
}

.other-page h2 {
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 24px;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }
  
  .left-menu {
    width: 100%;
  }
  
  .dashboard-grid,
  .exam-grid {
    grid-template-columns: 1fr;
  }
  
  .training-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .recommendation-content {
    flex-direction: column;
  }
  
  .exam-info {
    display: none;
  }
}
</style>