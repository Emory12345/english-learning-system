<template>
  <div class="profile">
    <!-- 头部区域 -->
    <div class="profile-header">
      <div class="header-left">
        <h1 class="page-title">个人中心</h1>
      </div>
      <div class="header-right">
        <div class="user-greeting">
          Hi, {{ userInfo.name || '用户' }}！
        </div>
        <div class="header-actions">
          <el-button type="text" class="setting-btn">
            <el-icon><Setting /></el-icon>
          </el-button>
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </div>
    
    <!-- 中部内容区：个人信息与设置 -->
    <div class="middle-content">
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>个人信息</span>
            <el-button type="primary" size="small" @click="editMode = !editMode">
              {{ editMode ? '取消' : '编辑' }}
            </el-button>
          </div>
        </template>
        <div class="profile-info">
          <div class="avatar-section">
            <img :src="fullAvatarUrl" :alt="userInfo.name" class="avatar" />
            <input 
              type="file" 
              ref="fileInput" 
              accept="image/*" 
              style="display: none"
              @change="handleFileChange"
            />
            <el-button size="small" @click="changeAvatar">更换头像</el-button>
          </div>
          <div class="info-form">
            <el-form :model="userInfo" label-width="100px">
              <el-form-item label="姓名">
                <el-input v-model="userInfo.name" :disabled="!editMode" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userInfo.email" :disabled="!editMode" />
              </el-form-item>
              <el-form-item label="性别">
                <el-select v-model="userInfo.gender" :disabled="!editMode" placeholder="请选择性别">
                  <el-option label="男" value="male" />
                  <el-option label="女" value="female" />
                </el-select>
              </el-form-item>
              <el-form-item label="年龄">
                <el-input v-model="userInfo.age" type="number" :disabled="!editMode" />
              </el-form-item>
              <el-form-item label="考试类型">
                <el-radio-group v-model="userInfo.examType" :disabled="!editMode">
                  <el-radio label="ielts">雅思</el-radio>
                  <el-radio label="toefl">托福</el-radio>
                  <el-radio label="cet4">四级</el-radio>
                  <el-radio label="cet6">六级</el-radio>
                  <el-radio label="kaoyan">考研英语</el-radio>
                  <el-radio label="teenage">青少年英语</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="目标分数">
                <el-input v-model="userInfo.targetScore" :disabled="!editMode" />
              </el-form-item>
              <el-form-item label="目标日期">
                <el-date-picker
                  v-model="userInfo.targetDate"
                  type="date"
                  :disabled="!editMode"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
              <el-form-item v-if="editMode">
                <el-button type="primary" @click="saveInfo">保存</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 主体内容区 -->
    <div class="main-content">
      <!-- 左侧列：数据看板 -->
      <div class="left-column">
        <!-- 学习概览卡片 -->
        <el-card class="dashboard-card">
          <template #header>
            <div class="card-header">
              <span>学习概览</span>
            </div>
          </template>
          <div class="learning-overview">
            <div class="overview-item">
              <div class="overview-label">今日任务进度</div>
              <div class="progress-container">
                <el-progress :percentage="60" :format="() => '3/5'" />
              </div>
            </div>
            <div class="overview-item">
              <div class="overview-label">连续学习天数</div>
              <div class="streak-count">7天</div>
              <div class="calendar-preview">
                <div class="calendar-days">
                  <div v-for="day in 30" :key="day" class="calendar-day" :class="{ 'active': day > 23 }">
                    {{ day }}
                  </div>
                </div>
              </div>
            </div>
            <div class="overview-item">
              <div class="overview-label">总学习时长</div>
              <div class="time-stats">
                <div class="time-item">
                  <div class="time-value">4.5h</div>
                  <div class="time-label">本周</div>
                </div>
                <div class="time-item">
                  <div class="time-value">18h</div>
                  <div class="time-label">本月</div>
                </div>
                <div class="time-item">
                  <div class="time-value">120h</div>
                  <div class="time-label">累计</div>
                </div>
              </div>
            </div>
            <div class="overview-item">
              <div class="overview-label">当前等级</div>
              <div class="level-info">
                <div class="level-badge">雅思 6.0 分水平</div>
                <div class="level-desc">Level B2</div>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 能力雷达图 -->
        <el-card class="dashboard-card">
          <div class="radar-chart" ref="radarChartRef" style="height: 300px;"></div>
        </el-card>
        
        <!-- 近期模考成绩 -->
        <el-card class="dashboard-card">
          <div class="exam-chart" ref="examChartRef" style="height: 250px;"></div>
          <div style="text-align: center; margin-top: 20px;">
            <el-button type="primary" size="small" class="view-report-btn">查看详细报告</el-button>
          </div>
        </el-card>
      </div>
      
      <!-- 右侧列：核心功能入口 -->
      <div class="right-column">
        <!-- 快捷学习入口 -->
        <el-card class="action-card">
          <template #header>
            <div class="card-header">
              <span>快捷学习入口</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" class="start-learning-btn">
              开始今日学习
            </el-button>
            <div class="action-items">
              <div class="action-item">
                <div class="action-icon word-icon">
                  <el-icon><Notebook /></el-icon>
                </div>
                <div class="action-content">
                  <div class="action-title">单词本</div>
                  <div class="action-desc">掌握率 75%</div>
                </div>
                <el-button type="text" class="action-btn">去学习</el-button>
              </div>
              <div class="action-item">
                <div class="action-icon error-icon">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="action-content">
                  <div class="action-title">错题本</div>
                  <div class="action-desc">未复习 12 题</div>
                </div>
                <el-button type="text" class="action-btn">去复习</el-button>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 我的资产 -->
        <el-card class="action-card">
          <template #header>
            <div class="card-header">
              <span>我的资产</span>
            </div>
          </template>
          <div class="assets">
            <div class="asset-item">
              <div class="asset-icon course-icon">
                <el-icon><VideoCamera /></el-icon>
              </div>
              <div class="asset-content">
                <div class="asset-title">我的课程</div>
                <div class="asset-desc">已购买 3 门课程</div>
              </div>
              <el-button type="text" class="asset-btn">查看</el-button>
            </div>
            <div class="asset-item">
              <div class="asset-icon collect-icon">
                <el-icon><Star /></el-icon>
              </div>
              <div class="asset-content">
                <div class="asset-title">我的收藏</div>
                <div class="asset-desc">收藏 15 项内容</div>
              </div>
              <el-button type="text" class="asset-btn">查看</el-button>
            </div>
            <div class="asset-item">
              <div class="asset-icon note-icon">
                <el-icon><Edit /></el-icon>
              </div>
              <div class="asset-content">
                <div class="asset-title">我的笔记</div>
                <div class="asset-desc">记录 20 条笔记</div>
              </div>
              <el-button type="text" class="asset-btn">查看</el-button>
            </div>
          </div>
        </el-card>
        
        <!-- 安全设置 -->
        <el-card class="action-card">
          <template #header>
            <div class="card-header">
              <span>安全设置</span>
            </div>
          </template>
          <div class="security-settings">
            <div class="security-item">
              <div class="security-label">修改密码</div>
              <el-button type="text">修改</el-button>
            </div>
            <div class="security-item">
              <div class="security-label">绑定手机</div>
              <el-button type="text">绑定</el-button>
            </div>
            <div class="security-item">
              <div class="security-label">第三方登录</div>
              <div class="third-party">
                <el-button type="text" class="third-party-btn">
                  <el-icon><ChatLineSquare /></el-icon> 微信
                </el-button>
                <el-button type="text" class="third-party-btn">
                  <el-icon><Monitor /></el-icon> QQ
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
    
    <!-- 底部内容区：学习记录与系统消息 -->
    <div class="bottom-content">
      <div class="bottom-left">
        <!-- 学习记录 -->
        <el-card class="record-card">
          <template #header>
            <div class="card-header">
              <span>学习记录</span>
              <el-button type="text" size="small">查看全部</el-button>
            </div>
          </template>
          <div class="learning-records">
            <div class="record-item">
              <div class="record-time">昨天</div>
              <div class="record-content">
                <div class="record-title">学习了雅思听力真题Section 3</div>
                <div class="record-desc">学习了45分钟</div>
              </div>
            </div>
            <div class="record-item">
              <div class="record-time">2天前</div>
              <div class="record-content">
                <div class="record-title">完成了单词复习任务</div>
                <div class="record-desc">复习了50个单词</div>
              </div>
            </div>
            <div class="record-item">
              <div class="record-time">3天前</div>
              <div class="record-content">
                <div class="record-title">参加了模拟考试</div>
                <div class="record-desc">得分：6.0</div>
              </div>
            </div>
          </div>
        </el-card>
      </div>
      <div class="bottom-right">
        <!-- 消息中心 -->
        <el-card class="message-card">
          <template #header>
            <div class="card-header">
              <span>消息中心</span>
              <el-badge value="3" type="danger" />
            </div>
          </template>
          <div class="messages">
            <div class="message-item unread">
              <div class="message-content">
                <div class="message-title">口语作业已批改</div>
                <div class="message-desc">你的口语作业已批改，请查收</div>
              </div>
              <div class="message-time">1小时前</div>
            </div>
            <div class="message-item">
              <div class="message-content">
                <div class="message-title">新课程上线</div>
                <div class="message-desc">雅思写作提升课程已上线</div>
              </div>
              <div class="message-time">昨天</div>
            </div>
            <div class="message-item">
              <div class="message-content">
                <div class="message-title">学习提醒</div>
                <div class="message-desc">距离考试还有30天，加油！</div>
              </div>
              <div class="message-time">2天前</div>
            </div>
          </div>
        </el-card>
        

      </div>
    </div>
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
  Monitor 
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const editMode = ref(false)
const fileInput = ref<HTMLInputElement>()
const uploading = ref(false)

