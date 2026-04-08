<template>
  <div class="teenage-english-15-18">
    <h2>15-18岁 进阶英语</h2>
    <p class="subtitle">高级语法应用，学术英语训练，为考试和未来发展做准备</p>
    
    <!-- 学习模块标签页 -->
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="单词" name="words">
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>高级词汇学习</span>
              <el-tag type="success">进阶阶段</el-tag>
            </div>
          </template>
          <div class="search-bar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索单词..."
              clearable
              style="margin-bottom: 20px"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="word-list">
            <div v-for="word in filteredWords" :key="word.id" class="word-item">
              <div class="word-info">
                <h3>{{ word.word }}</h3>
                <p class="phonetic">{{ word.phonetic }}</p>
                <p class="meaning">{{ word.definition }}</p>
                <p class="chinese-meaning" v-if="word.chineseMeaning">{{ word.chineseMeaning }}</p>
                <p class="example">{{ word.example }}</p>
              </div>
              <div class="word-actions">
                <el-button type="primary" size="small" @click="playAudio(word.id)">播放发音</el-button>
                <el-button type="success" size="small" @click="addToList(word.id)">添加到学习列表</el-button>
              </div>
            </div>
            
            <!-- 分页组件 -->
            <div class="pagination" v-if="!searchKeyword && totalPages > 1">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="totalElements"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="口语" name="speaking">
        <!-- 口语视频 -->
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>口语视频学习</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in speakingVideos" :key="video.id" class="video-card" @click="handleVideoClick(video)">
              <div class="video-thumbnail">
                <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                  您的浏览器不支持视频播放
                </video>
              </div>
              <div class="video-info">
                <h4>{{ video.title }}</h4>
                <p>{{ video.description }}</p>
                <p class="video-type">类型: {{ video.type }}</p>
                <el-button type="primary" size="small" @click.stop="handleVideoClick(video)">保存到最近学习</el-button>
              </div>
            </div>
            <el-empty v-if="speakingVideos.length === 0" description="暂无口语视频" />
          </div>
        </el-card>
        
        <!-- 口语作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>口语作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in speakingHomework" :key="homework.id" class="homework-item">
              <div class="homework-info">
                <h4>{{ homework.title }}</h4>
                <p class="homework-content">{{ homework.content }}</p>
                <div v-if="homework.image" class="homework-image">
                  <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                </div>
                <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
                
                <!-- 已提交作业信息 -->
                <div v-if="getSubmissionForHomework(homework.id)" class="submission-info">
                  <el-divider />
                  <h5>我的提交：</h5>
                  <p class="submission-content">{{ getSubmissionForHomework(homework.id).content }}</p>
                  <div v-if="getSubmissionForHomework(homework.id).image" class="submission-image">
                    <img :src="`http://localhost:8080${getSubmissionForHomework(homework.id).image}`" alt="我提交的图片" />
                  </div>
                  <p class="submission-time">提交时间: {{ formatTime(getSubmissionForHomework(homework.id).submissionDate) }}</p>
                  
                  <!-- 批改结果 -->
                  <div v-if="getSubmissionForHomework(homework.id).status === 'graded'" class="grade-result">
                    <h5>批改结果：</h5>
                    <p class="score">分数：{{ getSubmissionForHomework(homework.id).score }}分</p>
                    <p class="feedback">评语：{{ getSubmissionForHomework(homework.id).feedback }}</p>
                  </div>
                </div>
              </div>
              <div class="homework-actions">
                <el-button 
                  v-if="!getSubmissionForHomework(homework.id)" 
                  type="primary" 
                  size="small" 
                  @click="submitHomework(homework.id)"
                >
                  提交作业
                </el-button>
                <el-tag v-else :type="getSubmissionForHomework(homework.id).status === 'graded' ? 'success' : 'info'" size="small">
                  {{ getSubmissionForHomework(homework.id).status === 'graded' ? '已批改' : '已提交' }}
                </el-tag>
              </div>
            </div>
            <el-empty v-if="speakingHomework.length === 0" description="暂无口语作业" />
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="阅读" name="reading">
        <!-- 阅读视频 -->
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>阅读视频学习</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in readingVideos" :key="video.id" class="video-card" @click="handleVideoClick(video)">
              <div class="video-thumbnail">
                <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                  您的浏览器不支持视频播放
                </video>
              </div>
              <div class="video-info">
                <h4>{{ video.title }}</h4>
                <p>{{ video.description }}</p>
                <p class="video-type">类型: {{ video.type }}</p>
                <el-button type="primary" size="small" @click.stop="handleVideoClick(video)">保存到最近学习</el-button>
              </div>
            </div>
            <el-empty v-if="readingVideos.length === 0" description="暂无阅读视频" />
          </div>
        </el-card>
        
        <!-- 阅读作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>阅读作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in readingHomework" :key="homework.id" class="homework-item">
              <div class="homework-info">
                <h4>{{ homework.title }}</h4>
                <p class="homework-content">{{ homework.content }}</p>
                <div v-if="homework.image" class="homework-image">
                  <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                </div>
                <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
              </div>
              <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
            </div>
            <el-empty v-if="readingHomework.length === 0" description="暂无阅读作业" />
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="听力" name="listening">
        <!-- 听力视频 -->
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>听力视频学习</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in listeningVideos" :key="video.id" class="video-card" @click="handleVideoClick(video)">
              <div class="video-thumbnail">
                <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                  您的浏览器不支持视频播放
                </video>
              </div>
              <div class="video-info">
                <h4>{{ video.title }}</h4>
                <p>{{ video.description }}</p>
                <p class="video-type">类型: {{ video.type }}</p>
                <el-button type="primary" size="small" @click.stop="handleVideoClick(video)">保存到最近学习</el-button>
              </div>
            </div>
            <el-empty v-if="listeningVideos.length === 0" description="暂无听力视频" />
          </div>
        </el-card>
        
        <!-- 听力作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>听力作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in listeningHomework" :key="homework.id" class="homework-item">
              <div class="homework-info">
                <h4>{{ homework.title }}</h4>
                <p class="homework-content">{{ homework.content }}</p>
                <div v-if="homework.image" class="homework-image">
                  <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                </div>
                <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
              </div>
              <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
            </div>
            <el-empty v-if="listeningHomework.length === 0" description="暂无听力作业" />
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="写作" name="writing">
        <!-- 写作视频 -->
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>写作视频学习</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in writingVideos" :key="video.id" class="video-card" @click="handleVideoClick(video)">
              <div class="video-thumbnail">
                <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                  您的浏览器不支持视频播放
                </video>
              </div>
              <div class="video-info">
                <h4>{{ video.title }}</h4>
                <p>{{ video.description }}</p>
                <p class="video-type">类型: {{ video.type }}</p>
                <el-button type="primary" size="small" @click.stop="handleVideoClick(video)">保存到最近学习</el-button>
              </div>
            </div>
            <el-empty v-if="writingVideos.length === 0" description="暂无写作视频" />
          </div>
        </el-card>
        
        <!-- 写作作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>写作作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in writingHomework" :key="homework.id" class="homework-item">
              <div class="homework-info">
                <h4>{{ homework.title }}</h4>
                <p class="homework-content">{{ homework.content }}</p>
                <div v-if="homework.image" class="homework-image">
                  <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                </div>
                <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
              </div>
              <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
            </div>
            <el-empty v-if="writingHomework.length === 0" description="暂无写作作业" />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 提交作业对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="提交作业"
      width="500px"
    >
      <el-form :model="submitForm" label-width="80px">
        <el-form-item label="作业标题">
          <el-input v-model="submitForm.title" disabled />
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input
            v-model="submitForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入作业内容"
          />
        </el-form-item>
        <el-form-item label="上传音频">
          <el-upload
            class="upload-demo"
            :http-request="customUpload"
            :file-list="fileList"
            :auto-upload="true"
            :show-file-list="false"
            accept="audio/*"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                只能上传MP3、WAV等音频文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="submitForm.audio">
          <div class="uploaded-audio">
            <audio :src="`http://localhost:8080${submitForm.audio}`" controls></audio>
            <el-button type="danger" size="small" @click="removeAudio">删除音频</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="submitDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSubmit" :loading="submitting">确定提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { api } from '../../api'
