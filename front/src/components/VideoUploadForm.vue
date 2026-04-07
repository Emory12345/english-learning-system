<template>
  <div class="video-upload-form">
    <el-form :model="form" label-width="100px">
      <el-form-item label="视频标题">
        <el-input v-model="form.title" placeholder="请输入视频标题" />
      </el-form-item>
      <el-form-item label="视频描述">
        <el-input 
          v-model="form.description" 
          type="textarea" 
          :rows="2" 
          placeholder="请输入视频描述"
        />
      </el-form-item>
      <el-form-item label="选择视频">
        <el-upload
          class="video-uploader"
          :show-file-list="false"
          :before-upload="beforeUpload"
          :on-success="handleSuccess"
          :on-error="handleError"
          :on-change="handleChange"
          :auto-upload="false"
          accept="video/*"
          drag
        >
          <div v-if="!videoUrl" class="upload-placeholder">
            <el-icon class="upload-icon"><UploadFilled /></el-icon>
            <div class="upload-text">将视频拖到此处，或<em>点击上传</em></div>
            <div class="upload-hint">支持 mp4、mov、avi 等格式，文件大小不超过 100MB</div>
          </div>
          <div v-else class="video-preview">
            <video :src="videoUrl" controls class="preview-video" />
            <el-button type="danger" size="small" @click="removeVideo" class="remove-btn">移除视频</el-button>
          </div>
        </el-upload>
      </el-form-item>
      <el-form-item>
        <el-button 
          type="primary" 
          @click="uploadVideo" 
          :loading="uploading"
          :disabled="!form.title || !selectedFile"
        >
          发布视频
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { api } from '../api'

interface Props {
  category: string
  type: string
}

const props = defineProps<Props>()
const emit = defineEmits(['upload-success'])

const uploading = ref(false)
const selectedFile = ref<File | null>(null)
const videoUrl = ref('')

const form = ref({
  title: '',
  description: ''
})

// 上传前检查
const beforeUpload = (file: File) => {
  const isVideo = file.type.startsWith('video/')
  const isLt100M = file.size / 1024 / 1024 < 100
  
  if (!isVideo) {
    ElMessage.error('只能上传视频文件!')
    return false
  }
  if (!isLt100M) {
    ElMessage.error('视频文件大小不能超过 100MB!')
    return false
  }
  return true
}

// 文件改变
const handleChange = (file: any) => {
  if (file.raw) {
    selectedFile.value = file.raw
    videoUrl.value = URL.createObjectURL(file.raw)
  }
}

// 上传成功
const handleSuccess = (response: any, file: any) => {
  ElMessage.success('视频上传成功')
  emit('upload-success')
  resetForm()
}

// 上传失败
const handleError = (error: any) => {
  ElMessage.error('视频上传失败，请重试')
  uploading.value = false
}

// 移除视频
const removeVideo = () => {
  selectedFile.value = null
  videoUrl.value = ''
  if (selectedFile.value) {
    URL.revokeObjectURL(videoUrl.value)
  }
}

// 重置表单
const resetForm = () => {
  form.value.title = ''
  form.value.description = ''
  removeVideo()
  uploading.value = false
}

// 上传视频
const uploadVideo = async () => {
  if (!form.value.title || !selectedFile.value) {
    ElMessage.error('请填写视频标题并选择视频文件')
    return
  }

  try {
    uploading.value = true
    // 调用后端API上传视频
    await api.upload.video(
      selectedFile.value,
      form.value.title,
      form.value.description,
      props.category,
      props.type
    )
    handleSuccess({}, {})
  } catch (error: any) {
    ElMessage.error(error.message || '上传失败')
    uploading.value = false
  }
}
</script>

<style scoped>
.video-upload-form {
  padding: 10px 0;
}

.upload-placeholder {
  text-align: center;
  padding: 40px 0;
}

.upload-icon {
  font-size: 67px;
  color: #c0c4cc;
  margin: 40px 0 16px;
  transition: color 0.3s;
}

.upload-text {
  color: #606266;
  font-size: 14px;
}

.upload-text em {
  color: #409eff;
  font-style: normal;
}

.upload-hint {
  color: #909399;
  font-size: 12px;
  margin-top: 7px;
}

.video-preview {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.preview-video {
  width: 100%;
  max-height: 300px;
  border-radius: 4px;
}

.remove-btn {
  position: absolute;
  top: 10px;
  right: 10px;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style>
