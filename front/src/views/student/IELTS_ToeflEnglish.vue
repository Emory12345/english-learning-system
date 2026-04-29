<template>
  <div class="ielts-english">
    <!-- 雅思部分 -->
    <div class="exam-section ielts-section">
      <!-- 1. 顶部用户信息卡 -->
      <div class="header-card">
        <div class="header-left">
          <span class="header-icon">📚</span>
          <span class="header-title">IELTS 雅思</span>
        </div>
        <div class="header-right">
          <div class="target-info">目标 7.0</div>
          <div class="days-info">48天</div>
        </div>
        <div class="progress-bar">
          <div class="progress-fill" style="width: 65%;"></div>
        </div>
        <div class="progress-text">进度 65%</div>
      </div>

      <!-- 2. 三个科目大卡片 -->
      <div class="subject-cards">
        <div class="subject-card listening-card">
          <div class="subject-icon">👂</div>
          <div class="subject-name">听力</div>
          <div class="subject-score">6.5</div>
          <div class="subject-change up">↗ 0.5</div>
          <div class="subject-target">距目标0.5</div>
        </div>
        <div class="subject-card reading-card">
          <div class="subject-icon">📖</div>
          <div class="subject-name">阅读</div>
          <div class="subject-score">7.0</div>
          <div class="subject-change same">→ 持平</div>
          <div class="subject-target achieved">已达目标</div>
        </div>
        <div class="subject-card writing-card">
          <div class="subject-icon">✍️</div>
          <div class="subject-name">写作</div>
          <div class="subject-score">5.5</div>
          <div class="subject-change up">↗ 1.0</div>
          <div class="subject-target low">距目标1.5</div>
        </div>
      </div>

      <!-- 3. 今日任务 -->
      <div class="tasks-card">
        <div class="tasks-header">
          <span class="tasks-title">📝 今日任务</span>
          <span class="tasks-progress">已完成 {{ completedCount }}/{{ tasks.length }}</span>
        </div>
        
        <!-- 添加任务 -->
        <div class="add-task">
          <input 
            v-model="newTaskName" 
            @keyup.enter="addTask" 
            placeholder="添加新任务..." 
            class="task-input"
          />
          <el-button @click="addTask" class="add-btn">添加</el-button>
        </div>
        
        <!-- 任务列表 -->
        <div class="tasks-list">
          <div 
            v-for="(task, index) in tasks" 
            :key="task.id" 
            class="task-item"
            :class="{ completed: task.completed }"
          >
            <span 
              class="task-checkbox"
              :class="{ checked: task.completed }"
              @click="toggleTask(task.id)"
            >
              {{ task.completed ? '☑' : '☐' }}
            </span>
            <span class="task-name">{{ task.name }}</span>
            <el-button 
              size="small" 
              class="delete-btn" 
              @click.stop="deleteTask(task.id)"
            >
              删除
            </el-button>
          </div>
          <div v-if="tasks.length === 0" class="empty-tasks">
            还没有任务，添加一个吧！
          </div>
        </div>
      </div>

      <!-- 4. 学习日历 -->
      <div class="calendar-card">
        <div class="calendar-row">
          <div class="calendar-day">
            <div class="day-label">三</div>
            <div class="day-status">✅</div>
          </div>
          <div class="calendar-day">
            <div class="day-label">四</div>
            <div class="day-status">✅</div>
          </div>
          <div class="calendar-day">
            <div class="day-label">五</div>
            <div class="day-status">🔥</div>
          </div>
          <div class="calendar-day">
            <div class="day-label">六</div>
            <div class="day-status">📝</div>
          </div>
          <div class="calendar-day">
            <div class="day-label">日</div>
            <div class="day-status">📝</div>
          </div>
          <div class="calendar-day">
            <div class="day-label">一</div>
            <div class="day-status">--</div>
          </div>
          <div class="calendar-day today">
            <div class="day-label">二</div>
            <div class="day-status">🔵</div>
          </div>
        </div>
      </div>

      <!-- 5. 真题模考列表 -->
      <div class="mock-exam-card">
        <div class="exam-header">真题模考</div>
        
        <!-- PDF展示区域 -->
        <div class="pdf-container" v-if="currentPdf">
          <div class="pdf-header">
            <span>{{ currentPdfName }}</span>
            <el-button size="small" @click="closePdf">关闭</el-button>
          </div>
          <iframe :src="currentPdf" class="pdf-iframe"></iframe>
        </div>
        
        <!-- PDF列表 -->
        <div class="pdf-list" v-else>
          <div class="exam-item" @click="openPdf('/雅思/剑桥雅思17（A类）.pdf', '剑桥雅思17（A类）')">
            <div class="exam-info">
              <div class="exam-name">📋 剑桥雅思17（A类）</div>
            </div>
            <el-button class="exam-btn">查看PDF</el-button>
          </div>
          <div class="exam-item" @click="openPdf('/雅思/剑桥雅思真题18.pdf', '剑桥雅思真题18')">
            <div class="exam-info">
              <div class="exam-name">📋 剑桥雅思真题18</div>
            </div>
            <el-button class="exam-btn">查看PDF</el-button>
          </div>
          <div class="exam-item" @click="openPdf('/雅思/剑19（A类）.pdf', '剑19（A类）')">
            <div class="exam-info">
              <div class="exam-name">📋 剑19（A类）</div>
            </div>
            <el-button class="exam-btn">查看PDF</el-button>
          </div>
          <div class="exam-item" @click="openPdf('/雅思/剑19（G类）.pdf', '剑19（G类）')">
            <div class="exam-info">
              <div class="exam-name">📋 剑19（G类）</div>
            </div>
            <el-button class="exam-btn">查看PDF</el-button>
          </div>
          <div class="exam-item" @click="openPdf('/雅思/剑桥20.pdf', '剑桥20')">
            <div class="exam-info">
              <div class="exam-name">📋 剑桥20</div>
            </div>
            <el-button class="exam-btn">查看PDF</el-button>
          </div>
          <div class="exam-item" @click="openPdf('/雅思/IELTS 21 Practice Test.pdf', 'IELTS 21 Practice Test')">
            <div class="exam-info">
              <div class="exam-name">📋 IELTS 21 Practice Test</div>
            </div>
            <el-button class="exam-btn">查看PDF</el-button>
          </div>
        </div>
      </div>


    </div>


  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

