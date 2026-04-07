<template>
  <div class="profile">
    <h2>个人中心</h2>
    
    <!-- 个人信息 -->
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
            <el-form-item v-if="editMode">
              <el-button type="primary" @click="saveInfo">保存</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>


  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

import { api } from '../../api'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const editMode = ref(false)
const fileInput = ref<HTMLInputElement>()
const uploading = ref(false)

const API_BASE_URL = 'http://localhost:8080'

// 用户信息
const userInfo = ref({
  name: '',
  email: '',
  avatar: ''
})



// 计算完整的头像URL
const fullAvatarUrl = computed(() => {
  if (!userInfo.value.avatar) {
    return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=teacher%20avatar&image_size=square'
  }
  if (userInfo.value.avatar.startsWith('http')) {
    return userInfo.value.avatar
  }
  return `${API_BASE_URL}${userInfo.value.avatar}`
})

// 初始化用户信息
onMounted(() => {
  if (userStore.userInfo) {
    userInfo.value = {
      name: userStore.userInfo.name || '',
      email: userStore.userInfo.email || '',
      avatar: userStore.userInfo.avatar || ''
    }
  }
})

// 保存个人信息
const saveInfo = () => {
  ElMessage.success('保存成功')
  editMode.value = false
}

// 更换头像 - 打开文件选择器
const changeAvatar = () => {
  fileInput.value?.click()
}

// 处理文件选择
const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files || files.length === 0) {
    return
  }

  const file = files[0]
  
  // 检查文件大小 (最大10MB)
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }

  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  try {
    uploading.value = true
    
    // 上传头像
    const result = await api.upload.avatar(file)
    
    if (result.success) {
      userInfo.value.avatar = result.avatarUrl
      
      // 更新store中的用户信息
      if (userStore.userInfo) {
        userStore.userInfo.avatar = result.avatarUrl
        localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
      }
      
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(result.message || '头像上传失败')
    }
  } catch (error: any) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败，请重试')
  } finally {
    uploading.value = false
    // 清空文件输入，允许重复选择同一文件
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}
</script>

<style scoped>
.profile {
  padding: 20px 0;
}

.profile h2 {
  margin-bottom: 20px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-info {
  display: flex;
  gap: 40px;
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

@media screen and (max-width: 768px) {
  .profile-info {
    flex-direction: column;
    align-items: center;
  }
}
</style>
