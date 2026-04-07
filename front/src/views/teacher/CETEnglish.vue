<template>
  <div class="teacher-course">
    <h2>四六级英语</h2>
    
    <!-- 视频标签页 -->
    <el-card class="video-card">
      <template #header>
        <div class="card-header">
          <span>发布教学视频</span>
        </div>
      </template>
      <el-tabs v-model="activeVideoTab">
        <el-tab-pane label="四级" name="cet4">
          <el-tabs v-model="activeCet4VideoTab">
            <el-tab-pane label="口语" name="speaking">
              <VideoUploadForm 
                category="四级英语" 
                type="口语" 
                @upload-success="handleUploadSuccess"
              />
            </el-tab-pane>
            <el-tab-pane label="阅读" name="reading">
              <VideoUploadForm 
                category="四级英语" 
                type="阅读" 
                @upload-success="handleUploadSuccess"
              />
            </el-tab-pane>
            <el-tab-pane label="听力" name="listening">
              <VideoUploadForm 
                category="四级英语" 
                type="听力" 
                @upload-success="handleUploadSuccess"
              />
            </el-tab-pane>
            <el-tab-pane label="写作" name="writing">
              <VideoUploadForm 
                category="四级英语" 
                type="写作" 
                @upload-success="handleUploadSuccess"
              />
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>
        <el-tab-pane label="六级" name="cet6">
          <el-tabs v-model="activeCet6VideoTab">
            <el-tab-pane label="口语" name="speaking">
              <VideoUploadForm 
                category="六级英语" 
                type="口语" 
                @upload-success="handleUploadSuccess"
              />
            </el-tab-pane>
            <el-tab-pane label="阅读" name="reading">
              <VideoUploadForm 
                category="六级英语" 
                type="阅读" 
                @upload-success="handleUploadSuccess"
              />
            </el-tab-pane>
            <el-tab-pane label="听力" name="listening">
              <VideoUploadForm 
                category="六级英语" 
                type="听力" 
                @upload-success="handleUploadSuccess"
              />
            </el-tab-pane>
            <el-tab-pane label="写作" name="writing">
              <VideoUploadForm 
                category="六级英语" 
                type="写作" 
                @upload-success="handleUploadSuccess"
              />
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 已发布的视频列表 -->
    <el-card class="videos-list-card">
      <template #header>
        <div class="card-header">
          <span>已发布的视频</span>
        </div>
      </template>
      <div class="videos-list">
        <div v-for="video in videos" :key="video.id" class="video-item">
          <div class="video-info">
            <h4>{{ video.title }}</h4>
            <p class="video-meta">
              <el-tag size="small">{{ video.type }}</el-tag>
              <span class="video-time">{{ formatTime(video.createdAt) }}</span>
            </p>
          </div>
          <div class="video-actions">
            <el-button type="primary" size="small" @click="playVideo(video)">播放</el-button>
            <el-button type="danger" size="small" @click="deleteVideo(video.id)">删除</el-button>
          </div>
        </div>
        <el-empty v-if="videos.length === 0" description="暂无视频" />
      </div>
    </el-card>

    <!-- 发布作业 -->
    <el-card class="homework-card">
      <template #header>
        <div class="card-header">
          <span>发布作业</span>
        </div>
      </template>
      <el-tabs v-model="activeHomeworkTab">
        <el-tab-pane label="四级" name="cet4">
          <el-tabs v-model="activeCet4HomeworkTab">
            <el-tab-pane label="口语" name="speaking">
              <el-form :model="homeworkForm.cet4.speaking" label-width="100px">
                <el-form-item label="作业标题">
                  <el-input v-model="homeworkForm.cet4.speaking.title" placeholder="请输入作业标题" />
                </el-form-item>
                <el-form-item label="作业内容">
                  <el-input 
                    v-model="homeworkForm.cet4.speaking.content" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="请输入作业内容"
                  />
                </el-form-item>
                <el-form-item label="上传图片">
                  <div class="image-upload">
                    <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      :on-change="(file) => handleImageChange(file, 'cet4', 'speaking')"
                      accept="image/*"
                      list-type="picture-card"
                    >
                      <img v-if="homeworkForm.cet4.speaking.image" :src="getImageUrl(homeworkForm.cet4.speaking.image)" class="image-preview" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                    <div v-if="homeworkForm.cet4.speaking.image" class="image-actions">
                      <el-button type="danger" size="small" @click="removeImage('cet4', 'speaking')">移除图片</el-button>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="publishHomework('cet4', 'speaking')" :loading="publishing">发布作业</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="阅读" name="reading">
              <el-form :model="homeworkForm.cet4.reading" label-width="100px">
                <el-form-item label="作业标题">
                  <el-input v-model="homeworkForm.cet4.reading.title" placeholder="请输入作业标题" />
                </el-form-item>
                <el-form-item label="作业内容">
                  <el-input 
                    v-model="homeworkForm.cet4.reading.content" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="请输入作业内容"
                  />
                </el-form-item>
                <el-form-item label="上传图片">
                  <div class="image-upload">
                    <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      :on-change="(file) => handleImageChange(file, 'cet4', 'reading')"
                      accept="image/*"
                      list-type="picture-card"
                    >
                      <img v-if="homeworkForm.cet4.reading.image" :src="getImageUrl(homeworkForm.cet4.reading.image)" class="image-preview" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                    <div v-if="homeworkForm.cet4.reading.image" class="image-actions">
                      <el-button type="danger" size="small" @click="removeImage('cet4', 'reading')">移除图片</el-button>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="publishHomework('cet4', 'reading')" :loading="publishing">发布作业</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="听力" name="listening">
              <el-form :model="homeworkForm.cet4.listening" label-width="100px">
                <el-form-item label="作业标题">
                  <el-input v-model="homeworkForm.cet4.listening.title" placeholder="请输入作业标题" />
                </el-form-item>
                <el-form-item label="作业内容">
                  <el-input 
                    v-model="homeworkForm.cet4.listening.content" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="请输入作业内容"
                  />
                </el-form-item>
                <el-form-item label="上传图片">
                  <div class="image-upload">
                    <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      :on-change="(file) => handleImageChange(file, 'cet4', 'listening')"
                      accept="image/*"
                      list-type="picture-card"
                    >
                      <img v-if="homeworkForm.cet4.listening.image" :src="getImageUrl(homeworkForm.cet4.listening.image)" class="image-preview" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                    <div v-if="homeworkForm.cet4.listening.image" class="image-actions">
                      <el-button type="danger" size="small" @click="removeImage('cet4', 'listening')">移除图片</el-button>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="publishHomework('cet4', 'listening')" :loading="publishing">发布作业</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="写作" name="writing">
              <el-form :model="homeworkForm.cet4.writing" label-width="100px">
                <el-form-item label="作业标题">
                  <el-input v-model="homeworkForm.cet4.writing.title" placeholder="请输入作业标题" />
                </el-form-item>
                <el-form-item label="作业内容">
                  <el-input 
                    v-model="homeworkForm.cet4.writing.content" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="请输入作业内容"
                  />
                </el-form-item>
                <el-form-item label="上传图片">
                  <div class="image-upload">
                    <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      :on-change="(file) => handleImageChange(file, 'cet4', 'writing')"
                      accept="image/*"
                      list-type="picture-card"
                    >
                      <img v-if="homeworkForm.cet4.writing.image" :src="getImageUrl(homeworkForm.cet4.writing.image)" class="image-preview" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                    <div v-if="homeworkForm.cet4.writing.image" class="image-actions">
                      <el-button type="danger" size="small" @click="removeImage('cet4', 'writing')">移除图片</el-button>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="publishHomework('cet4', 'writing')" :loading="publishing">发布作业</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>
        <el-tab-pane label="六级" name="cet6">
          <el-tabs v-model="activeCet6HomeworkTab">
            <el-tab-pane label="口语" name="speaking">
              <el-form :model="homeworkForm.cet6.speaking" label-width="100px">
                <el-form-item label="作业标题">
                  <el-input v-model="homeworkForm.cet6.speaking.title" placeholder="请输入作业标题" />
                </el-form-item>
                <el-form-item label="作业内容">
                  <el-input 
                    v-model="homeworkForm.cet6.speaking.content" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="请输入作业内容"
                  />
                </el-form-item>
                <el-form-item label="上传图片">
                  <div class="image-upload">
                    <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      :on-change="(file) => handleImageChange(file, 'cet6', 'speaking')"
                      accept="image/*"
                      list-type="picture-card"
                    >
                      <img v-if="homeworkForm.cet6.speaking.image" :src="getImageUrl(homeworkForm.cet6.speaking.image)" class="image-preview" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                    <div v-if="homeworkForm.cet6.speaking.image" class="image-actions">
                      <el-button type="danger" size="small" @click="removeImage('cet6', 'speaking')">移除图片</el-button>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="publishHomework('cet6', 'speaking')" :loading="publishing">发布作业</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="阅读" name="reading">
              <el-form :model="homeworkForm.cet6.reading" label-width="100px">
                <el-form-item label="作业标题">
                  <el-input v-model="homeworkForm.cet6.reading.title" placeholder="请输入作业标题" />
                </el-form-item>
                <el-form-item label="作业内容">
                  <el-input 
                    v-model="homeworkForm.cet6.reading.content" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="请输入作业内容"
                  />
                </el-form-item>
                <el-form-item label="上传图片">
                  <div class="image-upload">
                    <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      :on-change="(file) => handleImageChange(file, 'cet6', 'reading')"
                      accept="image/*"
                      list-type="picture-card"
                    >
                      <img v-if="homeworkForm.cet6.reading.image" :src="getImageUrl(homeworkForm.cet6.reading.image)" class="image-preview" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                    <div v-if="homeworkForm.cet6.reading.image" class="image-actions">
                      <el-button type="danger" size="small" @click="removeImage('cet6', 'reading')">移除图片</el-button>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="publishHomework('cet6', 'reading')" :loading="publishing">发布作业</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="听力" name="listening">
              <el-form :model="homeworkForm.cet6.listening" label-width="100px">
                <el-form-item label="作业标题">
                  <el-input v-model="homeworkForm.cet6.listening.title" placeholder="请输入作业标题" />
                </el-form-item>
                <el-form-item label="作业内容">
                  <el-input 
                    v-model="homeworkForm.cet6.listening.content" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="请输入作业内容"
                  />
                </el-form-item>
                <el-form-item label="上传图片">
                  <div class="image-upload">
                    <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      :on-change="(file) => handleImageChange(file, 'cet6', 'listening')"
                      accept="image/*"
                      list-type="picture-card"
                    >
                      <img v-if="homeworkForm.cet6.listening.image" :src="getImageUrl(homeworkForm.cet6.listening.image)" class="image-preview" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                    <div v-if="homeworkForm.cet6.listening.image" class="image-actions">
                      <el-button type="danger" size="small" @click="removeImage('cet6', 'listening')">移除图片</el-button>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="publishHomework('cet6', 'listening')" :loading="publishing">发布作业</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="写作" name="writing">
              <el-form :model="homeworkForm.cet6.writing" label-width="100px">
                <el-form-item label="作业标题">
                  <el-input v-model="homeworkForm.cet6.writing.title" placeholder="请输入作业标题" />
                </el-form-item>
                <el-form-item label="作业内容">
                  <el-input 
                    v-model="homeworkForm.cet6.writing.content" 
                    type="textarea" 
                    :rows="4" 
                    placeholder="请输入作业内容"
                  />
                </el-form-item>
                <el-form-item label="上传图片">
                  <div class="image-upload">
                    <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :before-upload="beforeImageUpload"
                      :on-change="(file) => handleImageChange(file, 'cet6', 'writing')"
                      accept="image/*"
                      list-type="picture-card"
                    >
                      <img v-if="homeworkForm.cet6.writing.image" :src="getImageUrl(homeworkForm.cet6.writing.image)" class="image-preview" />
                      <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                    <div v-if="homeworkForm.cet6.writing.image" class="image-actions">
                      <el-button type="danger" size="small" @click="removeImage('cet6', 'writing')">移除图片</el-button>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="publishHomework('cet6', 'writing')" :loading="publishing">发布作业</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import VideoUploadForm from '../../components/VideoUploadForm.vue'
