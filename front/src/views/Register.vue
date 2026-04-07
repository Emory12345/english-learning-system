<template>
  <div class="register-container">
    <div class="register-form-wrapper">
      <h2>在线英语学习系统</h2>
      <el-form
        :model="registerForm"
        :rules="registerRules"
        ref="registerFormRef"
        label-position="top"
      >
        <el-form-item label="角色" prop="role">
          <el-radio-group v-model="registerForm.role">
            <el-radio label="student">学生</el-radio>
            <el-radio label="teacher">教师</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="registerForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" />
        </el-form-item>
        
        <!-- 教师专用字段 -->
        <template v-if="registerForm.role === 'teacher'">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="registerForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="registerForm.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="教学科目" prop="subject">
            <el-input v-model="registerForm.subject" placeholder="请输入教学科目" />
          </el-form-item>
          <el-form-item label="学历/毕业院校" prop="education">
            <el-input v-model="registerForm.education" placeholder="请输入学历/毕业院校" />
          </el-form-item>
          <el-form-item label="教学年限" prop="experience">
            <el-input v-model="registerForm.experience" placeholder="请输入教学年限" />
          </el-form-item>
          <el-form-item label="个人简介" prop="introduction">
            <el-input v-model="registerForm.introduction" type="textarea" placeholder="请输入个人简介" />
          </el-form-item>
          <el-form-item label="教师资格证号" prop="certificate">
            <el-input v-model="registerForm.certificate" placeholder="请输入教师资格证号" />
          </el-form-item>
        </template>
        
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">注册</el-button>
        </el-form-item>
        <div class="login-link">
          <span>已有账号？</span>
          <a href="/login">立即登录</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../api'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  role: 'student' as 'student' | 'teacher',
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  // 教师专用字段
  username: '',
  gender: '',
  subject: '',
  education: '',
  experience: '',
  introduction: '',
  certificate: ''
})

const registerRules = {
  role: [
    {
      required: true,
      message: '请选择角色',
      trigger: 'change'
    }
  ],
  name: [
    {
      required: true,
      message: '请输入姓名',
      trigger: 'blur'
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
  ],
  confirmPassword: [
    {
      required: true,
      message: '请确认密码',
      trigger: 'blur'
    },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  // 教师专用字段验证规则
  username: [
    {
      required: registerForm.role === 'teacher',
      message: '请输入用户名',
      trigger: 'blur'
    }
  ],
  gender: [
    {
      required: registerForm.role === 'teacher',
      message: '请选择性别',
      trigger: 'change'
    }
  ],
  subject: [
    {
      required: registerForm.role === 'teacher',
      message: '请输入教学科目',
      trigger: 'blur'
    }
  ],
  education: [
    {
      required: registerForm.role === 'teacher',
      message: '请输入学历/毕业院校',
      trigger: 'blur'
    }
  ],
  experience: [
    {
      required: registerForm.role === 'teacher',
      message: '请输入教学年限',
      trigger: 'blur'
    }
  ],
  introduction: [
    {
      required: registerForm.role === 'teacher',
      message: '请输入个人简介',
      trigger: 'blur'
    }
  ],
  certificate: [
    {
      required: registerForm.role === 'teacher',
      message: '请输入教师资格证号',
      trigger: 'blur'
    }
  ]
}

const handleRegister = async () => {
  if (registerFormRef.value) {
    await registerFormRef.value.validate(async (valid: boolean) => {
      if (valid) {
        loading.value = true
        try {
          // 调用后端API注册
          await api.auth.register({
            name: registerForm.name,
            email: registerForm.email,
            password: registerForm.password,
            confirmPassword: registerForm.confirmPassword,
            role: registerForm.role
          })
          
          // 注册成功
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } catch (error: any) {
          ElMessage.error(error.message || '注册失败，请稍后重试')
          console.error('Register error:', error)
        } finally {
          loading.value = false
        }
      }
    })
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.register-form-wrapper {
  width: 400px;
  padding: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-form-wrapper h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}

.login-link {
  margin-top: 20px;
  text-align: center;
}

.login-link a {
  color: #409EFF;
  text-decoration: none;
  margin-left: 5px;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>