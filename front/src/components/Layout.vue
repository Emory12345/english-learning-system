<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <!-- 左侧系统名称 -->
      <div class="nav-left">
        <div class="system-logo">
          <span class="logo-text">在线英语学习系统</span>
        </div>
      </div>
      
      <!-- 中间导航菜单 -->
      <nav class="nav-center">
        <template v-if="userStore.role === 'student'">
          <a :href="'/student/home'" :class="[ 'nav-item', activeMenu === '/student/home' ? 'nav-item-active' : '' ]">
            首页
          </a>
          <a :href="'/student/teenage-english'" :class="[ 'nav-item', activeMenu.includes('/student/teenage-english') ? 'nav-item-active' : '' ]">
            青少年英语
          </a>
          <a :href="'/student/cet-english'" :class="[ 'nav-item', activeMenu.includes('/student/cet-english') ? 'nav-item-active' : '' ]">
            四六级英语
          </a>
          <a :href="'/student/kaoyan-english'" :class="[ 'nav-item', activeMenu.includes('/student/kaoyan-english') ? 'nav-item-active' : '' ]">
            考研英语
          </a>
          <a :href="'/student/ielts-toefl-english'" :class="[ 'nav-item', activeMenu.includes('/student/ielts-toefl-english') ? 'nav-item-active' : '' ]">
            雅思托福
          </a>

          <a :href="'/student/community'" :class="[ 'nav-item', activeMenu === '/student/community' ? 'nav-item-active' : '' ]">
            学习社区
          </a>
          <a :href="'/student/profile'" :class="[ 'nav-item', activeMenu === '/student/profile' ? 'nav-item-active' : '' ]">
            个人中心
          </a>
        </template>
        
        <template v-else-if="userStore.role === 'teacher'">
          <a :href="'/teacher/teenage-english'" :class="[ 'nav-item', activeMenu.includes('/teacher/teenage-english') ? 'nav-item-active' : '' ]">
            青少年英语
          </a>
          <a :href="'/teacher/kaoyan-english'" :class="[ 'nav-item', activeMenu.includes('/teacher/kaoyan-english') ? 'nav-item-active' : '' ]">
            考研英语
          </a>
          <a :href="'/teacher/cet-english'" :class="[ 'nav-item', activeMenu.includes('/teacher/cet-english') ? 'nav-item-active' : '' ]">
            四六级英语
          </a>
          <a :href="'/teacher/ielts-toefl-english'" :class="[ 'nav-item', activeMenu.includes('/teacher/ielts-toefl-english') ? 'nav-item-active' : '' ]">
            雅思托福
          </a>

          <a :href="'/teacher/homework-correction'" :class="[ 'nav-item', activeMenu === '/teacher/homework-correction' ? 'nav-item-active' : '' ]">
            待批改作业
          </a>
          <a :href="'/teacher/homework-management'" :class="[ 'nav-item', activeMenu === '/teacher/homework-management' ? 'nav-item-active' : '' ]">
            作业管理
          </a>
          <a :href="'/teacher/community'" :class="[ 'nav-item', activeMenu === '/teacher/community' ? 'nav-item-active' : '' ]">
            学习社区
          </a>
          <a :href="'/teacher/profile'" :class="[ 'nav-item', activeMenu === '/teacher/profile' ? 'nav-item-active' : '' ]">
            个人中心
          </a>
        </template>
        
        <template v-else-if="userStore.role === 'admin'">
          <a :href="'/admin/data-statistics'" :class="[ 'nav-item', activeMenu === '/admin/data-statistics' ? 'nav-item-active' : '' ]">
            数据统计
          </a>
          <a :href="'/admin/home'" :class="[ 'nav-item', activeMenu === '/admin/home' ? 'nav-item-active' : '' ]">
            教师审核
          </a>
          <a :href="'/admin/user-management'" :class="[ 'nav-item', activeMenu === '/admin/user-management' ? 'nav-item-active' : '' ]">
            用户管理
          </a>
          <a :href="'/admin/video-audit'" :class="[ 'nav-item', activeMenu === '/admin/video-audit' ? 'nav-item-active' : '' ]">
            课程审核
          </a>
          <a :href="'/admin/community'" :class="[ 'nav-item', activeMenu === '/admin/community' ? 'nav-item-active' : '' ]">
            学习社区
          </a>
        </template>
      </nav>
      
      <!-- 右侧用户信息 -->
      <div class="nav-right">
        <el-dropdown v-if="userStore.isLoggedIn" trigger="click" class="user-dropdown">
          <div class="user-info">
            <span class="user-name">{{ userStore.userInfo.name || '用户' }}</span>
            <el-avatar size="small" :src="userAvatar || defaultAvatar" />
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button v-else type="primary" size="small" @click="router.push('/login')" class="login-button">
          登录
        </el-button>
      </div>
    </header>
    


    <!-- 内容区 -->
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { HomeFilled, Reading, School, Document, Flag, Briefcase, ChatDotRound, List, User, Check, Notebook, DataAnalysis, UserFilled, VideoCamera, Search, ArrowDown } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const defaultAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar&image_size=square'

