<template>
  <div class="cet4-homework-correction">
    <h2>四级真题作业批改</h2>
    
    <!-- 作业筛选 -->
    <el-card class="filter-card">
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="请选择状态">
              <el-option label="全部" value="" />
              <el-option label="待批改" value="submitted" />
              <el-option label="已批改" value="graded" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchSubmissions">搜索</el-button>
            <el-button @click="refreshList">刷新</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 作业列表 -->
    <el-card class="submissions-card">
      <el-table :data="filteredSubmissions" style="width: 100%" v-loading="loading">
        <el-table-column prop="homeworkTitle" label="作业标题" min-width="200" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="submissionDate" label="提交时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.submissionDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'graded' ? 'success' : 'warning'" size="small">
              {{ scope.row.status === 'graded' ? '已批改' : '待批改' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="80">
          <template #default="scope">
            {{ scope.row.score !== null ? scope.row.score + '分' : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewSubmission(scope.row)">
              {{ scope.row.status === 'graded' ? '查看' : '批改' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="filteredSubmissions.length === 0 && !loading" description="暂无作业提交" />
    </el-card>

    <!-- 批改对话框 -->
    <el-dialog v-model="correctionDialogVisible" title="批改作业" width="700px">
      <div v-if="selectedSubmission" class="correction-form">
        <div class="submission-info">
          <h4>{{ selectedSubmission.homeworkTitle }}</h4>
          <p class="meta">
            <span>学生：{{ selectedSubmission.studentName }}</span>
            <span>提交时间：{{ formatTime(selectedSubmission.submissionDate) }}</span>
          </p>
        </div>
        
        <el-divider />
        
        <div class="answer-section">
          <h5>学生客观题答案：</h5>
          <div class="answer-content" v-if="studentAnswers">
            <div v-for="(answer, key) in studentAnswers" :key="key" class="answer-item">
              <span class="answer-key">{{ formatAnswerKey(key) }}:</span>
              <span class="answer-value">{{ answer.selected !== undefined ? getOptionLetter(answer.selected) : '未答' }}</span>
              <span class="answer-status" :class="isCorrect(answer) ? 'correct' : 'incorrect'">
                {{ isCorrect(answer) ? '✓ 正确' : '✗ 错误' }}
              </span>
              <span class="correct-answer" v-if="!isCorrect(answer)">
                正确答案: {{ answer.answer }}
              </span>
            </div>
          </div>
        </div>
        
        <div class="subjective-section" v-if="selectedSubmission.subjectiveImage">
          <h5>学生主观题答案（图片）：</h5>
          <div class="image-container">
            <img :src="getImageUrl(selectedSubmission.subjectiveImage)" alt="主观题答案" @dblclick="previewImage(selectedSubmission.subjectiveImage)" />
          </div>
        </div>
        
        <el-divider />
        
        <div class="grading-section">
          <h5>评分</h5>
          <div class="auto-score" v-if="autoScore !== null">
            <span>客观题自动评分：</span>
            <span class="score-value">{{ autoScore }}分</span>
            <span class="score-detail">（共{{ totalObjectiveQuestions }}题，正确{{ correctCount }}题）</span>
          </div>
          
          <el-form :model="gradingForm" label-width="100px">
            <el-form-item label="主观题得分">
              <el-input-number 
                v-model="gradingForm.subjectiveScore" 
                :min="0" 
                :max="subjectiveMaxScore" 
                :step="1"
              />
              <span style="margin-left: 10px; color: #909399;">满分{{ subjectiveMaxScore }}分</span>
            </el-form-item>
            <el-form-item label="总分">
              <span class="total-score">{{ totalScore }}分</span>
            </el-form-item>
            <el-form-item label="评语">
              <el-input 
                v-model="gradingForm.feedback" 
                type="textarea" 
                :rows="4" 
                placeholder="请输入评语"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
      <template #footer>
        <el-button @click="correctionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGrade" :loading="submitting">提交评分</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="imageDialogVisible" title="图片预览" width="80%">
      <div class="image-preview-container">
        <img :src="previewImageUrl" alt="预览" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

const filterForm = ref({
  status: ''
})

const loading = ref(false)
const correctionDialogVisible = ref(false)
const imageDialogVisible = ref(false)
const submitting = ref(false)
const previewImageUrl = ref('')

const submissions = ref<any[]>([])
const selectedSubmission = ref<any>(null)
const studentAnswers = ref<any>(null)

const gradingForm = ref({
  subjectiveScore: 0,
  feedback: ''
})

const subjectiveMaxScore = 50 // 主观题满分50分（作文和翻译）

const filteredSubmissions = computed(() => {
  if (!filterForm.value.status) return submissions.value
  return submissions.value.filter(s => s.status === filterForm.value.status)
})

const autoScore = computed(() => {
  if (!studentAnswers.value) return null
  let correct = 0
  let total = 0
  Object.values(studentAnswers.value).forEach((answer: any) => {
    if (answer.answer !== undefined) {
      total++
      if (isCorrect(answer)) correct++
    }
  })
  return Math.round((correct / total) * 100)
})

const correctCount = computed(() => {
  if (!studentAnswers.value) return 0
  let correct = 0
  Object.values(studentAnswers.value).forEach((answer: any) => {
    if (isCorrect(answer)) correct++
  })
  return correct
})

const totalObjectiveQuestions = computed(() => {
  if (!studentAnswers.value) return 0
  return Object.keys(studentAnswers.value).length
})

const totalScore = computed(() => {
  return (autoScore.value || 0) + gradingForm.value.subjectiveScore
})

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

const formatAnswerKey = (key: string) => {
  return key.replace(/_/g, ' ')
}

const getOptionLetter = (index: number) => {
  return String.fromCharCode(65 + index) // 0->A, 1->B, etc.
}

const isCorrect = (answer: any) => {
  if (answer.selected === undefined) return false
  return getOptionLetter(answer.selected) === answer.answer
}

const getImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http') || url.startsWith('blob:')) {
    return url
  }
  return `http://localhost:8080${url}`
}

const fetchSubmissions = async () => {
  loading.value = true
  try {
    const response = await api.cet4Homework.getTeacherSubmissions()
    if (response.success) {
      submissions.value = response.submissions
    } else {
      ElMessage.error(response.message || '获取作业提交列表失败')
    }
  } catch (error: any) {
    console.error('获取作业提交列表失败:', error)
    ElMessage.error('获取作业提交列表失败')
  } finally {
    loading.value = false
  }
}

const refreshList = () => {
  fetchSubmissions()
}

const viewSubmission = (submission: any) => {
  selectedSubmission.value = submission
  
  // 解析学生答案
  try {
    if (submission.studentAnswer) {
      studentAnswers.value = JSON.parse(submission.studentAnswer)
    } else {
      studentAnswers.value = null
    }
  } catch (e) {
    console.error('解析学生答案失败:', e)
    studentAnswers.value = null
  }
  
  // 初始化评分表单
  gradingForm.value = {
    subjectiveScore: submission.score || 0,
    feedback: submission.feedback || ''
  }
  
  correctionDialogVisible.value = true
}

const previewImage = (imageUrl: string) => {
  previewImageUrl.value = getImageUrl(imageUrl)
  imageDialogVisible.value = true
}

const submitGrade = async () => {
  if (!selectedSubmission.value) return
  
  if (!gradingForm.value.feedback) {
    ElMessage.error('请输入评语')
    return
  }
  
  try {
    submitting.value = true
    
    const response = await api.cet4Homework.grade({
      submissionId: selectedSubmission.value.id,
      score: totalScore.value,
      feedback: gradingForm.value.feedback
    })
    
    if (response.success) {
      ElMessage.success('评分成功')
      correctionDialogVisible.value = false
      fetchSubmissions()
    } else {
      ElMessage.error('评分失败: ' + response.message)
    }
  } catch (error: any) {
    console.error('评分失败:', error)
    ElMessage.error('评分失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchSubmissions()
})
</script>

<style scoped>
.cet4-homework-correction {
  padding: 20px 0;
}

.cet4-homework-correction h2 {
  margin-bottom: 20px;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.submissions-card {
  margin-bottom: 20px;
}

.correction-form {
  max-height: 70vh;
  overflow-y: auto;
}

.submission-info {
  margin-bottom: 20px;
}

.submission-info h4 {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: 600;
}

.meta {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.answer-section,
.subjective-section,
.grading-section {
  margin-bottom: 20px;
}

.answer-section h5,
.subjective-section h5,
.grading-section h5 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.answer-content {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.answer-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px solid #e4e7ed;
}

.answer-item:last-child {
  border-bottom: none;
}

.answer-key {
  font-weight: 600;
  min-width: 150px;
}

.answer-value {
  min-width: 60px;
}

.answer-status {
  font-weight: bold;
}

.answer-status.correct {
  color: #67c23a;
}

.answer-status.incorrect {
  color: #f56c6c;
}

.correct-answer {
  color: #909399;
  font-size: 14px;
}

.image-container {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.image-container img {
  max-width: 100%;
  max-height: 300px;
  cursor: pointer;
  border-radius: 4px;
  transition: transform 0.2s;
}

.image-container img:hover {
  transform: scale(1.02);
}

.auto-score {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #ecf5ff;
  border-radius: 4px;
}

.score-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.score-detail {
  color: #909399;
}

.total-score {
  font-size: 24px;
  font-weight: bold;
  color: #67c23a;
}

.image-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.image-preview-container img {
  max-width: 100%;
  max-height: 600px;
  object-fit: contain;
}
</style>