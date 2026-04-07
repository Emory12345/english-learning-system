<template>
  <div class="homework-management">
    <h2>作业管理</h2>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="refreshHomework">
        <el-icon><Refresh /></el-icon>
        刷新列表
      </el-button>
    </div>
    
    <!-- 作业列表 -->
    <el-card class="homework-card">
      <template #header>
        <div class="card-header">
          <span>已发布作业</span>
        </div>
      </template>
      
      <el-table :data="homeworkList" style="width: 100%">
        <el-table-column prop="title" label="作业标题" min-width="200" />
        <el-table-column prop="category" label="类别" width="120" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="createdAt" label="发布时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="editHomework(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="deleteHomework(scope.row.id)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="homeworkList.length === 0" description="暂无已发布作业" />
    </el-card>
    
    <!-- 编辑作业对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑作业" width="600px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="作业标题">
          <el-input v-model="editForm.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input
            v-model="editForm.content"
            type="textarea"
            placeholder="请输入作业内容"
            rows="4"
          />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="editForm.category" placeholder="请选择类别">
            <el-option label="青少年英语" value="TeenageEnglish" />
            <el-option label="商务英语" value="BusinessEnglish" />
            <el-option label="四六级英语" value="CETEnglish" />
            <el-option label="考研英语" value="KaoyanEnglish" />
            <el-option label="雅思托福" value="IELTS_ToeflEnglish" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.type" placeholder="请选择类型">
            <el-option label="听力" value="listening" />
            <el-option label="阅读" value="reading" />
            <el-option label="写作" value="writing" />
            <el-option label="口语" value="speaking" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片">
          <div class="image-upload">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              :on-change="handleImageChange"
              accept="image/*"
              list-type="picture-card"
            >
              <img v-if="editForm.image" :src="getImageUrl(editForm.image)" class="image-preview" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div v-if="editForm.image" class="image-actions">
              <el-button type="danger" size="small" @click="removeImage">移除图片</el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="音频">
          <div class="audio-upload">
            <el-upload
              class="audio-uploader"
              :show-file-list="false"
              :http-request="customUpload"
              accept="audio/*"
              list-type="text"
            >
              <el-button type="primary">
                <el-icon class="el-icon--left"><Upload /></el-icon>
                选择音频文件
              </el-button>
            </el-upload>
            <div v-if="editForm.audio" class="audio-actions">
              <el-button type="danger" size="small" @click="removeAudio">移除音频</el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmEdit">保存修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Edit, Delete, Plus } from '@element-plus/icons-vue'
import { api } from '../../api'

// 作业列表
const homeworkList = ref<any[]>([])
// 加载状态
const loading = ref(false)
// 编辑对话框
const editDialogVisible = ref(false)
// 编辑表单
const editForm = ref({
  id: '',
  title: '',
  content: '',
  category: '',
  type: '',
  image: '',
  audio: ''
})

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 获取教师作业列表
const getTeacherHomework = async () => {
  loading.value = true
  try {
    const response = await api.homeworks.getTeacherHomework()
    homeworkList.value = response
  } catch (error: any) {
    console.error('获取作业列表失败:', error)
    ElMessage.error('获取作业列表失败')
  } finally {
    loading.value = false
  }
}

// 刷新作业列表
const refreshHomework = () => {
  getTeacherHomework()
}

// 编辑作业
const editHomework = (homework: any) => {
  editForm.value = {
    id: homework.id,
    title: homework.title,
    content: homework.content,
    category: homework.category,
    type: homework.type,
    image: homework.image || '',
    audio: homework.audio || ''
  }
  editDialogVisible.value = true
}

// 确认编辑
const confirmEdit = async () => {
  if (!editForm.value.title || !editForm.value.content) {
    ElMessage.error('请填写作业标题和内容')
    return
  }
  
  console.log('编辑作业数据:', {
    id: editForm.value.id,
    title: editForm.value.title,
    content: editForm.value.content,
    category: editForm.value.category,
    type: editForm.value.type,
    image: editForm.value.image,
    audio: editForm.value.audio
  })
  
  try {
    await api.homeworks.editHomework(editForm.value.id, {
      title: editForm.value.title,
      content: editForm.value.content,
      category: editForm.value.category,
      type: editForm.value.type,
      image: editForm.value.image || null,
      audio: editForm.value.audio || null
    })
    ElMessage.success('作业编辑成功')
    editDialogVisible.value = false
    getTeacherHomework()
  } catch (error: any) {
    console.error('编辑作业失败:', error)
    ElMessage.error('编辑作业失败')
  }
}

// 删除作业
const deleteHomework = (id: string) => {
  ElMessageBox.confirm('确定要删除这个作业吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await api.homeworks.deleteHomework(id)
      ElMessage.success('作业删除成功')
      getTeacherHomework()
    } catch (error: any) {
      console.error('删除作业失败:', error)
      ElMessage.error('删除作业失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 页面加载时获取作业列表
onMounted(() => {
  getTeacherHomework()
})

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
const handleImageChange = async (file: any) => {
  if (file.raw) {
    try {
      // 先显示临时预览
      editForm.value.image = URL.createObjectURL(file.raw)
      
      // 上传图片到服务器
      const response = await api.upload.image(file.raw)
      if (response.success) {
        // 使用服务器返回的图片URL
        editForm.value.image = response.imageUrl
        ElMessage.success('图片上传成功')
      } else {
        ElMessage.error(response.message || '图片上传失败')
        editForm.value.image = ''
      }
    } catch (error: any) {
      console.error('图片上传失败:', error)
      ElMessage.error(error.message || '图片上传失败')
      editForm.value.image = ''
    }
  }
}

// 移除图片
const removeImage = () => {
  editForm.value.image = ''
}

// 自定义上传方法
const customUpload = async (options: any) => {
  try {
    const response = await api.upload.audio(options.file)
    if (response.success) {
      editForm.value.audio = response.audioUrl
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
  editForm.value.audio = ''
  ElMessage.success('音频已删除')
}
</script>

<style scoped>
.homework-management {
  padding: 20px 0;
}

.homework-management h2 {
  margin-bottom: 20px;
  color: #303133;
}

.action-buttons {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
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

.audio-upload {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.audio-actions {
  padding-top: 10px;
}
</style>