import { api } from '../../api'

const activeVideoTab = ref('cet4')
const activeHomeworkTab = ref('cet4')
const activeCet4VideoTab = ref('speaking')
const activeCet6VideoTab = ref('speaking')
const activeCet4HomeworkTab = ref('speaking')
const activeCet6HomeworkTab = ref('speaking')
const publishing = ref(false)
const videos = ref<any[]>([])

const homeworkForm = ref({
  cet4: {
    speaking: {
      title: '',
      content: '',
      image: ''
    },
    reading: {
      title: '',
      content: '',
      image: ''
    },
    listening: {
      title: '',
      content: '',
      image: ''
    },
    writing: {
      title: '',
      content: '',
      image: ''
    }
  },
  cet6: {
    speaking: {
      title: '',
      content: '',
      image: ''
    },
    reading: {
      title: '',
      content: '',
      image: ''
    },
    listening: {
      title: '',
      content: '',
      image: ''
    },
    writing: {
      title: '',
      content: '',
      image: ''
    }
  }
})

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 视频上传成功
const handleUploadSuccess = () => {
  ElMessage.success('视频发布成功')
  fetchVideos()
}

// 获取视频列表
const fetchVideos = async () => {
  try {
    // 调用后端API获取视频列表
    const response = await api.videos.getAll()
    // 过滤出四六级英语的视频
    videos.value = response.filter((video: any) => video.category === '四级英语' || video.category === '六级英语')
    console.log('Fetched videos from API:', videos.value)
  } catch (error) {
    console.error('Failed to fetch videos:', error)
    ElMessage.error('获取视频列表失败')
    videos.value = []
  }
}