// 雅思部分PDF状态
const currentPdf = ref<string | null>(null)
const currentPdfName = ref<string>('')

// 打开PDF
const openPdf = (pdfPath: string, pdfName: string) => {
  currentPdf.value = pdfPath
  currentPdfName.value = pdfName
}

// 关闭PDF
const closePdf = () => {
  currentPdf.value = null
  currentPdfName.value = ''
}

// 任务管理
interface Task {
  id: number
  name: string
  completed: boolean
}

const tasks = ref<Task[]>([])
const newTaskName = ref<string>('')

// 从localStorage加载任务
const loadTasks = () => {
  const savedTasks = localStorage.getItem('ielts-tasks')
  if (savedTasks) {
    tasks.value = JSON.parse(savedTasks)
  } else {
    // 默认任务
    tasks.value = [
      { id: Date.now(), name: '听力Section 4精听 15min', completed: true },
      { id: Date.now() + 1, name: '阅读1篇计时练习 20min', completed: false },
      { id: Date.now() + 2, name: '大作文Task 2 40min', completed: false },
      { id: Date.now() + 3, name: '场景词汇复习 10min', completed: false }
    ]
  }
}

// 保存任务到localStorage
const saveTasks = () => {
  localStorage.setItem('ielts-tasks', JSON.stringify(tasks.value))
}

// 添加任务
const addTask = () => {
  if (newTaskName.value.trim()) {
    tasks.value.push({
      id: Date.now(),
      name: newTaskName.value.trim(),
      completed: false
    })
    newTaskName.value = ''
    saveTasks()
  }
}

// 切换任务完成状态
const toggleTask = (id: number) => {
  const task = tasks.value.find(t => t.id === id)
  if (task) {
    task.completed = !task.completed
    saveTasks()
  }
}

// 删除任务
const deleteTask = (id: number) => {
  tasks.value = tasks.value.filter(t => t.id !== id)
  saveTasks()
}

// 计算已完成的任务数量
const completedCount = computed(() => {
  return tasks.value.filter(t => t.completed).length
})

onMounted(() => {
  loadTasks()
})
</script>

<style scoped>
.ielts-english {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 24px;
}

/* 考试部分 */
.exam-section {
  max-width: 900px;
  margin: 0 auto 32px;
}