import { useWatchHistory } from '../../composables/useWatchHistory'

// 激活的标签页
const activeTab = ref('words')

// 最近学习功能
const { saveWatchHistory } = useWatchHistory()

// 处理视频点击，保存到最近学习
const handleVideoClick = (video: any) => {
  console.log('Saving video to watch history:', video)
  try {
    saveWatchHistory(video, 'teenage15-18')
    ElMessage.success('已添加到最近学习')
  } catch (error) {
    console.error('Failed to save watch history:', error)
    ElMessage.error('保存失败，请重试')
  }
}

// 单词数据 - 从后端API获取
const words = ref([])
const allWords = ref([]) // 存储所有单词用于搜索
const searchKeyword = ref('')

// 检查是否包含中文字符
const hasChinese = (text: string) => {
  return /[\u4e00-\u9fa5]/.test(text)
}

// 过滤后的单词
const filteredWords = computed(() => {
  if (!searchKeyword.value) {
    return words.value
  }
  const keyword = searchKeyword.value
  const wordsToSearch = allWords.value.length > 0 ? allWords.value : words.value
  if (hasChinese(keyword)) {
    // 输入中文 → 只搜索中文释义
    return wordsToSearch.filter((word: any) => 
      word.chineseMeaning && word.chineseMeaning.includes(keyword)
    )
  } else {
    // 输入英文 → 只搜索英文单词
    const lowerKeyword = keyword.toLowerCase()
    return wordsToSearch.filter((word: any) => 
      word.word.toLowerCase().includes(lowerKeyword)
    )
  }
})