// 播放视频
const playVideo = (video: any) => {
  // 打开一个新窗口播放视频
  const videoUrl = `http://localhost:8080${video.videoUrl}`
  window.open(videoUrl, '_blank', 'width=800,height=600')
}

// 删除视频
const deleteVideo = async (videoId: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个视频吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用后端API删除视频
    const response = await api.videos.delete(videoId)
    if (response.success) {
      ElMessage.success('删除成功')
      fetchVideos()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 获取图片URL的函数
const getImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) {
    return url
  }
  if (url.startsWith('blob:')) {
    return url
  }
  return `http://localhost:8080${url}`
}

// 图片上传前检查
const beforeImageUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片文件大小不能超过 10MB!')
    return false
  }
  return false
}

// 处理图片选择 - 直接上传到服务器
const handleImageChange = async (file: any, level: string, type: string) => {
  if (file.raw) {
    try {
      // 先显示临时预览
      homeworkForm.value[level as keyof typeof homeworkForm.value][type as keyof typeof homeworkForm.value[keyof typeof homeworkForm.value]].image = URL.createObjectURL(file.raw)
      
      // 上传图片到服务器
      const result = await api.upload.image(file.raw)
      if (result.success) {
        // 使用服务器返回的图片URL
        homeworkForm.value[level as keyof typeof homeworkForm.value][type as keyof typeof homeworkForm.value[keyof typeof homeworkForm.value]].image = result.imageUrl
        ElMessage.success('图片上传成功')
      } else {
        ElMessage.error(result.message || '图片上传失败')
        homeworkForm.value[level as keyof typeof homeworkForm.value][type as keyof typeof homeworkForm.value[keyof typeof homeworkForm.value]].image = ''
      }
    } catch (error) {
      console.error('图片上传失败:', error)
      ElMessage.error('图片上传失败，请重试')
      homeworkForm.value[level as keyof typeof homeworkForm.value][type as keyof typeof homeworkForm.value[keyof typeof homeworkForm.value]].image = ''
    }
  }
}