const API_BASE_URL = 'http://localhost:8080'

const userInfo = ref({
  name: '',
  email: '',
  gender: '',
  age: 0,
  goal: '',
  avatar: '',
  examType: 'ielts',
  targetScore: '7.0',
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

// 模考成绩数据
const examData = [5.5, 5.5, 5.8, 6.0, 6.0]

onMounted(() => {
  if (userStore.userInfo) {
    userInfo.value = {
      name: userStore.userInfo.name || '',
      email: userStore.userInfo.email || '',
      gender: '',
      age: 0,
      goal: '',
      avatar: userStore.userInfo.avatar || '',
      examType: 'ielts',
      targetScore: '7.0',
      targetDate: ''
    }
  }
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
      userInfo.value.avatar = result.avatarUrl
      
      if (userStore.userInfo) {
        userStore.userInfo.avatar = result.avatarUrl
        localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
      }
      
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
const examChartRef = ref<HTMLElement>()
let radarChart: echarts.ECharts | null = null
let examChart: echarts.ECharts | null = null

// 初始化能力雷达图
const initRadarChart = () => {
  if (!radarChartRef.value) return
  
  radarChart = echarts.init(radarChartRef.value)
  
  const option = {
    title: {
      text: '能力雷达图',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      data: ['当前分数', '目标分数'],
      bottom: 10
    },
    radar: {
      indicator: [
        { name: '听力', max: 9 },
        { name: '口语', max: 9 },
        { name: '阅读', max: 9 },
        { name: '写作', max: 9 }
      ]
    },
    series: [
      {
        name: '能力分数',
        type: 'radar',
        data: [
          {
            value: [6.0, 5.5, 6.5, 6.0],
            name: '当前分数',
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: 'rgba(64, 158, 255, 0.2)'
            }
          },
          {
            value: [7.0, 7.0, 7.0, 7.0],
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

// 初始化近期模考成绩图表
const initExamChart = () => {
  if (!examChartRef.value) return
  
  examChart = echarts.init(examChartRef.value)
  
  const option = {
    title: {
      text: '近期模考成绩',
      left: 'center'
    },
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
      interval: 0.5
    },
    series: [
      {
        data: [5.5, 5.5, 5.8, 6.0, 6.0],
        type: 'line',
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
  
  examChart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  radarChart?.resize()
  examChart?.resize()
}

onMounted(() => {
  if (userStore.userInfo) {
    userInfo.value = {
      name: userStore.userInfo.name || '',
      email: userStore.userInfo.email || '',
      gender: '',
      age: 0,
      goal: '',
      avatar: userStore.userInfo.avatar || '',
      examType: 'ielts',
      targetScore: '7.0',
      targetDate: ''
    }
  }
  
  nextTick(() => {
    initRadarChart()
    initExamChart()
    window.addEventListener('resize', handleResize)
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  radarChart?.dispose()
  examChart?.dispose()
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

/* 头部区域 */
.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e6e6e6;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.system-logo {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  letter-spacing: 1px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-greeting {
  font-size: 16px;
  color: #666;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.setting-btn {
  color: #666;
}

/* 主体内容区 */
.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  margin-bottom: 30px;
}

/* 左侧列：数据看板 */
.left-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dashboard-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
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

/* 学习概览 */
.learning-overview {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.overview-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.overview-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.progress-container {
  width: 100%;
}

.streak-count {
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
}

.calendar-preview {
  margin-top: 10px;
}

.calendar-days {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.calendar-day {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #999;
  border-radius: 4px;
}

.calendar-day.active {
  background-color: #409EFF;
  color: white;
}

.time-stats {
  display: flex;
  gap: 20px;
}

.time-item {
  flex: 1;
  text-align: center;
}

.time-value {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.time-label {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.level-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.level-badge {
  background-color: #f0f9ff;
  color: #409EFF;
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
  width: fit-content;
}

.level-desc {
  font-size: 14px;
  color: #666;
}

/* 能力雷达图 */
.radar-chart {
  padding: 20px;
  position: relative;
}

.radar-legend {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #666;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.score-gap {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

/* 近期模考成绩 */
.exam-chart {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.view-report-btn {
  align-self: center;
}

/* 右侧列：核心功能入口 */
.right-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.action-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

/* 快捷学习入口 */
.quick-actions {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.start-learning-btn {
  width: 100%;
  padding: 15px;
  font-size: 16px;
  font-weight: 600;
}

.action-items {
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
}

.action-desc {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.action-btn {
  color: #409EFF;
}

/* 我的资产 */
.assets {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.asset-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.asset-item:hover {
  background-color: #f0f0f0;
}

.asset-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
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

.asset-content {
  flex: 1;
}

.asset-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.asset-desc {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.asset-btn {
  color: #409EFF;
}

/* 中部内容区：个人信息与设置 */
.middle-content {
  margin-bottom: 30px;
}

.info-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

.profile-info {
  display: flex;
  gap: 40px;
  padding: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

.info-form {
  flex: 1;
}

/* 底部内容区：学习记录与系统消息 */
.bottom-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.bottom-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.bottom-right {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.record-card,
.message-card,
.security-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
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
  gap: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.record-item:hover {
  background-color: #f0f0f0;
}

.record-time {
  width: 80px;
  font-size: 14px;
  color: #999;
  font-weight: 500;
}

.record-content {
  flex: 1;
}

.record-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.record-desc {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
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
  justify-content: space-between;
  align-items: flex-start;
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

.message-content {
  flex: 1;
  margin-right: 15px;
}

.message-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.message-desc {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.message-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

/* 安全设置 */
.security-settings {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.security-item:hover {
  background-color: #f0f0f0;
}

.security-label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.third-party {
  display: flex;
  gap: 10px;
}

.third-party-btn {
  color: #409EFF;
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .bottom-content {
    grid-template-columns: 1fr;
  }
  
  .profile-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-right {
    align-self: flex-end;
  }
}

@media screen and (max-width: 768px) {
  .profile-info {
    flex-direction: column;
    align-items: center;
  }
  
  .info-form {
    width: 100%;
  }
  
  .time-stats {
    flex-direction: column;
    gap: 10px;
  }
  
  .time-item {
    text-align: left;
  }
  
  .radar-legend {
    flex-direction: column;
    gap: 10px;
  }
  
  .record-item {
    flex-direction: column;
    gap: 10px;
  }
  
  .record-time {
    width: 100%;
  }
  
  .message-item {
    flex-direction: column;
    gap: 10px;
  }
  
  .message-time {
    align-self: flex-start;
  }
}
</style>
