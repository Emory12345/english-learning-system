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
              <h4>{{ submission.homework?.title || submission.homeworkTitle || '作业' }}</h4>
              <p class="submission-meta">
                <span>学生：{{ submission.student?.username || submission.student?.nickname || submission.studentName || '未知学生' }}</span>
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
          <h4>{{ selectedSubmission.homework?.title || selectedSubmission.homeworkTitle || '作业' }}</h4>
          <p class="preview-meta">
            <span>学生：{{ selectedSubmission.student?.username || selectedSubmission.student?.nickname || selectedSubmission.studentName || '未知学生' }}</span>
            <span>提交时间：{{ formatTime(selectedSubmission.submissionDate) }}</span>
          </p>
          <!-- 自动批改结果 -->
          <div v-if="selectedSubmission.studentAnswer" class="auto-grading-result">
            <h5>客观题自动批改结果：</h5>
            <p>得分：{{ correctionForm.score }}分</p>
            <p>正确率：{{ Math.round((correctionForm.score / 55) * 100) }}%（{{ correctionForm.score }}/55）</p>
          </div>
          <div class="preview-content">
            <div v-if="loadingPaper" class="loading">加载中...</div>
            <div v-else-if="paperData">
              <!-- 试卷标题 -->
              <div class="paper-title">
                <h2>{{ paperData.title }}(含听力)</h2>
              </div>
              
              <!-- 听力播放器 -->
              <div class="audio-player">
                <audio controls>
                  <source src="/cet4_2022_09_1.mp3" type="audio/mpeg">
                  您的浏览器不支持音频播放。
                 </audio>
              </div>
              
              <!-- 真题内容 -->
              <div class="paper-content">
                <h3>大学英语四级考试 {{ paperData.title }}</h3>
                
                <div v-if="paperData.sections" v-for="(section, sectionIndex) in paperData.sections" :key="sectionIndex" class="section-item">
                  <div class="section-title">
                    <h4>{{ section.title }} {{ section.time }}</h4>
                  </div>
                  <div v-if="section.title === 'Part II'" class="audio-player">
                    <audio controls>
                      <source src="/cet4_2022_09_1.mp3" type="audio/mpeg">
                      您的浏览器不支持音频播放。
                     </audio>
                  </div>
                  <div v-if="section.directions || section.direction" class="section-direction">
                    {{ section.directions || section.direction }}
                  </div>
                  <div v-if="section.passage" class="section-passage">
                    {{ section.passage }}
                  </div>
                  <div v-if="section.content" class="section-content">
                    {{ section.content }}
                  </div>
                  <div v-if="section.wordBank" class="section-word-bank">
                    <h6>Word Bank:</h6>
                    <div class="word-bank-items">
                      <div 
                        v-for="(word, idx) in section.wordBank" 
                        :key="idx"
                        class="word-bank-item"
                      >
                        {{ word }}
                      </div>
                    </div>
                  </div>
                  <!-- 作文上传 - 只在主Part I section显示 -->
                  <div v-if="section.title === 'Part I Writing'" class="subjective-submit-area">
                    <h5>作文上传</h5>
                    <div v-if="selectedSubmission.writingImage" class="preview-inline">
                      <img 
                        :src="getImageUrl(selectedSubmission.writingImage)" 
                        alt="作文答案" 
                        class="image-preview-inline clickable-image" 
                        @dblclick="previewImage(getImageUrl(selectedSubmission.writingImage))"
                      />
                    </div>
                    <div v-else>
                      <p>学生未上传作文图片</p>
                    </div>
                  </div>
                  <!-- 翻译上传 - 只在主Part IV section显示 -->
                  <div v-if="section.title === 'Part IV Translation'" class="subjective-submit-area">
                    <h5>翻译上传</h5>
                    <div v-if="selectedSubmission.translationImage" class="preview-inline">
                      <img 
                        :src="getImageUrl(selectedSubmission.translationImage)" 
                        alt="翻译答案" 
                        class="image-preview-inline clickable-image" 
                        @dblclick="previewImage(getImageUrl(selectedSubmission.translationImage))"
                      />
                    </div>
                    <div v-else>
                      <p>学生未上传翻译图片</p>
                    </div>
                  </div>
                  <div v-if="section.questions" class="section-questions">
                    <div v-for="(question, qIndex) in section.questions" :key="question.id || qIndex" class="question-item">
                      <div class="question-content">{{ question.content }}</div>
                      <div v-if="question.options" class="question-options">
                        <div 
                          v-for="(option, idx) in question.options" 
                          :key="idx"
                          class="option-item"
                        >
                          <div class="radio-button">
                            <div 
                              class="radio-inner"
                              :class="{ 'selected': parseJson(selectedSubmission.studentAnswer) && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_${qIndex}`] && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_${qIndex}`].selected === idx }"
                            ></div>
                          </div>
                          <div class="option-text">{{ option }}</div>
                        </div>
                      </div>
                      <!-- 显示学生的答案 -->
                      <div v-if="parseJson(selectedSubmission.studentAnswer) && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_${qIndex}`]" class="question-answer">
                        <strong>学生答案：</strong>{{ numberToLetter(parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_${qIndex}`].selected) }}
                        <strong>正确答案：</strong>{{ parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_${qIndex}`].answer }}
                      </div>
                    </div>
                  </div>
                  <div v-if="section.sections" class="sub-sections">
                    <div v-for="(subSection, subSectionIndex) in section.sections" :key="subSectionIndex" class="sub-section-item">
                      <div class="sub-section-title">
                        <h5>{{ subSection.title }}</h5>
                      </div>
                      <div v-if="subSection.directions" class="sub-section-direction">
                        {{ subSection.directions }}
                      </div>
                      <div v-if="subSection.passage" class="sub-section-passage">
                        {{ subSection.passage }}
                      </div>
                      <div v-if="subSection.wordBank" class="section-word-bank">
                        <h6>Word Bank:</h6>
                        <div class="word-bank-items">
                          <div 
                            v-for="(word, idx) in subSection.wordBank" 
                            :key="idx"
                            class="word-bank-item"
                          >
                            {{ word }}
                          </div>
                        </div>
                      </div>
                      <div v-if="subSection.questions" class="section-questions">
                        <div v-for="(question, qIndex) in subSection.questions" :key="question.id || qIndex" class="question-item">
                          <div class="question-content">{{ question.content }}</div>
                          <div v-if="question.options" class="question-options">
                            <div 
                              v-for="(option, idx) in question.options" 
                              :key="idx"
                              class="option-item"
                            >
                              <div class="radio-button">
                                <div 
                                  class="radio-inner"
                                  :class="{ 'selected': parseJson(selectedSubmission.studentAnswer) && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_${qIndex}`] && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_${qIndex}`].selected === idx }"
                                ></div>
                              </div>
                              <div class="option-text">{{ option }}</div>
                            </div>
                          </div>
                          <div v-else-if="subSection.title === 'Section B'" class="matching-options">
                            <div class="matching-option">
                              <input 
                                type="text" 
                                :value="parseJson(selectedSubmission.studentAnswer) && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_${qIndex}`] ? parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_${qIndex}`].selected : ''" 
                                class="matching-input"
                                disabled
                              />
                            </div>
                          </div>
                          <!-- 显示学生的答案 -->
                          <div v-if="parseJson(selectedSubmission.studentAnswer) && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_${qIndex}`]" class="question-answer">
                            <strong>学生答案：</strong>{{ numberToLetter(parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_${qIndex}`].selected) }}
                            <strong>正确答案：</strong>{{ parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_${qIndex}`].answer }}
                          </div>
                        </div>
                      </div>
                      <div v-if="subSection.passages" class="passages">
                        <div v-for="(passage, passageIndex) in subSection.passages" :key="passageIndex" class="passage-item">
                          <div v-if="passage.content" class="passage-content">
                            {{ passage.content }}
                          </div>
                          <div v-if="passage.questions_info" class="passage-questions-info">
                            {{ passage.questions_info }}
                          </div>
                          <div v-if="passage.questions" class="passage-questions">
                            <div v-for="(question, qIndex) in passage.questions" :key="question.id || qIndex" class="question-item">
                              <div class="question-content" v-if="question.content">{{ question.content }}</div>
                              <div v-if="question.options" class="question-options">
                                <div 
                                  v-for="(option, idx) in question.options" 
                                  :key="idx"
                                  class="option-item"
                                >
                                  <div class="radio-button">
                                    <div 
                                      class="radio-inner"
                                      :class="{ 'selected': parseJson(selectedSubmission.studentAnswer) && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_passage_${passageIndex}_${qIndex}`] && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_passage_${passageIndex}_${qIndex}`].selected === idx }"
                                    ></div>
                                  </div>
                                  <div class="option-text">{{ option }}</div>
                                </div>
                              </div>
                              <!-- 显示学生的答案 -->
                              <div v-if="parseJson(selectedSubmission.studentAnswer) && parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_passage_${passageIndex}_${qIndex}`]" class="question-answer">
                                <strong>学生答案：</strong>{{ numberToLetter(parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_passage_${passageIndex}_${qIndex}`].selected) }}
                                <strong>正确答案：</strong>{{ parseJson(selectedSubmission.studentAnswer)[`section_${sectionIndex}_subsection_${subSectionIndex}_passage_${passageIndex}_${qIndex}`].answer }}
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div v-else-if="selectedSubmission.studentAnswer">
              <div v-if="typeof selectedSubmission.studentAnswer === 'string' && selectedSubmission.studentAnswer.startsWith('{')">
                <div v-if="parseJson(selectedSubmission.studentAnswer)">
                  <div v-if="Object.keys(parseJson(selectedSubmission.studentAnswer)).length > 0">
                    <h6>客观题答案：</h6>
                    <ul>
                      <li v-for="(answer, key) in parseJson(selectedSubmission.studentAnswer)" :key="key">
                        <span>{{ key }}: </span>
                        <span>选择：{{ answer.selected }}，正确答案：{{ answer.answer }}</span>
                      </li>
                    </ul>
                  </div>
                  <div v-else>
                    <p>暂无客观题答案</p>
                  </div>
                </div>
                <div v-else>
                  <p>解析答案失败：{{ selectedSubmission.studentAnswer }}</p>
                </div>
              </div>
              <div v-else>
                <p>{{ selectedSubmission.studentAnswer }}</p>
              </div>
            </div>
            <div v-else-if="selectedSubmission.content">
              <p>{{ selectedSubmission.content }}</p>
            </div>
            <div v-else>
              <p>暂无作业内容</p>
            </div>
            <div v-if="selectedSubmission.audio" class="preview-audio">
              <h6>提交的音频：</h6>
              <audio :src="getImageUrl(selectedSubmission.audio)" controls> </audio>
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

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="imageDialogVisible"
      title="图片预览"
      width="80%"
      custom-class="image-preview-dialog"
    >
      <div class="image-preview-container">
        <img :src="previewImageUrl" alt="预览图片" class="large-image" />
      </div>
      <template #footer>
        <el-button @click="imageDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'
import axios from 'axios'

// 筛选表单
const filterForm = ref({
  status: ''
})

// 批改对话框
const correctionDialogVisible = ref(false)
const selectedSubmission = ref<any>(null)
const submitting = ref(false)

// 试卷数据
const paperData = ref<any>(null)
const loadingPaper = ref(false)

// 图片预览
const imageDialogVisible = ref(false)
const previewImageUrl = ref('')

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
  if (url.startsWith('http') || url.startsWith('data:')) {
    return url
  }
  return `http://localhost:5178/api${url}`
}

// 安全解析JSON
const parseJson = (jsonString: string) => {
  try {
    return JSON.parse(jsonString)
  } catch (error) {
    console.error('Failed to parse JSON:', error)
    console.error('JSON string:', jsonString)
    return null
  }
}

// 将数字转换为字母（0 -> A, 1 -> B, 2 -> C, 3 -> D）
const numberToLetter = (num: number) => {
  return String.fromCharCode(65 + num)
}

// 自动批改客观题
const autoGradeObjectiveQuestions = (studentAnswers: any, paperData: any) => {
  if (!studentAnswers || !paperData || !paperData.sections) {
    return { score: 0, total: 0, correct: 0 }
  }
  
  let correctCount = 0
  let totalCount = 0
  
  // 遍历试卷的所有部分
  paperData.sections.forEach((section: any, sectionIndex: number) => {
    // 处理听力部分的子部分
    if (section.sections) {
      section.sections.forEach((subSection: any, subSectionIndex: number) => {
        // 处理每个子部分的文章
        if (subSection.passages) {
          subSection.passages.forEach((passage: any, passageIndex: number) => {
            // 处理每个文章的问题
            if (passage.questions) {
              passage.questions.forEach((question: any, qIndex: number) => {
                if (question.answer) {
                  totalCount++
                  // 构建学生答案的键
                  const answerKey = `section_${sectionIndex}_subsection_${subSectionIndex}_passage_${passageIndex}_${qIndex}`
                  // 获取学生答案
                  const studentAnswer = studentAnswers[answerKey]
                  if (studentAnswer) {
                    // 将学生的数字答案转换为字母
                    const studentAnswerLetter = numberToLetter(studentAnswer.selected)
                    // 比较学生答案和正确答案
                    if (studentAnswerLetter === question.answer) {
                      correctCount++
                    }
                  }
                }
              })
            }
          })
        }
        // 处理直接在子部分下的问题（如Section B的匹配题）
        if (subSection.questions) {
          subSection.questions.forEach((question: any, qIndex: number) => {
            if (question.answer) {
              totalCount++
              // 构建学生答案的键
              const answerKey = `section_${sectionIndex}_subsection_${subSectionIndex}_${qIndex}`
              // 获取学生答案
              const studentAnswer = studentAnswers[answerKey]
              if (studentAnswer) {
                // 对于匹配题，直接比较答案
                if (subSection.title === 'Section B') {
                  if (studentAnswer.selected && studentAnswer.selected.toUpperCase() === question.answer) {
                    correctCount++
                  }
                } else {
                  // 对于选择题，将数字答案转换为字母
                  const studentAnswerLetter = numberToLetter(studentAnswer.selected)
                  if (studentAnswerLetter === question.answer) {
                    correctCount++
                  }
                }
              }
            }
          })
        }
      })
    }
    // 处理直接在部分下的问题
    if (section.questions) {
      section.questions.forEach((question: any, qIndex: number) => {
        if (question.answer) {
          totalCount++
          // 构建学生答案的键
          const answerKey = `section_${sectionIndex}_${qIndex}`
          // 获取学生答案
          const studentAnswer = studentAnswers[answerKey]
          if (studentAnswer) {
            // 将学生的数字答案转换为字母
            const studentAnswerLetter = numberToLetter(studentAnswer.selected)
            // 比较学生答案和正确答案
            if (studentAnswerLetter === question.answer) {
              correctCount++
            }
          }
        }
      })
    }
  })
  
  // 计算得分（假设每题1分）
  const score = correctCount
  
  return { score, total: totalCount, correct: correctCount }
}

// 从后端API获取作业提交数据
const fetchHomeworks = async () => {
  try {
    console.log('Fetching homework submissions from API...')
    // 获取普通作业
    const regularSubmissions = await api.homeworks.getTeacherSubmissions()
    console.log('Fetched regular homework submissions:', regularSubmissions)
    
    // 获取四级作业
    const cet4Submissions = await api.cet4Homework.getTeacherSubmissions()
    console.log('Fetched CET4 homework submissions:', cet4Submissions)
    
    // 合并两种作业
    let cet4SubmissionsArray = []
    if (Array.isArray(cet4Submissions)) {
      cet4SubmissionsArray = cet4Submissions
    } else if (cet4Submissions.submissions) {
      // 检查submissions是否是数组
      if (Array.isArray(cet4Submissions.submissions)) {
        cet4SubmissionsArray = cet4Submissions.submissions
      } else {
        console.error('CET4 submissions is not an array:', cet4Submissions.submissions)
      }
    } else {
      console.error('CET4 submissions not found:', cet4Submissions)
    }
    
    submissions.value = [...regularSubmissions, ...cet4SubmissionsArray]
    console.log('Merged homework submissions:', submissions.value)
    
    // 检查四级作业的学生答案
    submissions.value.forEach((sub: any, index: number) => {
      if (sub.homeworkTitle) {
        console.log(`Submission ${index} (CET4):`, {
          id: sub.id,
          homeworkTitle: sub.homeworkTitle,
          studentName: sub.studentName,
          studentAnswer: sub.studentAnswer,
          studentAnswerType: typeof sub.studentAnswer,
          studentAnswerStartsWithBrace: typeof sub.studentAnswer === 'string' && sub.studentAnswer.startsWith('{'),
          parsedAnswer: parseJson(sub.studentAnswer),
          parsedAnswerKeys: parseJson(sub.studentAnswer) ? Object.keys(parseJson(sub.studentAnswer)) : [],
          paperContent: sub.paperContent,
          paperContentType: typeof sub.paperContent,
          paperContentStartsWithBrace: typeof sub.paperContent === 'string' && sub.paperContent.startsWith('{'),
          parsedPaperContent: parseJson(sub.paperContent)
        });
      }
    });
    
    // 检查是否有包含audio字段的提交
    const submissionsWithAudio = submissions.value.filter((sub: any) => sub.audio)
    console.log('Submissions with audio:', submissionsWithAudio)
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
const correctHomework = async (submission: any) => {
  selectedSubmission.value = submission
  correctionForm.value.score = submission.score || 0
  correctionForm.value.feedback = submission.feedback || ''
  
  // 加载试卷数据
  loadingPaper.value = true
  try {
    // 从本地JSON文件读取真题内容
    const response = await axios.get('/2022-09-cet4-complete.json')
    paperData.value = response.data
    console.log('Loaded paper data:', paperData.value)
    
    // 自动批改客观题
    if (submission.studentAnswer) {
      console.log('Student answer:', submission.studentAnswer)
      const studentAnswers = parseJson(submission.studentAnswer)
      console.log('Parsed student answers:', studentAnswers)
      if (studentAnswers) {
        const gradingResult = autoGradeObjectiveQuestions(studentAnswers, paperData.value)
        console.log('Auto grading result:', gradingResult)
        // 将客观题得分填充到批改表单中
        correctionForm.value.score = gradingResult.score
        console.log('Correction form score:', correctionForm.value.score)
      }
    }
  } catch (error) {
    console.error('Failed to load paper data:', error)
    ElMessage.error('加载试卷数据失败')
  } finally {
    loadingPaper.value = false
  }
  
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
    
    // 检查作业类型，调用对应的批改方法
    let response
    if (selectedSubmission.value.homeworkTitle) {
      // 四级作业
      response = await api.cet4Homework.grade({
        submissionId: selectedSubmission.value.id,
        score: correctionForm.value.score,
        feedback: correctionForm.value.feedback
      })
    } else {
      // 普通作业
      response = await api.homeworks.correct({
        submissionId: selectedSubmission.value.id,
        score: correctionForm.value.score,
        feedback: correctionForm.value.feedback
      })
    }
    
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

// 预览图片
const previewImage = (imageUrl: string) => {
  previewImageUrl.value = imageUrl
  imageDialogVisible.value = true
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
}

/* 批改作业对话框样式 */
.correction-form {
  max-width: 100%;
}

.submission-preview {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.preview-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  color: #606266;
  font-size: 14px;
}

.preview-content {
  margin-top: 20px;
}

/* 试卷标题样式 */
.paper-title {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e4e7ed;
}

.paper-title h2 {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

/* 听力播放器样式 */
.audio-player {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.audio-player audio {
  width: 100%;
  max-width: 600px;
  border-radius: 4px;
}

/* 真题内容样式 */
.paper-content {
  margin-top: 30px;
}

.paper-content h3 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 20px;
  color: #303133;
}

/* 部分样式 */
.section-item {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.section-title {
  margin-bottom: 10px;
  font-weight: bold;
}

.section-direction {
  margin-bottom: 15px;
  font-style: italic;
  color: #606266;
}

.section-passage {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
  line-height: 1.6;
}

.section-content {
  margin-bottom: 15px;
  line-height: 1.5;
}

/* 词汇银行样式 */
.section-word-bank {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.section-word-bank h6 {
  margin-bottom: 10px;
  font-weight: bold;
  color: #303133;
}

.word-bank-items {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.word-bank-item {
  padding: 5px 10px;
  background-color: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.word-bank-item:hover {
  background-color: #d9ecff;
  transform: translateY(-1px);
}

/* 子部分样式 */
.sub-sections {
  margin-top: 15px;
  margin-left: 20px;
}

.sub-section-item {
  margin-bottom: 20px;
}

.sub-section-title {
  margin-bottom: 10px;
  font-weight: bold;
}

.sub-section-passage {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
  line-height: 1.6;
}

/* 问题样式 */
.question-item {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.question-content {
  margin-bottom: 10px;
  font-size: 16px;
  line-height: 1.5;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
  margin-bottom: 15px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 0;
  padding-left: 10px;
}

.option-item:hover {
  background-color: #f5f7fa;
}

.radio-button {
  width: 20px;
  height: 20px;
  border: 2px solid #dcdfe6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s;
}

.radio-inner {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  transition: all 0.3s;
}

.radio-inner.selected {
  background-color: #409eff;
}

.option-text {
  flex: 1;
  font-size: 14px;
  line-height: 1.5;
}

.question-answer {
  margin-top: 10px;
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

/* 匹配题样式 */
.matching-options {
  margin-top: 10px;
  margin-bottom: 15px;
}

.matching-input {
  width: 100px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s;
}

/* 文章样式 */
.passages {
  margin-top: 15px;
}

.passage-item {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.passage-content {
  margin-bottom: 15px;
  line-height: 1.5;
}

.passage-questions-info {
  margin-bottom: 15px;
  font-weight: bold;
  color: #303133;
}

.passage-questions {
  margin-top: 15px;
}

/* 主观题上传区域样式 */
.subjective-submit-area {
  margin-top: 20px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.subjective-submit-area h5 {
  margin-bottom: 15px;
  color: #303133;
}

.preview-inline {
  margin-top: 15px;
}

.image-preview-inline {
  max-width: 100%;
  max-height: 400px;
  border-radius: 4px;
}

/* 图片预览样式 */
.preview-image {
  margin-top: 15px;
}

.preview-image p {
  margin-bottom: 5px;
  font-weight: bold;
}

.preview-image img {
  max-width: 100%;
  max-height: 400px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.clickable-image {
  cursor: pointer;
  transition: all 0.3s;
}

.clickable-image:hover {
  transform: scale(1.02);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 音频预览样式 */
.preview-audio {
  margin-top: 15px;
}

.preview-audio h6 {
  margin-bottom: 10px;
  color: #303133;
}

/* 图片预览对话框样式 */
.image-preview-dialog .el-dialog__body {
  padding: 0;
}

.image-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.large-image {
  max-width: 100%;
  max-height: 80vh;
  border-radius: 4px;
}

.submission-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #606266;
  font-size: 14px;
}

/* 加载样式 */
.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  font-size: 16px;
  color: #409eff;
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
.preview-image,
.submission-audio,
.preview-audio {
  margin-top: 15px;
}

.submission-image img,
.preview-image img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.submission-audio audio,
.preview-audio audio {
  max-width: 100%;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  padding: 10px;
  background-color: #f9f9f9;
}

.submission-image img:hover,
.preview-image img:hover {
  transform: scale(1.02);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.clickable-image {
  cursor: pointer;
}

.image-preview-dialog {
  .el-dialog__body {
    padding: 20px;
  }
}

.image-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.large-image {
  max-width: 100%;
  max-height: 600px;
  object-fit: contain;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
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

.auto-grading-result {
  background-color: #f0f9ff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 20px;
}

.auto-grading-result h5 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.auto-grading-result p {
  margin: 5px 0;
  color: #606266;
  font-size: 14px;
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