// 分页相关变量
const currentPage = ref(1)

// 监听搜索关键词变化，重置页码到第一页
watch(searchKeyword, () => {
  currentPage.value = 1
})
const pageSize = ref(20)
const totalElements = ref(0)
const totalPages = ref(0)

// 视频数据
const speakingVideos = ref<any[]>([])
const readingVideos = ref<any[]>([])
const listeningVideos = ref<any[]>([])
const writingVideos = ref<any[]>([])

// 作业数据变量
const speakingHomework = ref<any[]>([])
const readingHomework = ref<any[]>([])
const listeningHomework = ref<any[]>([])
const writingHomework = ref<any[]>([])

// 学生作业提交
const mySubmissions = ref<any[]>([])

// 提交作业对话框状态
const submitDialogVisible = ref(false)
const submitForm = ref({ title: '', content: '', audio: '' })
const submitting = ref(false)
const currentHomeworkId = ref('')
const fileList = ref([])

// 从后端API获取所有单词（用于搜索）
const fetchAllWords = async () => {
  try {
    console.log('Fetching all Teenage 15-18 words for search...')
    const response = await fetch('http://localhost:8080/api/words/age-group/15')
    if (!response.ok) {
      throw new Error('Failed to fetch all Teenage 15-18 words')
    }
    const data = await response.json()
    const formattedWords = data.map((word: any) => ({
      id: word.id,
      word: word.word,
      phonetic: word.phonetic,
      definition: word.meaning,
      chineseMeaning: word.chineseMeaning,
      example: word.example
    }))
    allWords.value = formattedWords
    console.log('All Teenage 15-18 words loaded:', allWords.value.length)
  } catch (error) {
    console.error('Failed to fetch all Teenage 15-18 words:', error)
  }
}

// 从后端API获取单词数据（带分页）
const fetchWords = async () => {
  try {
    console.log('Fetching words from API...')
    const response = await fetch(`http://localhost:8080/api/words/age-group/15/page?page=${currentPage.value - 1}&size=${pageSize.value}`)
    console.log('Response status:', response.status)
    if (!response.ok) {
      throw new Error('Failed to fetch words with status: ' + response.status)
    }
    const data = await response.json()
    console.log('API response:', data)
    
    // 转换数据格式，确保使用definition字段
    const formattedWords = data.content.map((word: any) => ({
      id: word.id,
      word: word.word,
      phonetic: word.phonetic,
      definition: word.meaning, // 使用meaning字段作为definition
      chineseMeaning: word.chineseMeaning, // 中文释义
      example: word.example
    }))
    console.log('Formatted words:', formattedWords)
    words.value = formattedWords
    totalElements.value = data.totalElements
    totalPages.value = data.totalPages
    
    console.log('Words updated:', words.value)
    console.log('Total elements:', totalElements.value)
    console.log('Total pages:', totalPages.value)
  } catch (error) {
    console.error('Failed to fetch words:', error)
    // 显示错误信息给用户
    ElMessage.error('获取单词数据失败，请稍后重试')
  }
}

