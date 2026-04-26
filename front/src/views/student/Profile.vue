<template>
  <div class="profile">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <div class="welcome-message">
        <h2>你好，学习者 🌟</h2>
        <p>今天是你坚持学习的第 7 天，加油！</p>
      </div>
      <div class="header-actions">
        <el-badge value="5" type="danger" class="notification-badge">
          <el-button type="text">
            <el-icon><Bell /></el-icon>
          </el-button>
        </el-badge>
      </div>
    </div>
    
    <!-- 顶部统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card blue">
        <div class="stat-icon">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-title">今日任务进度</div>
          <div class="stat-value">3 / 5</div>
          <div class="stat-progress">
            <el-progress :percentage="60" :format="() => ''" />
          </div>
        </div>
      </div>
      <div class="stat-card white">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-title">今日学习时长</div>
          <div class="stat-value">4.5 h</div>
          <div class="stat-subtitle">本周</div>
        </div>
      </div>
      <div class="stat-card green">
        <div class="stat-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-title">连续学习天数</div>
          <div class="stat-value">7 天</div>
          <div class="stat-subtitle">持续进步中 🔥</div>
        </div>
      </div>
      <div class="stat-card yellow">
        <div class="stat-icon">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-title">累计学习时长</div>
          <div class="stat-value">120 h</div>
          <div class="stat-subtitle">总计</div>
        </div>
      </div>
      <div class="quick-entry-card">
        <div class="quick-entry">
          <h3 class="quick-entry-title">快捷学习入口</h3>
          <div class="quick-entry-item">
            <div class="quick-entry-icon word-icon">
              <el-icon><Notebook /></el-icon>
            </div>
            <div class="quick-entry-text">
              <div class="quick-entry-title">单词本</div>
              <div class="quick-entry-desc">掌握率 75%</div>
            </div>
            <el-button type="text" class="quick-entry-btn">去学习</el-button>
          </div>
          <div class="quick-entry-item">
            <div class="quick-entry-icon error-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="quick-entry-text">
              <div class="quick-entry-title">错题本</div>
              <div class="quick-entry-desc">未复习 12 题</div>
            </div>
            <el-button type="text" class="quick-entry-btn">去复习</el-button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 主要内容区 -->
    <div class="main-content-grid">
      <!-- 左侧：近期模考成绩 -->
      <div class="card-item">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>近期模考成绩</span>
              <el-button type="text" size="small" class="view-more-btn">查看详细报告</el-button>
            </div>
          </template>
          <div class="chart-container">
            <div class="chart" ref="studyChartRef" style="height: 250px;"></div>
          </div>
        </el-card>
      </div>
      
      <!-- 中间：学习概览 -->
      <div class="card-item">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>学习概览</span>
            </div>
          </template>
          <div class="learning-overview">
            <!-- 本月学习日历 -->
            <div class="calendar-section">
              <div class="calendar-header">
                <span>本月学习日历</span>
                <div class="calendar-nav">
                  <el-button type="text" size="small"><el-icon><ArrowLeft /></el-icon></el-button>
                  <span>2024年5月</span>
                  <el-button type="text" size="small"><el-icon><ArrowRight /></el-icon></el-button>
                </div>
              </div>
              <div class="calendar-weekdays">
                <div class="weekday">一</div>
                <div class="weekday">二</div>
                <div class="weekday">三</div>
                <div class="weekday">四</div>
                <div class="weekday">五</div>
                <div class="weekday">六</div>
                <div class="weekday">日</div>
              </div>
              <div class="calendar-days">
                <div class="calendar-day">1</div>
                <div class="calendar-day">2</div>
                <div class="calendar-day">3</div>
                <div class="calendar-day">4</div>
                <div class="calendar-day">5</div>
                <div class="calendar-day">6</div>
                <div class="calendar-day">7</div>
                <div class="calendar-day">8</div>
                <div class="calendar-day">9</div>
                <div class="calendar-day">10</div>
                <div class="calendar-day">11</div>
                <div class="calendar-day">12</div>
                <div class="calendar-day">13</div>
                <div class="calendar-day">14</div>
                <div class="calendar-day">15</div>
                <div class="calendar-day">16</div>
                <div class="calendar-day">17</div>
                <div class="calendar-day">18</div>
                <div class="calendar-day">19</div>
                <div class="calendar-day">20</div>
                <div class="calendar-day">21</div>
                <div class="calendar-day">22</div>
                <div class="calendar-day">23</div>
                <div class="calendar-day active">24</div>
                <div class="calendar-day active">25</div>
                <div class="calendar-day active">26</div>
                <div class="calendar-day active">27</div>
                <div class="calendar-day active">28</div>
                <div class="calendar-day active">29</div>
                <div class="calendar-day active">30</div>
                <div class="calendar-day">31</div>
              </div>
            </div>
            
            <!-- 总学习时长 -->
            <div class="total-time">
              <div class="time-label">总学习时长（本月）</div>
              <div class="time-value">18 h</div>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 右侧：能力雷达图 -->
      <div class="card-item">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>能力雷达图</span>
            </div>
          </template>
          <div class="radar-chart">
            <div class="radar" ref="radarChartRef" style="height: 200px;"></div>
            <div class="radar-legend">
              <div class="legend-item">
                <div class="legend-color current"></div>
                <span>当前水平</span>
              </div>
              <div class="legend-item">
                <div class="legend-color target"></div>
                <span>目标水平</span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 左侧：学习记录 -->
      <div class="card-item">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>学习记录</span>
              <el-button type="text" size="small" class="view-more-btn">全部记录</el-button>
            </div>
          </template>
          <div class="learning-records">
            <div class="record-item" v-for="(record, index) in studyRecords" :key="index">
              <div class="record-icon">
                <el-icon v-if="index === 0"><VideoPlay /></el-icon>
                <el-icon v-else-if="index === 1"><Notebook /></el-icon>
                <el-icon v-else><Trophy /></el-icon>
              </div>
              <div class="record-content">
                <div class="record-title">{{ record.title }}</div>
                <div class="record-desc">{{ record.description }}</div>
              </div>
              <div class="record-time">{{ record.time }}</div>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 中间：消息中心 -->
      <div class="card-item">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>消息中心</span>
              <el-button type="text" size="small" class="view-more-btn">全部消息</el-button>
            </div>
          </template>
          <div class="messages">
            <div class="message-item unread" v-for="(message, index) in messages" :key="index" :class="{ 'unread': message.unread }">
              <div class="message-icon">
                <el-badge v-if="message.unread" type="danger" :value="''" class="message-badge" />
                <el-icon v-if="index === 0"><ChatLineSquare /></el-icon>
                <el-icon v-else-if="index === 1"><Document /></el-icon>
                <el-icon v-else><Star /></el-icon>
              </div>
              <div class="message-content">
                <div class="message-title">{{ message.title }}</div>
                <div class="message-desc">{{ message.description }}</div>
              </div>
              <div class="message-time">{{ message.time }}</div>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 右侧：我的资源 -->
      <div class="card-item">
        <el-card class="content-card">
          <template #header>
            <div class="card-header">
              <span>我的资源</span>
            </div>
          </template>
          <div class="resources">
            <div class="resource-item">
              <div class="resource-icon course-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="resource-content">
                <div class="resource-title">我的课程</div>
                <div class="resource-desc">已购买 3 门课程</div>
              </div>
              <el-button type="text" class="resource-btn">查看</el-button>
            </div>
            <div class="resource-item">
              <div class="resource-icon collect-icon">
                <el-icon><Star /></el-icon>
              </div>
              <div class="resource-content">
                <div class="resource-title">新的收藏</div>
                <div class="resource-desc">收藏 15 项内容</div>
              </div>
              <el-button type="text" class="resource-btn">查看</el-button>
            </div>
            <div class="resource-item">
              <div class="resource-icon note-icon">
                <el-icon><Notebook /></el-icon>
              </div>
              <div class="resource-content">
                <div class="resource-title">我的笔记</div>
                <div class="resource-desc">记录 20 条笔记</div>
              </div>
              <el-button type="text" class="resource-btn">查看</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>
    
    <!-- 安全设置对话框 -->
    <el-dialog
      v-model="showSecuritySettings"
      title="安全设置"
      width="500px"
    >
      <div class="security-settings">
        <div class="security-item">
          <div class="security-label">修改密码</div>
          <el-button type="primary" size="small">修改</el-button>
        </div>
        <div class="security-item">
          <div class="security-label">绑定手机</div>
          <el-button type="primary" size="small">绑定</el-button>
        </div>
        <div class="security-item">
          <div class="security-label">第三方登录</div>
          <div class="third-party-bindings">
            <el-button type="text" class="third-party-btn">
              <el-icon><ChatLineSquare /></el-icon> 微信
            </el-button>
            <el-button type="text" class="third-party-btn">
              <el-icon><Monitor /></el-icon> QQ
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api'
import { useUserStore } from '../../stores/user'
import * as echarts from 'echarts'
import { 
  Setting, 
  Notebook, 
  Warning, 
  VideoCamera, 
  Star, 
  Edit, 
  ChatLineSquare, 
  Monitor, 
  Check, 
  Clock, 
  TrendCharts, 
  Timer, 
  VideoPlay, 
  Trophy, 
  Document,
  Bell,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const editMode = ref(false)
const fileInput = ref<HTMLInputElement>()
const uploading = ref(false)
const showSecuritySettings = ref(false)

const API_BASE_URL = 'http://localhost:8080'

const userInfo = ref({
  name: '',
  email: '',
  gender: '',
  age: 0,
  goal: '',
  avatar: '',
  level: 'B2',
  examType: 'ielts',
  targetScore: '6.5',
  targetDate: ''
})

const fullAvatarUrl = computed(() => {
  if (!userInfo.value.avatar) {
    return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=student%20avatar&image_size=square'
  }
  if (userInfo.value.avatar.startsWith('http')) {
    return userInfo.value.avatar
  }
  return `${API_BASE_URL}${userInfo.value.avatar}`
})

// 最近学习课程
const recentCourses = ref([
  { title: '雅思听力真题Section 3', progress: 75 },
  { title: '雅思写作提升课程', progress: 45 },
  { title: '单词复习任务', progress: 100 }
])

// 学习记录
const studyRecords = ref([
  { title: '学习了雅思听力真题Section 3', description: '学习了45分钟', time: '昨天' },
  { title: '完成了单词复习任务', description: '复习了50个单词', time: '2天前' },
  { title: '参加了模拟考试', description: '得分：6.0', time: '3天前' }
])

// 徽章
const badges = ref([
  { icon: '🏆', name: '连续打卡' },
  { icon: '⭐', name: '模考达人' },
  { icon: '🔥', name: '学习之星' }
])

// 我的资源
const resources = ref([
  { icon: '📝', title: '我的笔记', count: '20条' },
  { icon: '❌', title: '错题本', count: '12题' },
  { icon: '⭐', title: '收藏夹', count: '15项' },
  { icon: '📥', title: '下载资料', count: '8份' }
])

// 消息中心
const messages = ref([
  { title: '口语作业已批改', description: '你的口语作业已批改，请查收', time: '1小时前', unread: true },
  { title: '新课程上线', description: '雅思写作提升课程已上线', time: '昨天', unread: false },
  { title: '学习提醒', description: '距离考试还有30天，加油！', time: '2天前', unread: false }
])

// 学习数据图表数据
const studyChartData = ref({
  months: ['1月', '2月', '3月', '4月', '5月', '6月'],
  hours: [4.5, 5.2, 6.8, 7.5, 8.2, 7.8],
  accuracy: [75, 78, 82, 85, 88, 90]
})

// 能力雷达图数据
const indicator = [
  { name: '听力', max: 9 },
  { name: '口语', max: 9 },
  { name: '阅读', max: 9 },
  { name: '写作', max: 9 }
]

const series = [
  {
    name: '当前分数',
    data: [6.0, 5.5, 6.5, 6.0]
  },
  {
    name: '目标分数',
    data: [7.0, 7.0, 7.0, 7.0]
  }
]

onMounted(() => {
  if (userStore.userInfo) {
    userInfo.value = {
      name: userStore.userInfo.name || '',
      email: userStore.userInfo.email || '',
      gender: '',
      age: 0,
      goal: '',
      avatar: userStore.userInfo.avatar || '',
      level: 'B2',
      examType: 'ielts',
      targetScore: '6.5',
      targetDate: ''
    }
  }
  
  nextTick(() => {
    initStudyChart()
    initRadarChart()
    window.addEventListener('resize', handleResize)
  })
})

const saveInfo = () => {
  ElMessage.success('保存成功')
  editMode.value = false
}

const changeAvatar = () => {
  fileInput.value?.click()
}

const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files || files.length === 0) {
    return
  }

  const file = files[0]
  
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }

  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  try {
    uploading.value = true
    
    const result = await api.upload.avatar(file)
    
    if (result.success) {
      // 直接更新 userInfo.value.avatar
      userInfo.value.avatar = result.avatarUrl
      
      // 同时更新 userStore
      if (userStore.userInfo) {
        userStore.userInfo.avatar = result.avatarUrl
        localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
      }
      
      // 强制重新计算 fullAvatarUrl
      nextTick(() => {
        // 触发计算属性重新计算
        console.log('头像已更新:', userInfo.value.avatar)
      })
      
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(result.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败，请重试')
  } finally {
    uploading.value = false
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

// 图表引用
const radarChartRef = ref<HTMLElement>()
const studyChartRef = ref<HTMLElement>()
let radarChart: echarts.ECharts | null = null
let studyChart: echarts.ECharts | null = null

// 初始化近期模考成绩图表
const initStudyChart = () => {
  if (!studyChartRef.value) return
  
  studyChart = echarts.init(studyChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月']
    },
    yAxis: {
      type: 'value',
      min: 5.0,
      max: 7.0,
      interval: 0.5,
      axisLabel: {
        formatter: '{value}'
      }
    },
    series: [
      {
        name: '雅思总分',
        type: 'line',
        data: [5.5, 5.5, 5.8, 6.0, 6.0],
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.2)'
        }
      }
    ]
  }
  
  studyChart.setOption(option)
}