// 移除图片
const removeImage = (level: string, type: string) => {
  homeworkForm.value[level as keyof typeof homeworkForm.value][type as keyof typeof homeworkForm.value[keyof typeof homeworkForm.value]].image = ''
}

// 发布作业
const publishHomework = async (level: string, type: string) => {
  const form = homeworkForm.value[level as keyof typeof homeworkForm.value][type as keyof typeof homeworkForm.value[keyof typeof homeworkForm.value]]
  if (!form.title || !form.content) {
    ElMessage.error('请填写作业标题和内容')
    return
  }
  
  // 转换type值，使其与学生端一致
  let homeworkType = type
  if (type === 'speaking') {
    homeworkType = '口语'
  } else if (type === 'reading') {
    homeworkType = '阅读'
  } else if (type === 'listening') {
    homeworkType = '听力'
  } else if (type === 'writing') {
    homeworkType = '写作'
  }
  
  try {
    publishing.value = true
    // 调用后端API发布作业
    const category = level === 'cet4' ? '四级英语' : '六级英语'
    const response = await api.homeworks.publish({
      title: form.title,
      content: form.content,
      category: category,
      type: homeworkType,
      image: form.image
    })
    console.log('作业发布成功:', response)
    ElMessage.success(`${level === 'cet4' ? '四级' : '六级'}${homeworkType}作业发布成功`)
    homeworkForm.value[level as keyof typeof homeworkForm.value][type as keyof typeof homeworkForm.value[keyof typeof homeworkForm.value]].title = ''
    homeworkForm.value[level as keyof typeof homeworkForm.value][type as keyof typeof homeworkForm.value[keyof typeof homeworkForm.value]].content = ''
    homeworkForm.value[level as keyof typeof homeworkForm.value][type as keyof typeof homeworkForm.value[keyof typeof homeworkForm.value]].image = ''
  } catch (error: any) {
    console.error('作业发布失败:', error)
    ElMessage.error(error.message || '发布失败')
  } finally {
    publishing.value = false
  }
}

// 组件挂载时获取视频列表
fetchVideos()
</script>

<style scoped>
.teacher-course {
  padding: 20px 0;
}

.teacher-course h2 {
  margin-bottom: 20px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.video-card,
.videos-list-card,
.homework-card {
  margin-bottom: 20px;
}

.videos-list {
  max-height: 400px;
  overflow-y: auto;
}

.video-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.video-item:last-child {
  border-bottom: none;
}

.video-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
}

.video-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.video-time {
  font-size: 12px;
  color: #909399;
}

.video-actions {
  display: flex;
  gap: 10px;
}

.image-upload {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

.image-actions {
  padding-top: 10px;
}
</style>

