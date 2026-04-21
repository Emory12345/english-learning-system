<template>
  <div class="teenage-english-3-6">
    <h2>3-6岁 启蒙英语</h2>
    <p class="subtitle">寓教于乐，让孩子在快乐中学习英语</p>
    
    <!-- 学习模块标签页 -->
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="单词认知" name="words">
        <el-card class="module-card apple-design">
          <template #header>
            <div class="card-header">
              <span>基础单词认知</span>
              <el-tag type="primary" effect="light">启蒙阶段</el-tag>
            </div>
          </template>
          
          <!-- 分类筛选器 -->
          <div class="category-filter">
            <el-radio-group v-model="selectedCategory" size="large">
              <el-radio-button v-for="category in categories" :key="category.value" :label="category.value">
                {{ category.label }}
              </el-radio-button>
            </el-radio-group>
          </div>
          
          <div class="word-slider-container apple-design">
            <div class="word-scroll-container">
              <div class="word-grid apple-design">
                <div v-for="word in filteredWords" :key="word.id" class="word-card apple-design" @click="playAudio(word.id)" :style="{ backgroundImage: `url(${getImageUrl(word.word)})` }">
                  <div class="word-info apple-design">
                    <h3>{{ word.word }}</h3>
                    <p class="phonetic">{{ word.phonetic }}</p>
                    <p class="meaning">{{ word.meaning }}</p>
                  </div>
                  <div class="play-icon apple-design">
                    <el-icon><VideoPlay /></el-icon>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="动画教学" name="videos">
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>动画互动教学</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in animationVideos" :key="video.id" class="video-card" @click="handleVideoClick(video)">
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
            <el-empty v-if="animationVideos.length === 0" description="暂无动画教学视频" />
          </div>
        </el-card>
        
        <!-- 动画教学作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>动画教学作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in animationHomework" :key="homework.id" class="homework-item">
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
            <el-empty v-if="animationHomework.length === 0" description="暂无动画教学作业" />
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="简单对话" name="dialogue">
        <!-- 简单对话视频 -->
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>对话视频学习</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in dialogueVideos" :key="video.id" class="video-card" @click="handleVideoClick(video)">
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
            <el-empty v-if="dialogueVideos.length === 0" description="暂无对话视频" />
          </div>
        </el-card>
        
        <!-- 简单对话作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>简单对话作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in dialogueHomework" :key="homework.id" class="homework-item">
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
            <el-empty v-if="dialogueHomework.length === 0" description="暂无简单对话作业" />
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
        <el-form-item label="上传图片">
          <el-upload
            class="upload-demo"
            :http-request="customUpload"
            :file-list="fileList"
            :auto-upload="true"
            :show-file-list="false"
            accept="image/*"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                只能上传JPG、PNG等图片文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="submitForm.image">
          <div class="uploaded-image">
            <img :src="`http://localhost:8080${submitForm.image}`" alt="已上传图片" />
            <el-button type="danger" size="small" @click="removeImage">删除图片</el-button>
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
import { ref, onMounted, computed } from 'vue'
import { VideoPlay, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { api } from '../../api'
import { ElMessage } from 'element-plus'
import { useWatchHistory } from '../../composables/useWatchHistory'

const wordGrid = ref<HTMLElement | null>(null)

// 激活的标签页
const activeTab = ref('words')

// 最近学习功能
const { saveWatchHistory } = useWatchHistory()

// 处理视频点击，保存到最近学习
const handleVideoClick = (video: any) => {
  console.log('Saving video to watch history:', video)
  try {
    saveWatchHistory(video, 'teenage3-6')
    ElMessage.success('已添加到最近学习')
  } catch (error) {
    console.error('Failed to save watch history:', error)
    ElMessage.error('保存失败，请重试')
  }
}

// 分类选项
const categories = ref([
  { value: 'all', label: '全部' },
  { value: 'animal', label: '动物' },
  { value: 'fruit', label: '水果' },
  { value: 'number', label: '数字' },
  { value: 'color', label: '颜色' },
  { value: 'nature', label: '自然' }
])

// 当前选中的分类
const selectedCategory = ref('all')

// 单词数据 - 使用WordService中的单词列表
const words = ref([
  { id: '1', word: 'apple', phonetic: '/ˈæpl/', meaning: '苹果', image: '@/assets/images/apple.jpg', category: 'fruit' },
  { id: '2', word: 'banana', phonetic: '/bəˈnɑːnə/', meaning: '香蕉', image: '@/assets/images/banana.jpg', category: 'fruit' },
  { id: '3', word: 'cat', phonetic: '/kæt/', meaning: '猫', image: '@/assets/images/cat.jpg', category: 'animal' },
  { id: '4', word: 'dog', phonetic: '/dɒɡ/', meaning: '狗', image: '@/assets/images/dog.jpg', category: 'animal' },
  { id: '5', word: 'sun', phonetic: '/sʌn/', meaning: '太阳', image: '@/assets/images/sun.jpg', category: 'nature' },
  { id: '6', word: 'moon', phonetic: '/muːn/', meaning: '月亮', image: '@/assets/images/moon.jpg', category: 'nature' },
  { id: '7', word: 'bird', phonetic: '/bɜːd/', meaning: '鸟', image: '@/assets/images/bird.jpg', category: 'animal' },
  { id: '8', word: 'fish', phonetic: '/fɪʃ/', meaning: '鱼', image: '@/assets/images/fish.jpg', category: 'animal' },
  { id: '9', word: 'elephant', phonetic: '/ˈelɪfənt/', meaning: '大象', image: '@/assets/images/elephant.jpg', category: 'animal' },
  { id: '10', word: 'lion', phonetic: '/ˈlaɪən/', meaning: '狮子', image: '@/assets/images/lion.jpg', category: 'animal' },
  { id: '11', word: 'tiger', phonetic: '/ˈtaɪɡə/', meaning: '老虎', image: '@/assets/images/tiger.jpg', category: 'animal' },
  { id: '12', word: 'monkey', phonetic: '/ˈmʌŋki/', meaning: '猴子', image: '@/assets/images/monkey.jpg', category: 'animal' },
  { id: '13', word: 'rabbit', phonetic: '/ˈræbɪt/', meaning: '兔子', image: '@/assets/images/rabbit.jpg', category: 'animal' },
  { id: '14', word: 'mouse', phonetic: '/maʊs/', meaning: '老鼠', image: '@/assets/images/mouse.jpg', category: 'animal' },
  { id: '15', word: 'horse', phonetic: '/hɔːs/', meaning: '马', image: '@/assets/images/horse.jpg', category: 'animal' },
  { id: '16', word: 'cow', phonetic: '/kaʊ/', meaning: '奶牛', image: '@/assets/images/cow.jpg', category: 'animal' },
  { id: '17', word: 'sheep', phonetic: '/ʃiːp/', meaning: '绵羊', image: '@/assets/images/sheep.jpg', category: 'animal' },
  { id: '18', word: 'pig', phonetic: '/pɪɡ/', meaning: '猪', image: '@/assets/images/pig.jpg', category: 'animal' },
  { id: '19', word: 'duck', phonetic: '/dʌk/', meaning: '鸭子', image: '@/assets/images/duck.jpg', category: 'animal' },
  { id: '20', word: 'chicken', phonetic: '/ˈtʃɪkɪn/', meaning: '鸡', image: '@/assets/images/chicken.jpg', category: 'animal' },
  { id: '51', word: 'bear', phonetic: '/beər/', meaning: '熊', image: '@/assets/images/bear.jpg', category: 'animal' },
  { id: '21', word: 'orange', phonetic: '/ˈɒrɪndʒ/', meaning: '橙子', image: '@/assets/images/orange.jpg', category: 'fruit' },
  { id: '22', word: 'grape', phonetic: '/ɡreɪp/', meaning: '葡萄', image: '@/assets/images/grape.jpg', category: 'fruit' },
  { id: '23', word: 'strawberry', phonetic: '/ˈstrɔːbəri/', meaning: '草莓', image: '@/assets/images/strawberry.jpg', category: 'fruit' },
  { id: '24', word: 'watermelon', phonetic: '/ˈwɔːtəmelən/', meaning: '西瓜', image: '@/assets/images/watermelon.jpg', category: 'fruit' },
  { id: '25', word: 'pineapple', phonetic: '/ˈpaɪnæpl/', meaning: '菠萝', image: '@/assets/images/pineapple.jpg', category: 'fruit' },
  { id: '26', word: 'peach', phonetic: '/piːtʃ/', meaning: '桃子', image: '@/assets/images/peach.jpg', category: 'fruit' },
  { id: '27', word: 'pear', phonetic: '/peə/', meaning: '梨', image: '@/assets/images/pear.jpg', category: 'fruit' },
  { id: '28', word: 'lemon', phonetic: '/ˈlemən/', meaning: '柠檬', image: '@/assets/images/lemon.jpg', category: 'fruit' },
  { id: '29', word: 'cherry', phonetic: '/ˈtʃeri/', meaning: '樱桃', image: '@/assets/images/cherry.jpg', category: 'fruit' },
  { id: '30', word: 'mango', phonetic: '/ˈmæŋɡəʊ/', meaning: '芒果', image: '@/assets/images/mango.jpg', category: 'fruit' },
  { id: '31', word: 'red', phonetic: '/red/', meaning: '红色', image: '@/assets/images/red.jpg', category: 'color' },
  { id: '32', word: 'blue', phonetic: '/bluː/', meaning: '蓝色', image: '@/assets/images/blue.jpg', category: 'color' },
  { id: '33', word: 'green', phonetic: '/ɡriːn/', meaning: '绿色', image: '@/assets/images/green.jpg', category: 'color' },
  { id: '34', word: 'yellow', phonetic: '/ˈjeləʊ/', meaning: '黄色', image: '@/assets/images/yellow.jpg', category: 'color' },
  { id: '35', word: 'purple', phonetic: '/ˈpɜːpl/', meaning: '紫色', image: '@/assets/images/purple.jpg', category: 'color' },
  { id: '36', word: 'pink', phonetic: '/pɪŋk/', meaning: '粉色', image: '@/assets/images/pink.jpg', category: 'color' },
  { id: '37', word: 'black', phonetic: '/blæk/', meaning: '黑色', image: '@/assets/images/black.jpg', category: 'color' },
  { id: '38', word: 'white', phonetic: '/waɪt/', meaning: '白色', image: '@/assets/images/white.jpg', category: 'color' },
  { id: '39', word: 'brown', phonetic: '/braʊn/', meaning: '棕色', image: '@/assets/images/brown.jpg', category: 'color' },
  { id: '40', word: 'gray', phonetic: '/ɡreɪ/', meaning: '灰色', image: '@/assets/images/gray.jpg', category: 'color' },
  { id: '41', word: 'one', phonetic: '/wʌn/', meaning: '一', image: '@/assets/images/one.jpg', category: 'number' },
  { id: '42', word: 'two', phonetic: '/tuː/', meaning: '二', image: '@/assets/images/two.jpg', category: 'number' },
  { id: '43', word: 'three', phonetic: '/θriː/', meaning: '三', image: '@/assets/images/three.jpg', category: 'number' },
  { id: '44', word: 'four', phonetic: '/fɔː/', meaning: '四', image: '@/assets/images/four.jpg', category: 'number' },
  { id: '45', word: 'five', phonetic: '/faɪv/', meaning: '五', image: '@/assets/images/five.jpg', category: 'number' },
  { id: '46', word: 'six', phonetic: '/sɪks/', meaning: '六', image: '@/assets/images/six.jpg', category: 'number' },
  { id: '47', word: 'seven', phonetic: '/ˈsevn/', meaning: '七', image: '@/assets/images/seven.jpg', category: 'number' },
  { id: '48', word: 'eight', phonetic: '/eɪt/', meaning: '八', image: '@/assets/images/eight.jpg', category: 'number' },
  { id: '49', word: 'nine', phonetic: '/naɪn/', meaning: '九', image: '@/assets/images/nine.jpg', category: 'number' },
  { id: '50', word: 'ten', phonetic: '/ten/', meaning: '十', image: '@/assets/images/ten.jpg', category: 'number' }
])

// 根据分类筛选单词
const filteredWords = computed(() => {
  if (selectedCategory.value === 'all') {
    return words.value
  }
  return words.value.filter(word => word.category === selectedCategory.value)
})

// 从后端API获取单词数据
const fetchWords = async () => {
  try {
    // 暂时禁用API调用，直接使用本地数据
    console.log('Using local word data')
  } catch (error) {
    console.error('Failed to fetch words:', error)
    // 使用本地数据作为后备
    console.log('Using local word data')
  }
}

// 页面加载时获取单词数据
fetchWords()



// 视频数据
const videos = ref([])
const animationVideos = ref([])
const dialogueVideos = ref([])

// 作业数据
const animationHomework = ref([])
const dialogueHomework = ref([])

// 学生作业提交
const mySubmissions = ref<any[]>([])



// 从后端API获取视频数据
const fetchVideos = async () => {
  try {
    // 调用后端API获取视频数据
    console.log('Fetching videos from API...')
    const response = await api.videos.getByCategory('青少年英语-3-6岁')
    console.log('API response:', response)
    videos.value = response
    console.log('Fetched videos from API:', response)
    // 检查视频URL
    if (response.length > 0) {
      console.log('First video URL:', response[0].videoUrl)
      console.log('Full video URL:', `http://localhost:8080${response[0].videoUrl}`)
    }
    
    // 按类型分类视频
    animationVideos.value = response.filter((video: any) => video.type === '动画教学')
    dialogueVideos.value = response.filter((video: any) => video.type === '简单对话')
    console.log('Animation videos:', animationVideos.value)
    console.log('Dialogue videos:', dialogueVideos.value)
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
    const response = await api.homeworks.getByCategory('青少年英语-3-6岁')
    console.log('API response:', response)
    
    // 按类型分类作业
    animationHomework.value = response.filter((homework: any) => homework.type === '动画教学')
    dialogueHomework.value = response.filter((homework: any) => homework.type === '简单对话')
    console.log('Animation homework:', animationHomework.value)
    console.log('Dialogue homework:', dialogueHomework.value)
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
  fetchVideos()
  fetchHomework()
  fetchMySubmissions()
  // 获取滚动容器
  setTimeout(() => {
    if (!wordGrid.value) {
      wordGrid.value = document.querySelector('.word-grid') as HTMLElement
    }
  }, 100)
})

// 滚动功能
const scrollLeft = () => {
  if (wordGrid.value) {
    const currentTransform = wordGrid.value.style.transform || 'translateX(0)'
    const currentX = parseInt(currentTransform.replace('translateX(', '').replace(')', '')) || 0
    wordGrid.value.style.transform = `translateX(${currentX + 300}px)`
  }
}

const scrollRight = () => {
  if (wordGrid.value) {
    const currentTransform = wordGrid.value.style.transform || 'translateX(0)'
    const currentX = parseInt(currentTransform.replace('translateX(', '').replace(')', '')) || 0
    wordGrid.value.style.transform = `translateX(${currentX - 300}px)`
  }
}

// 获取图片URL
const getImageUrl = (word: string) => {
  try {
    // 尝试导入图片
    const imageUrl = new URL(`../../assets/images/${word}.jpg`, import.meta.url)
    return imageUrl.href
  } catch (error) {
    // 如果图片不存在，返回一个默认图片或空字符串
    console.warn(`Image not found for word: ${word}`)
    return ''
  }
}

// 播放单词发音
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



// 播放视频
const playVideo = (videoId: string) => {
  console.log('Playing video:', videoId)
}



// 作业数据
const homework = ref([])

// 重新获取作业数据
const refreshHomework = () => {
  fetchHomework()
}

// 合并所有作业
const getAllHomework = () => {
  return [...animationHomework.value, ...dialogueHomework.value]
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 提交作业对话框
const submitDialogVisible = ref(false)
const submitForm = ref({
  title: '',
  content: '',
  image: ''
})
const submitting = ref(false)
const currentHomeworkId = ref('')
const fileList = ref([])

// 自定义上传方法
const customUpload = async (options: any) => {
  try {
    const response = await api.upload.image(options.file)
    if (response.success) {
      submitForm.value.image = response.imageUrl
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('图片上传失败，请重试')
    console.error('Upload error:', error)
  }
}

// 删除图片
const removeImage = () => {
  submitForm.value.image = ''
  fileList.value = []
  ElMessage.success('图片已删除')
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
    submitForm.value.image = ''
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
      image: submitForm.value.image
    })
    console.log('作业提交成功:', response)
    ElMessage.success('作业提交成功')
    submitDialogVisible.value = false
    // 重新获取作业提交数据，以便显示最新的提交
    await fetchMySubmissions()
  } catch (error: any) {
    console.error('作业提交失败:', error)
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.teenage-english-3-6 {
  padding: 20px 0;
}

.teenage-english-3-6 h2 {
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

/* Apple Design 风格样式 */
.apple-design {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.word-scroll-container {
  width: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  max-width: 100%;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.word-scroll-container::-webkit-scrollbar {
  display: none;
}

.category-filter {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
}

.word-slider-container.apple-design {
  position: relative;
  overflow: hidden;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  padding: 20px;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.word-scroll-container {
  width: 100%;
  max-width: 100%;
  padding: 0;
}

.word-grid.apple-design {
  display: flex;
  gap: 25px;
  width: 100%;
  padding: 20px;
  transition: transform 0.3s ease;
  flex-wrap: wrap;
  justify-content: space-between;
}

.word-card.apple-design {
  border: none;
  border-radius: 16px;
  padding: 60px 25px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  width: calc(33.333% - 17px);
  height: 250px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  z-index: 1;
  box-sizing: border-box;
}



.word-card.apple-design::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0.2), rgba(0, 0, 0, 0.4));
  z-index: 1;
}

.word-card.apple-design:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.word-info.apple-design {
  position: relative;
  z-index: 2;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.word-info.apple-design h3 {
  font-size: 36px;
  font-weight: 600;
  margin: 0 0 12px 0;
  letter-spacing: 0.5px;
}

.word-info.apple-design .phonetic {
  font-size: 20px;
  margin: 0 0 12px 0;
  opacity: 0.9;
}

.word-info.apple-design .meaning {
  font-size: 24px;
  font-weight: 500;
  margin: 0;
}

.play-icon.apple-design {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #007aff;
  z-index: 3;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.play-icon.apple-design:hover {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 1);
}

.module-card.apple-design {
  border: none;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  background-color: #ffffff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background-color: #ffffff;
  border-bottom: 1px solid #f0f0f0;
}

.card-header span {
  font-size: 18px;
  font-weight: 600;
  color: #1c1c1e;
}

.el-tabs.el-tabs--border-card {
  border: none;
  box-shadow: none;
}

.el-tabs__header {
  margin-bottom: 20px;
}

.el-tab-pane {
  padding: 0;
}

.word-card:hover {
  border-color: #409EFF;
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(64, 158, 255, 0.3);
}

.word-image {
  width: 100px;
  height: 100px;
  margin: 0 auto 15px;
  border-radius: 50%;
  overflow: hidden;
}

.word-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.word-info h3 {
  margin: 0 0 5px 0;
  font-size: 24px;
  color: #303133;
}

.phonetic {
  margin: 0 0 5px 0;
  color: #909399;
  font-size: 14px;
}

.meaning {
  margin: 0;
  font-size: 16px;
  color: #67c23a;
  font-weight: 600;
}

.play-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 30px;
  height: 30px;
  background: #409EFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
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

.dialogue-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dialogue-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
}

.dialogue-scene h4 {
  margin: 0 0 15px 0;
  color: #409EFF;
  font-size: 18px;
}

.dialogue-content {
  margin-bottom: 15px;
}

.dialogue-line {
  padding: 8px 0;
  border-bottom: 1px dashed #e4e7ed;
}

.dialogue-line:last-child {
  border-bottom: none;
}

.speaker {
  font-weight: bold;
  color: #409EFF;
  margin-right: 10px;
}

.english {
  color: #303133;
  margin-right: 10px;
}

.chinese {
  color: #909399;
  font-size: 14px;
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

.uploaded-image {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.uploaded-image img {
  max-width: 200px;
  max-height: 150px;
  border-radius: 4px;
}

@media screen and (max-width: 768px) {
  .word-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .video-grid {
    grid-template-columns: 1fr;
  }
  
  .homework-image {
    max-width: 100%;
  }
  
  .song-item {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .uploaded-image img {
    max-width: 150px;
  }
}
</style>