// 初始化能力雷达图
const initRadarChart = () => {
  if (!radarChartRef.value) return
  
  radarChart = echarts.init(radarChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item'
    },
    radar: {
      indicator: indicator
    },
    series: [
      {
        name: '能力分数',
        type: 'radar',
        data: [
          {
            value: series[0].data,
            name: '当前分数',
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: 'rgba(64, 158, 255, 0.2)'
            }
          },
          {
            value: series[1].data,
            name: '目标分数',
            itemStyle: {
              color: '#67C23A'
            },
            areaStyle: {
              color: 'rgba(103, 194, 58, 0.2)'
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
  studyChart?.resize()
}

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  radarChart?.dispose()
  studyChart?.dispose()
})

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.profile {
  padding: 20px 0;
}

/* 欢迎信息 */
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.welcome-message h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.welcome-message p {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.notification-badge {
  font-size: 12px;
  line-height: 1;
  min-width: 16px;
  height: 16px;
  padding: 0 6px;
  border-radius: 8px;
  text-align: center;
  background-color: #ff4d4f;
  color: #fff;
  box-shadow: 0 0 0 1px #fff inset;
}

/* 顶部统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  margin-bottom: 30px;
  align-items: stretch;
}

.stat-card {
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
  transition: all 0.3s ease;
  background-color: white;
  min-height: 120px;
  justify-content: space-between;
}

.stat-card.blue {
  background-color: #e6f7ff;
  color: #1890ff;
}

.stat-card.white {
  background-color: #f8f9fa;
  color: #333333;
  border: 1px solid #e6e6e6;
}

.stat-card.green {
  background-color: #f6ffed;
  color: #52c41a;
}

.stat-card.yellow {
  background-color: #fffbe6;
  color: #faad14;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.quick-entry-card {
  grid-column: span 1;
}

.quick-entry {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 16px;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  justify-content: space-between;
}

.quick-entry-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.quick-entry-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.quick-entry-item:hover {
  background-color: #f0f0f0;
}

.quick-entry-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.quick-entry-text {
  flex: 1;
}

.quick-entry-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.quick-entry-desc {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.quick-entry-btn {
  color: #409EFF;
  font-size: 12px;
  padding: 0 8px;
}

.stat-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.8);
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
}

.stat-subtitle {
  font-size: 12px;
  color: #999;
}

.stat-progress {
  margin-top: 8px;
}

/* 主要内容区 */
.main-content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-template-rows: auto auto;
  gap: 20px;
  margin-bottom: 30px;
  align-items: stretch;
}

.card-item {
  display: flex;
  align-items: stretch;
}

.card-item .content-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.radar-chart {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.radar {
  flex: 1;
  min-height: 200px;
}

.content-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e6e6e6;
  background-color: white;
  min-height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e6e6e6;
}

.card-header span {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.view-more-btn {
  font-size: 12px;
  color: #409EFF;
}

/* 左侧内容 */
.left-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 图表容器 */
.chart-container {
  padding: 20px;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 学习记录 */
.learning-records {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.record-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.record-item:hover {
  background-color: #f0f0f0;
}

.record-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #e6f7ff;
  color: #409EFF;
  font-size: 16px;
  flex-shrink: 0;
}

.record-content {
  flex: 1;
}

.record-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.record-desc {
  font-size: 12px;
  color: #666;
}

.record-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  flex-shrink: 0;
}

/* 中间内容 */
.middle-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 学习概览 */
.learning-overview {
  padding: 20px;
}

.calendar-section {
  margin-bottom: 20px;
}

.calendar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.calendar-header span {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.calendar-nav {
  display: flex;
  align-items: center;
  gap: 10px;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
  margin-bottom: 10px;
}

.weekday {
  text-align: center;
  font-size: 12px;
  color: #999;
  padding: 5px;
}

.calendar-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
}

.calendar-day {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #999;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.calendar-day.active {
  background-color: #1890ff;
  color: white;
}

.total-time {
  text-align: center;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.time-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.time-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

/* 消息中心 */
.messages {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.message-item:hover {
  background-color: #f0f0f0;
}

.message-item.unread {
  background-color: #e6f7ff;
  border-left: 4px solid #409EFF;
}

.message-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-badge {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.message-content {
  flex: 1;
}

.message-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.message-desc {
  font-size: 12px;
  color: #666;
}

.message-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  flex-shrink: 0;
}

/* 右侧内容 */
.right-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 快捷学习入口 */
.quick-actions {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-item:hover {
  background-color: #f0f0f0;
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.word-icon {
  background-color: #e6f7ff;
  color: #409EFF;
}

.error-icon {
  background-color: #fff2f0;
  color: #ff4d4f;
}

.action-content {
  flex: 1;
}

.action-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.action-desc {
  font-size: 12px;
  color: #666;
}

.action-btn {
  color: #409EFF;
  font-size: 12px;
}

/* 能力雷达图 */
.radar-chart {
  padding: 10px;
  position: relative;
}

.radar {
  width: 100%;
  height: 100%;
}

.radar-legend {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e6e6e6;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #666;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-color.current {
  background-color: #1890ff;
}

.legend-color.target {
  background-color: #52c41a;
}

/* 我的资源 */
.resources {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.resource-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.resource-item:hover {
  background-color: #f0f0f0;
}

.resource-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.course-icon {
  background-color: #f6ffed;
  color: #52c41a;
}

.collect-icon {
  background-color: #fff7e6;
  color: #faad14;
}

.note-icon {
  background-color: #f9f0ff;
  color: #722ed1;
}

.resource-content {
  flex: 1;
}

.resource-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.resource-desc {
  font-size: 12px;
  color: #666;
}

.resource-btn {
  color: #409EFF;
  font-size: 12px;
}

/* 安全设置 */
.security-settings {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.security-label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.third-party-bindings {
  display: flex;
  gap: 15px;
}

.third-party-btn {
  color: #409EFF;
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .main-content-grid {
    grid-template-columns: 1fr;
  }
  
  .welcome-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-actions {
    align-self: flex-end;
  }
}

@media screen and (max-width: 768px) {
  .stats-cards {
    grid-template-columns: 1fr;
  }
  
  .welcome-section {
    padding: 0 10px;
  }
  
  .record-item {
    flex-direction: column;
    gap: 10px;
  }
  
  .record-time {
    align-self: flex-start;
  }
  
  .message-item {
    flex-direction: column;
    gap: 10px;
  }
  
  .message-time {
    align-self: flex-start;
  }
  
  .action-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .action-btn {
    align-self: flex-end;
  }
  
  .resource-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .resource-btn {
    align-self: flex-end;
  }
  
  .security-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .third-party-bindings {
    align-self: flex-end;
  }
}
</style>