/* 顶部用户信息卡 */
.header-card {
  background: linear-gradient(135deg, #1a237e 0%, #3949ab 100%);
  border-radius: 16px;
  padding: 24px 28px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(26, 35, 126, 0.3);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.header-icon {
  font-size: 28px;
}

.header-title {
  font-size: 24px;
  font-weight: 700;
  color: white;
}

.header-right {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.target-info,
.days-info {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.progress-bar {
  height: 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  margin-bottom: 8px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #42a5f5, #1976d2);
  border-radius: 4px;
  transition: width 0.6s ease;
}

.progress-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  text-align: right;
}

/* 三个科目大卡片 */
.subject-cards {
  display: grid;
  grid-template-columns: repeat(3, 286px);
  gap: 20px;
  margin-bottom: 24px;
  justify-content: center;
}

.subject-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border-top: 3px solid;
  position: relative;
  overflow: hidden;
}

.subject-card.listening-card {
  border-top-color: #1976d2;
}

.subject-card.reading-card {
  border-top-color: #2e7d32;
}

.subject-card.writing-card {
  border-top-color: #ed6c02;
}

.subject-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
}

.subject-icon {
  font-size: 48px;
  margin-bottom: 8px;
}

.subject-name {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
}

.subject-score {
  font-size: 36px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.subject-change {
  font-size: 14px;
  color: #2e7d32;
  margin-bottom: 4px;
}

.subject-change.same {
  color: #909399;
}

.subject-target {
  font-size: 14px;
  color: #606266;
  margin-bottom: 16px;
}

.subject-target.achieved {
  color: #2e7d32;
}

.subject-target.low {
  color: #d32f2f;
  font-weight: 600;
}

/* 今日任务 */
.tasks-card {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.tasks-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.tasks-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.tasks-progress {
  font-size: 14px;
  color: #1976d2;
  font-weight: 500;
}

/* 添加任务 */
.add-task {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.task-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s ease;
}

.task-input:focus {
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.1);
}

.add-btn {
  background-color: #1976d2;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
}

.add-btn:hover {
  background-color: #1565c0;
}

.tasks-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.task-item:hover {
  background: #e8f0fe;
}

.task-item.completed {
  background: #e8f5e9;
  opacity: 0.7;
}

.task-checkbox {
  font-size: 20px;
  color: #909399;
  cursor: pointer;
  user-select: none;
}

.task-checkbox.checked {
  color: #2e7d32;
}

.task-name {
  flex: 1;
  font-size: 14px;
  color: #606266;
}

.task-item.completed .task-name {
  text-decoration: line-through;
}

.delete-btn {
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 4px 8px;
  font-size: 12px;
}

.delete-btn:hover {
  background-color: #e64242;
}

.empty-tasks {
  text-align: center;
  color: #909399;
  padding: 24px;
  font-size: 14px;
}

/* 学习日历 */
.calendar-card {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.calendar-row {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.calendar-day {
  flex: 1;
  text-align: center;
  padding: 12px 8px;
  background: #f5f7fa;
  border-radius: 12px;
}

.calendar-day.today {
  background: #e3f2fd;
  border: 2px solid #1976d2;
}

.day-label {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
}

.calendar-day.today .day-label {
  color: #1976d2;
  font-weight: 600;
}

.day-status {
  font-size: 24px;
}

/* 真题模考列表 */
.mock-exam-card {
  background: white;
  border-radius: 16px;
  padding: 20px 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.exam-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.exam-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 12px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.exam-item:hover {
  background: #e8f0fe;
}

.exam-item:last-child {
  margin-bottom: 0;
}

.exam-info {
  flex: 1;
}

.exam-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
}

.exam-scores {
  font-size: 13px;
  color: #606266;
  margin-bottom: 10px;
}

.exam-total {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-bar {
  flex: 1;
  height: 6px;
  background: #e0e0e0;
  border-radius: 3px;
  overflow: hidden;
}

.total-fill {
  height: 100%;
  background: linear-gradient(90deg, #42a5f5, #1976d2);
  border-radius: 3px;
}

.total-text {
  font-size: 14px;
  font-weight: 600;
  color: #1976d2;
  min-width: 60px;
}

.exam-btn {
  background-color: #1976d2;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 500;
}

/* PDF展示区域 */
.pdf-container {
  display: flex;
  flex-direction: column;
}

.pdf-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  margin-bottom: 16px;
  border-bottom: 1px solid #e0e0e0;
}

.pdf-header span {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.pdf-iframe {
  width: 100%;
  height: 600px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ielts-english {
    padding: 16px;
  }

  .subject-cards {
    grid-template-columns: 1fr;
  }

  .header-right {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
