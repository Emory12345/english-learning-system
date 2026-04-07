<template>
  <div class="system-settings">
    <h2>系统设置</h2>
    
    <!-- 系统设置表单 -->
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <span>基本设置</span>
        </div>
      </template>
      <el-form :model="settingsForm" label-width="120px">
        <el-form-item label="系统名称">
          <el-input v-model="settingsForm.systemName" />
        </el-form-item>
        <el-form-item label="系统描述">
          <el-input v-model="settingsForm.systemDescription" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="系统版本">
          <el-input v-model="settingsForm.systemVersion" disabled />
        </el-form-item>
        <el-form-item label="是否开启注册">
          <el-switch v-model="settingsForm.enableRegistration" />
        </el-form-item>
        <el-form-item label="最大上传文件大小">
          <el-input v-model="settingsForm.maxFileSize" suffix="MB" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 邮件设置 -->
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <span>邮件设置</span>
        </div>
      </template>
      <el-form :model="emailForm" label-width="120px">
        <el-form-item label="SMTP服务器">
          <el-input v-model="emailForm.smtpServer" />
        </el-form-item>
        <el-form-item label="SMTP端口">
          <el-input v-model="emailForm.smtpPort" type="number" />
        </el-form-item>
        <el-form-item label="发件人邮箱">
          <el-input v-model="emailForm.senderEmail" />
        </el-form-item>
        <el-form-item label="发件人名称">
          <el-input v-model="emailForm.senderName" />
        </el-form-item>
        <el-form-item label="SMTP用户名">
          <el-input v-model="emailForm.smtpUsername" />
        </el-form-item>
        <el-form-item label="SMTP密码">
          <el-input v-model="emailForm.smtpPassword" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEmailSettings">保存邮件设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// 系统设置表单
const settingsForm = ref({
  systemName: '在线英语学习系统',
  systemDescription: '基于 SpringBoot + Vue 的在线英语学习系统',
  systemVersion: '1.0.0',
  enableRegistration: true,
  maxFileSize: 10
})

// 邮件设置表单
const emailForm = ref({
  smtpServer: 'smtp.example.com',
  smtpPort: 587,
  senderEmail: 'noreply@example.com',
  senderName: '在线英语学习系统',
  smtpUsername: 'username',
  smtpPassword: 'password'
})

// 保存系统设置
const saveSettings = () => {
  // 实际项目中这里会调用后端API
  ElMessage.success('保存系统设置成功')
}

// 保存邮件设置
const saveEmailSettings = () => {
  // 实际项目中这里会调用后端API
  ElMessage.success('保存邮件设置成功')
}
</script>

<style scoped>
.system-settings {
  padding: 20px 0;
}

.system-settings h2 {
  margin-bottom: 20px;
  color: #303133;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.settings-card {
  margin-bottom: 20px;
}

@media screen and (max-width: 768px) {
  .el-form-item {
    margin-bottom: 20px;
  }
}
</style>