// 从后端API获取视频数据
const fetchVideos = async () => {
  try {
    // 调用后端API获取视频数据
    console.log('Fetching videos from API...')
    const response = await api.videos.getByCategory('青少年英语-15-18岁')
    console.log('API response:', response)
    console.log('Fetched videos from API:', response)
    // 检查视频URL
    if (response.length > 0) {
      console.log('First video URL:', response[0].videoUrl)
      console.log('Full video URL:', `http://localhost:8080${response[0].videoUrl}`)
    }
    
    // 按类型分类视频
    speakingVideos.value = response.filter((video: any) => video.type === '口语')
    readingVideos.value = response.filter((video: any) => video.type === '阅读')
    listeningVideos.value = response.filter((video: any) => video.type === '听力')
    writingVideos.value = response.filter((video: any) => video.type === '写作')
    console.log('Speaking videos:', speakingVideos.value)
  } catch (error) {
    console.error('Failed to fetch videos:', error)
    // 使用本地数据作为后备
    console.log('Using local video data')
  }
}

// 从后端API获取作业数据
const fetchHomework = async () => {
  try {
    // 调用后端API获取作业数据
    console.log('Fetching homework from API...')
    const response = await api.homeworks.getByCategory('青少年英语-15-18岁')
    console.log('API response:', response)
    
    // 按类型分类作业
    speakingHomework.value = response.filter((homework: any) => homework.type === '口语')
    readingHomework.value = response.filter((homework: any) => homework.type === '阅读')
    listeningHomework.value = response.filter((homework: any) => homework.type === '听力')
    writingHomework.value = response.filter((homework: any) => homework.type === '写作')
    console.log('Speaking homework:', speakingHomework.value)
    console.log('Reading homework:', readingHomework.value)
    console.log('Listening homework:', listeningHomework.value)
    console.log('Writing homework:', writingHomework.value)
  } catch (error) {
    console.error('Failed to fetch homework:', error)
    // 使用本地数据作为后备
    console.log('Using local homework data')
  }
}

// 获取学生自己的作业提交
const fetchMySubmissions = async () => {
  try {
    console.log('Fetching my submissions from API...')
    const response = await api.homeworks.getMySubmissions()
    mySubmissions.value = response
    console.log('My submissions:', mySubmissions.value)
  } catch (error) {
    console.error('Failed to fetch my submissions:', error)
  }
}

// 获取某个作业的提交记录
const getSubmissionForHomework = (homeworkId: any) => {
  return mySubmissions.value.find((sub: any) => sub.homework?.id === homeworkId)
}

// 页面加载时获取数据
onMounted(() => {
  fetchWords()
  fetchAllWords()
  fetchVideos()
  fetchHomework()
  fetchMySubmissions()
})

// 分页处理函数
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchWords()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  fetchWords()
}

// 播放发音
const playAudio = (wordId: string) => {
  const word = words.value.find(w => w.id === wordId)
  if (word && 'speechSynthesis' in window) {
    const utterance = new SpeechSynthesisUtterance(word.word)
    utterance.lang = 'en-US'
    utterance.rate = 0.8
    window.speechSynthesis.speak(utterance)
  } else {
    console.log('Browser does not support speech synthesis')
  }
}

