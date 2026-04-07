<template>
  <div class="homework-correction">
    <h2>待批改作业</h2>
    
    <!-- 作业筛选 -->
    <el-card class="filter-card">
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="状态">
            <el-select v-model="filterForm.status" placeholder="请选择状态">
              <el-option label="全部" value="" />
              <el-option label="待批改" value="pending" />
              <el-option label="已批改" value="corrected" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchHomeworks">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 作业列表 -->
    <el-card class="homeworks-card">
      <div class="homeworks-list">
        <div v-for="submission in filteredSubmissions" :key="submission.id" class="submission-item">
          <div class="submission-header">
            <div class="submission-info">
              <h4>{{ submission.homework?.title || '作业' }}</h4>
              <p class="submission-meta">
                <span>学生：{{ submission.student?.username || submission.student?.nickname || '未知学生' }}</span>
                <span>提交时间：{{ formatTime(submission.submissionDate) }}</span>
                <el-tag :type="submission.status === 'graded' ? 'success' : 'warning'" size="small">
                  {{ submission.status === 'graded' ? '已批改' : '待批改' }}
                </el-tag>
              </p>
            </div>
            <el-button type="primary" size="small" @click="correctHomework(submission)">
              {{ submission.status === 'graded' ? '查看' : '批改' }}
            </el-button>
          </div>
          <div class="submission-content">
            <h5>作业内容：</h5>
            <p>{{ submission.content }}</p>
            <div v-if="submission.image" class="submission-image">
              <img :src="getImageUrl(submission.image)" alt="学生提交的图片" />
            </div>
          </div>
          <div v-if="submission.status === 'graded'" class="submission-result">
            <h5>批改结果：</h5>
            <p class="score">分数：{{ submission.score }}分</p>
            <p class="feedback">评语：{{ submission.feedback }}</p>
          </div>
        </div>
        <el-empty v-if="filteredSubmissions.length === 0" description="暂无作业" />
      </div>
    </el-card>

    <!-- 批改作业对话框 -->
    <el-dialog v-model="correctionDialogVisible" title="批改作业" width="600px">
      <div v-if="selectedSubmission" class="correction-form">
        <div class="submission-preview">
          <h4>{{ selectedSubmission.homework?.title || '作业' }}</h4>
          <p class="preview-meta">
            <span>学生：{{ selectedSubmission.student?.username || selectedSubmission.student?.nickname || '未知学生' }}</span>
            <span>提交时间：{{ formatTime(selectedSubmission.submissionDate) }}</span>
          </p>
          <div class="preview-content">
            <h5>作业内容：</h5>
            <p>{{ selectedSubmission.content }}</p>
            <div v-if="selectedSubmission.image" class="preview-image">
              <img :src="getImageUrl(selectedSubmission.image)" alt="学生提交的图片" />
            </div>
          </div>
        </div>
        <el-divider />
        <el-form :model="correctionForm" label-width="80px">
          <el-form-item label="分数">
            <el-input-number 
              v-model="correctionForm.score" 
              :min="0" 
              :max="100" 
              :step="1"
              placeholder="请输入分数"
            />
            <span style="margin-left: 10px; color: #909399;">分</span>
          </el-form-item>
          <el-form-item label="评语">
            <el-input 
              v-model="correctionForm.feedback" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入评语"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="correctionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCorrection" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

// 筛选表单
const filterForm = ref({
  status: ''
})

// 批改对话框
const correctionDialogVisible = ref(false)
const selectedSubmission = ref<any>(null)
const submitting = ref(false)

const correctionForm = ref({
  score: 0,
  feedback: ''
})

// 作业提交数据
const submissions = ref([])

// 筛选后的作业提交
const filteredSubmissions = computed(() => {
  return submissions.value.filter(submission => {
    if (!filterForm.value.status) return true
    if (filterForm.value.status === 'pending') return submission.status === 'submitted'
    if (filterForm.value.status === 'corrected') return submission.status === 'graded'
    return true
  })
})

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 获取图片URL
const getImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) {
    return url
  }
  return `http://localhost:8080${url}`
}

// 从后端API获取作业提交数据
const fetchHomeworks = async () => {
  try {
    console.log('Fetching homework submissions from API...')
    const response = await api.homeworks.getAllSubmissions()
    submissions.value = response
    console.log('Fetched homework submissions:', submissions.value)
  } catch (error) {
    console.error('Failed to fetch homework submissions:', error)
    ElMessage.error('获取作业列表失败')
  }
}

// 页面加载时获取作业数据
onMounted(() => {
  fetchHomeworks()
})

// 打开批改对话框
const correctHomework = (submission: any) => {
  selectedSubmission.value = submission
  correctionForm.value.score = submission.score || 0
  correctionForm.value.feedback = submission.feedback || ''
  correctionDialogVisible.value = true
}

// 提交批改
const submitCorrection = async () => {
  if (!correctionForm.value.score || correctionForm.value.score < 0 || correctionForm.value.score > 100) {
    ElMessage.error('请输入0-100之间的分数')
    return
  }
  
  if (!correctionForm.value.feedback) {
    ElMessage.error('请输入评语')
    return
  }
  
  try {
    submitting.value = true
    
    // 调用后端API提交批改
    const response = await api.homeworks.correct({
      submissionId: selectedSubmission.value.id,
      score: correctionForm.value.score,
      feedback: correctionForm.value.feedback
    })
    console.log('作业批改成功:', response)
    
    // 更新作业数据
    if (selectedSubmission.value) {
      selectedSubmission.value.score = correctionForm.value.score
      selectedSubmission.value.feedback = correctionForm.value.feedback
      selectedSubmission.value.status = 'graded'
    }
    
    // 刷新列表
    await fetchHomeworks()
    
    ElMessage.success('批改成功')
    correctionDialogVisible.value = false
  } catch (error: any) {
    console.error('作业批改失败:', error)
    ElMessage.error(error.message || '批改失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.homework-correction {
  padding: 20px 0;
}

.homework-correction h2 {
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

.homeworks-card {
  margin-bottom: 20px;
}

.homeworks-list {
  max-height: 600px;
  overflow-y: auto;
}

.submission-item {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.submission-item:last-child {
  border-bottom: none;
}

.submission-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.submission-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
}

.submission-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #606266;
  font-size: 14px;
}

.submission-content {
  margin-bottom: 15px;
}

.submission-content h5,
.submission-result h5 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
  font-weight: 600;
}

.submission-content p,
.submission-result p {
  margin: 0;
  color: #303133;
  line-height: 1.6;
}

.submission-image,
.preview-image {
  margin-top: 15px;
}

.submission-image img,
.preview-image img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.submission-result {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.score {
  color: #409EFF;
  font-weight: 600;
  font-size: 16px;
}

.feedback {
  color: #67C23A;
}

.correction-form {
  max-height: 600px;
  overflow-y: auto;
}

.submission-preview h4 {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: 600;
}

.preview-meta {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
  margin-bottom: 15px;
}

.preview-content {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.preview-content h5 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
}

.preview-content p {
  margin: 0;
  line-height: 1.6;
}

@media screen and (max-width: 768px) {
  .filter-form {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .submission-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .submission-meta {
    flex-wrap: wrap;
  }
}
</style>
