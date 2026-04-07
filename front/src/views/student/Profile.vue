<template>
  <div class="profile">
    <h2>个人中心</h2>
    
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
            <el-form-item label="性别">
              <el-select v-model="userInfo.gender" :disabled="!editMode" placeholder="请选择性别">
                <el-option label="男" value="male" />
                <el-option label="女" value="female" />
              </el-select>
            </el-form-item>
            <el-form-item label="年龄">
              <el-input v-model="userInfo.age" type="number" :disabled="!editMode" />
            </el-form-item>
            <el-form-item label="学习目标">
              <el-input v-model="userInfo.goal" type="textarea" :rows="3" :disabled="!editMode" />
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

const userInfo = ref({
  name: '',
  email: '',
  gender: '',
  age: 0,
  goal: '',
  avatar: ''
})

const fullAvatarUrl = computed(() => {
  if (!userInfo.value.avatar) {
    return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=student%20avatar&image_size=square'
  }
  if (userInfo.value.avatar.startsWith('http')) {
    return userInfo.value.avatar
  }
  return `${API_BASE_URL}${userInfo.value.avatar}`
})

onMounted(() => {
  if (userStore.userInfo) {
    userInfo.value = {
      name: userStore.userInfo.name || '',
      email: userStore.userInfo.email || '',
      gender: '',
      age: 0,
      goal: '',
      avatar: userStore.userInfo.avatar || ''
    }
  }
})

const saveInfo = () => {
  ElMessage.success('保存成功')
  editMode.value = false
}

const changeAvatar = () => {
  fileInput.value?.click()
}

const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (!files || files.length === 0) {
    return
  }

  const file = files[0]
  
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }

  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  try {
    uploading.value = true
    
    const result = await api.upload.avatar(file)
    
    if (result.success) {
      userInfo.value.avatar = result.avatarUrl
      
      if (userStore.userInfo) {
        userStore.userInfo.avatar = result.avatarUrl
        localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
      }
      
      ElMessage.success('头像上传成功')
    } else {
      ElMessage.error(result.message || '头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败，请重试')
  } finally {
    uploading.value = false
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
</style>
