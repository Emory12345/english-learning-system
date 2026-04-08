<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <h2>在线英语学习系统</h2>
      <el-form
        :model="loginForm"
        :rules="loginRules"
        ref="loginFormRef"
        label-position="top"
      >
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="loginForm.role">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">教师</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="loginForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
        <div class="register-link">
          <span>还没有账号？</span>
          <a href="/register">立即注册</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { api } from '../api'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  role: 'student' as 'student' | 'teacher' | 'admin',
  email: '',
  password: ''
})

const loginRules = {
  role: [
    {
      required: true,
      message: '请选择角色',
      trigger: 'change'
    }
  ],
  email: [
    {
      required: true,
      message: '请输入邮箱',
      trigger: 'blur'
    },
    {
      type: 'email',
      message: '请输入正确的邮箱格式',
      trigger: 'blur'
    }
  ],
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: 'blur'
    },
    {
      min: 6,
      message: '密码长度至少为6位',
      trigger: 'blur'
    }
  ]
}

const handleLogin = async () => {
  if (loginFormRef.value) {
    const valid = await new Promise<boolean>((resolve) => {
      loginFormRef.value.validate((valid: boolean) => {
        resolve(valid)
      })
    })
    
    if (valid) {
      loading.value = true
      try {
        // 清除localStorage中的token，避免使用无效的token
        localStorage.removeItem('token')
        
        // 调用后端API登录
        const response = await api.auth.login({
          email: loginForm.email,
          password: loginForm.password,
          role: loginForm.role
        })
        
        // 登录成功
        userStore.login(response.role, {
          id: response.userInfo.id.toString(),
          name: response.userInfo.name,
          email: response.userInfo.email
        }, response.token)

        ElMessage.success('登录成功')

        // 根据角色跳转到对应首页
        switch (response.role) {
          case 'student':
            router.push('/student/home')
            break
          case 'teacher':
            router.push('/teacher/teenage-english')
            break
          case 'admin':
            router.push('/admin/data-statistics')
            break
        }
      } catch (error: any) {
        ElMessage.error(error.message || '登录失败，请检查邮箱和密码')
        console.error('Login error:', error)
      } finally {
        loading.value = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-form-wrapper {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-form-wrapper h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}

.register-link {
  margin-top: 20px;
  text-align: center;
}

.register-link a {
  color: #409EFF;
  text-decoration: none;
  margin-left: 5px;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>