// 添加到学习列表
const addToList = async (wordId: string) => {
  try {
    console.log('Adding word to learning list with id:', wordId);
    // 为了测试方便，使用固定的用户ID
    const userId = 1;
    const response = await fetch(`http://localhost:8080/api/word-learning-list/add?userId=${userId}&wordId=${wordId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    console.log('Response status:', response.status);
    const result = await response.text();
    console.log('Response result:', result);
    if (response.ok) {
      ElMessage.success(result);
    } else {
      ElMessage.error(result);
    }
  } catch (error) {
    console.error('Failed to add word to learning list:', error);
    ElMessage.error('添加失败，请稍后重试');
  }
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 合并所有作业
const getAllHomework = () => {
  return [...speakingHomework.value, ...readingHomework.value, ...listeningHomework.value, ...writingHomework.value]
}

// 自定义上传方法
const customUpload = async (options: any) => {
  try {
    const response = await api.upload.audio(options.file)
    if (response.success) {
      submitForm.value.audio = response.audioUrl
      ElMessage.success('音频上传成功')
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('音频上传失败，请重试')
    console.error('Upload error:', error)
  }
}

// 删除音频
const removeAudio = () => {
  submitForm.value.audio = ''
  fileList.value = []
  ElMessage.success('音频已删除')
}

// 打开提交作业对话框
const submitHomework = (homeworkId: string) => {
  console.log('Submitting homework:', homeworkId)
  currentHomeworkId.value = homeworkId
  // 查找作业信息
  const allHomework = getAllHomework()
  const homework = allHomework.find(h => h.id === homeworkId)
  if (homework) {
    submitForm.value.title = homework.title
    submitForm.value.content = ''
    submitForm.value.audio = ''
    fileList.value = []
    submitDialogVisible.value = true
  }
}

// 确认提交作业
const confirmSubmit = async () => {
  if (!submitForm.value.content) {
    ElMessage.error('请输入作业内容')
    return
  }
  
  if (!currentHomeworkId.value) {
    ElMessage.error('作业ID不能为空')
    return
  }
  
  try {
    submitting.value = true
    console.log('Submitting homework with ID:', currentHomeworkId.value)
    // 调用后端API提交作业
    const response = await api.homeworks.submit({
      homeworkId: currentHomeworkId.value,
      content: submitForm.value.content,
      audio: submitForm.value.audio
    })
    console.log('作业提交成功:', response)
    ElMessage.success('作业提交成功')
    submitDialogVisible.value = false
  } catch (error: any) {
    console.error('作业提交失败:', error)
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.teenage-english-15-18 {
  padding: 20px 0;
}

.teenage-english-15-18 h2 {
  margin-bottom: 10px;
  color: #303133;
  font-size: 28px;
}

.subtitle {
  color: #606266;
  margin-bottom: 30px;
  font-size: 16px;
}

.module-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.word-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.word-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.word-info h3 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: 600;
}

.phonetic {
  margin: 0 0 5px 0;
  color: #909399;
  font-size: 14px;
}

.meaning {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}

.chinese-meaning {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #67c23a;
  font-weight: bold;
}

.example {
  margin: 0;
  font-size: 14px;
  color: #606266;
  font-style: italic;
}

.word-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.homework-actions {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.submission-info {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #e4e7ed;
}

.submission-info h5 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
  font-weight: 600;
}

.submission-content {
  margin: 0 0 10px 0;
  color: #303133;
  line-height: 1.6;
}

.submission-image {
  margin: 10px 0;
  max-width: 300px;
}

.submission-image img {
  width: 100%;
  border-radius: 4px;
}

.submission-time {
  margin: 10px 0;
  color: #909399;
  font-size: 13px;
}

.grade-result {
  margin-top: 15px;
  padding: 15px;
  background-color: #f0f9eb;
  border-radius: 4px;
  border: 1px solid #e1f3d8;
}

.grade-result .score {
  color: #67C23A;
  font-weight: 600;
  font-size: 16px;
  margin: 5px 0;
}

.grade-result .feedback {
  color: #606266;
  margin: 5px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 10px 0;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.video-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.video-thumbnail {
  position: relative;
  height: 180px;
}

.video-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 48px;
}

.video-info {
  padding: 15px;
}

.video-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.video-info p {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 14px;
}

.video-type {
  color: #409EFF;
  font-size: 12px;
  margin-top: 10px;
}

.homework-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.homework-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  background: #f5f7fa;
}

.homework-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 18px;
}

.homework-content {
  margin: 0 0 15px 0;
  color: #606266;
  line-height: 1.5;
}

.homework-image {
  margin: 15px 0;
  max-width: 300px;
}

.homework-image img {
  width: 100%;
  border-radius: 4px;
}

.homework-time {
  margin: 10px 0 0 0;
  color: #909399;
  font-size: 14px;
}

.homework-type {
  color: #409EFF;
  font-size: 12px;
  margin-top: 5px;
}

.uploaded-audio {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.uploaded-audio audio {
  max-width: 300px;
  border-radius: 4px;
}

@media screen and (max-width: 768px) {
  .word-item {
    flex-direction: column;
    gap: 10px;
  }
  
  .word-actions {
    align-self: flex-end;
  }
  
  .pagination {
    margin-top: 20px;
  }
  
  .video-grid {
    grid-template-columns: 1fr;
  }
  
  .video-thumbnail {
    height: 150px;
  }
  
  .homework-image {
    max-width: 100%;
  }
  
  .uploaded-image img {
    max-width: 150px;
  }
}
</style>