// 计算用户头像URL
const userAvatar = computed(() => {
  if (!userStore.userInfo || !userStore.userInfo.avatar) {
    return ''
  }
  
  const avatar = userStore.userInfo.avatar
  if (avatar.startsWith('http')) {
    return avatar
  }
  
  // 处理相对路径的头像URL
  const API_BASE_URL = 'http://localhost:8080'
  return `${API_BASE_URL}${avatar}`
})

// 确保store初始化
onMounted(() => {
  if (!userStore.isLoggedIn && localStorage.getItem('token')) {
    userStore.initialize()
  }
})

// 监听登录状态变化
watch(() => userStore.isLoggedIn, (newVal) => {
  if (!newVal) {
    router.push('/login')
  }
})

// 计算当前激活的菜单
const activeMenu = computed(() => {
  return router.currentRoute.value.path
})

// 获取角色文本
const getRoleText = (role: string | undefined) => {
  switch (role) {
    case 'student': return '学生'
    case 'teacher': return '教师'
    case 'admin': return '管理员'
    default: return '用户'
  }
}

// 获取页面标题
const getPageTitle = () => {
  const path = router.currentRoute.value.path
  if (path.includes('/student/home')) return '首页'
  if (path.includes('/student/teenage-english')) return '青少年英语'
  if (path.includes('/student/kaoyan-english')) return '考研英语'
  if (path.includes('/student/cet-english')) return '四六级英语'
  if (path.includes('/student/ielts-toefl-english')) return '雅思托福'

  if (path.includes('/student/community')) return '学习社区'
  if (path.includes('/student/profile')) return '个人中心'
  if (path.includes('/teacher/')) return '教师工作台'
  if (path.includes('/admin/')) return '管理后台'
  return '在线英语学习系统'
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
/* 全局样式变量 */
:root {
  --primary-color: #165DFF;
  --primary-light: #4080FF;
  --primary-dark: #0E42B2;
  --text-color: #333333;
  --text-light: #666666;
  --text-lighter: #999999;
  --background-color: #F5F7FA;
  --card-background: #FFFFFF;
  --border-color: #E5E7EB;
  --shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 4px 12px rgba(0, 0, 0, 0.15);
  --border-radius-sm: 4px;
  --border-radius-md: 8px;
  --border-radius-lg: 12px;
  --transition: all 0.3s ease;
  --banner-color: #60A5FA;
}

.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  font-family: 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: var(--background-color);
}

/* 顶部导航栏 */
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  background-color: #333333;
  color: white;
  box-shadow: var(--shadow-md);
  position: relative;
  z-index: 100;
  overflow-x: auto;
  white-space: nowrap;
}

.top-nav::-webkit-scrollbar {
  height: 4px;
}

.top-nav::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.top-nav::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
}

.top-nav::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

/* 左侧系统名称 */
.nav-left {
  display: flex;
  align-items: center;
}

.system-logo {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: white;
  letter-spacing: 1px;
}

/* 中间导航菜单 */
.nav-center {
  display: flex;
  align-items: center;
  gap: 30px;
  flex-shrink: 0;
}

.nav-item {
  color: white;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 0;
  position: relative;
  transition: var(--transition);
}

.nav-item:hover {
  color: #CCCCCC;
}

.nav-item-active {
  color: var(--banner-color);
  font-weight: 600;
}

.nav-item-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: var(--banner-color);
  border-radius: 1px;
}

/* 右侧用户信息 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 用户信息 */
.user-dropdown {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--border-radius-md);
  transition: var(--transition);
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: white;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 120px;
}

/* 登录按钮 */
.login-button {
  background-color: var(--banner-color);
  border-color: var(--banner-color);
  color: white;
  font-size: 14px;
  padding: 6px 16px;
  border-radius: var(--border-radius-md);
  transition: var(--transition);
}

.login-button:hover {
  background-color: #3B82F6;
  border-color: #3B82F6;
  color: white;
}







/* 内容区 */
.content {
  flex: 1;
  padding: 0 30px 30px;
  overflow-y: auto;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .top-nav {
    padding: 0 20px;
  }
  
  .nav-center {
    gap: 20px;
  }
  

  
  .content {
    padding: 0 20px 20px;
  }
}

@media screen and (max-width: 768px) {
  .top-nav {
    height: auto;
    padding: 12px 16px;
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .nav-center {
    order: 3;
    width: 100%;
    justify-content: center;
    gap: 16px;
  }
  
  .nav-right {
    gap: 12px;
  }
  

  
  .content {
    padding: 0 15px 15px;
  }
}



</style>