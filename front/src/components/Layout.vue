<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <!-- 左侧系统名称 -->
      <div class="nav-left">
        <div class="system-logo">
          <span class="logo-dot"></span>
          <span class="logo-text">在线英语学习系统</span>
        </div>
      </div>

      <!-- 汉堡菜单按钮 -->
      <div class="hamburger" @click="isMenuOpen = !isMenuOpen">
        <div class="hamburger-line"></div>
        <div class="hamburger-line"></div>
        <div class="hamburger-line"></div>
      </div>

      <!-- 中间导航菜单 -->
      <nav class="nav-center" :class="{ 'menu-open': isMenuOpen }">
        <template v-if="userStore.role === 'student'">
          <a :href="'/student/home'" :class="[ 'nav-item', activeMenu === '/student/home' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/student/home')">
            首页
          </a>
          <a :href="'/student/teenage-english'" :class="[ 'nav-item', activeMenu.includes('/student/teenage-english') ? 'nav-item-active' : '' ]" @click.prevent="navigate('/student/teenage-english')">
            青少年英语
          </a>
          <a :href="'/student/cet-english'" :class="[ 'nav-item', activeMenu.includes('/student/cet-english') ? 'nav-item-active' : '' ]" @click.prevent="navigate('/student/cet-english')">
            四六级英语
          </a>
          <a :href="'/student/kaoyan-english'" :class="[ 'nav-item', activeMenu.includes('/student/kaoyan-english') ? 'nav-item-active' : '' ]" @click.prevent="navigate('/student/kaoyan-english')">
            考研英语
          </a>
          <a :href="'/student/ielts-toefl-english'" :class="[ 'nav-item', activeMenu.includes('/student/ielts-toefl-english') ? 'nav-item-active' : '' ]" @click.prevent="navigate('/student/ielts-toefl-english')">
            雅思
          </a>
          <a :href="'/student/community'" :class="[ 'nav-item', activeMenu === '/student/community' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/student/community')">
            学习社区
          </a>
          <a :href="'/student/profile'" :class="[ 'nav-item', activeMenu === '/student/profile' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/student/profile')">
            个人中心
          </a>
        </template>

        <template v-else-if="userStore.role === 'teacher'">
          <a :href="'/teacher/dashboard'" :class="[ 'nav-item', activeMenu.includes('/teacher/dashboard') ? 'nav-item-active' : '' ]" @click.prevent="navigate('/teacher/dashboard')">
            首页
          </a>
          <a :href="'/teacher/teenage-english'" :class="[ 'nav-item', activeMenu.includes('/teacher/teenage-english') ? 'nav-item-active' : '' ]" @click.prevent="navigate('/teacher/teenage-english')">
            课程管理
          </a>
          <a :href="'/teacher/homework-correction'" :class="[ 'nav-item', activeMenu === '/teacher/homework-correction' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/teacher/homework-correction')">
            作业管理
          </a>
          <a :href="'/teacher/community'" :class="[ 'nav-item', activeMenu === '/teacher/community' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/teacher/community')">
            学习社区
          </a>
          <a :href="'/teacher/profile'" :class="[ 'nav-item', activeMenu === '/teacher/profile' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/teacher/profile')">
            个人中心
          </a>
        </template>

        <template v-else-if="userStore.role === 'admin'">
          <a :href="'/admin/home'" :class="[ 'nav-item', activeMenu === '/admin/home' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/admin/home')">
            首页
          </a>
          <a :href="'/admin/user-management'" :class="[ 'nav-item', activeMenu === '/admin/user-management' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/admin/user-management')">
            用户管理
          </a>
          <a :href="'/admin/video-audit'" :class="[ 'nav-item', activeMenu === '/admin/video-audit' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/admin/video-audit')">
            课程审核
          </a>
          <a :href="'/admin/community'" :class="[ 'nav-item', activeMenu === '/admin/community' ? 'nav-item-active' : '' ]" @click.prevent="navigate('/admin/community')">
            学习社区
          </a>
        </template>
      </nav>

      <!-- 右侧用户信息 -->
      <div class="nav-right">
        <div v-if="userStore.isLoggedIn" class="user-dropdown-wrapper">
          <div class="user-info" @click="isDropdownOpen = !isDropdownOpen">
            <span class="user-name">{{ userStore.userInfo.name || '用户' }}</span>
            <div class="user-avatar">
              <img :src="userAvatar || defaultAvatar" alt="avatar">
            </div>
            <span class="dropdown-arrow">▼</span>
          </div>
          <div class="dropdown-menu" v-if="isDropdownOpen">
            <div class="dropdown-item" @click="handleLogout">退出登录</div>
          </div>
        </div>
        <div v-else class="login-button-wrapper">
          <button class="login-button" @click="router.push('/login')">
            登录
          </button>
        </div>
      </div>
    </header>

    <!-- 内容区 -->
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const router = useRouter()
const defaultAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar&image_size=square'
const isMenuOpen = ref(false)
const isDropdownOpen = ref(false)

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

// 导航方法
const navigate = (path: string) => {
  router.push(path)
  isMenuOpen.value = false
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
  isDropdownOpen.value = false
  router.push('/login')
}

// 点击外部关闭下拉菜单
onMounted(() => {
  document.addEventListener('click', (e) => {
    const dropdown = document.querySelector('.user-dropdown-wrapper')
    if (dropdown && !dropdown.contains(e.target as Node)) {
      isDropdownOpen.value = false
    }
  })
})
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  font-family: 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7fa;
}

/* 顶部导航栏 */
.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  position: relative;
  z-index: 100;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

/* 汉堡菜单 */
.hamburger {
  display: none;
  flex-direction: column;
  cursor: pointer;
  padding: 6px;
}

.hamburger-line {
  width: 20px;
  height: 2px;
  background-color: rgba(255, 255, 255, 0.9);
  margin: 2px 0;
  transition: all 0.3s ease;
}

/* 左侧系统名称 */
.nav-left {
  display: flex;
  align-items: center;
}

.system-logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: white;
  flex-shrink: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: white;
  letter-spacing: 0.5px;
}

/* 中间导航菜单 */
.nav-center {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 6px 14px;
  border-radius: 20px;
  position: relative;
  transition: all 0.3s ease;
  cursor: pointer;
}

.nav-arrow {
  font-size: 10px;
  color: inherit;
}

.nav-item:hover {
  color: #ffffff;
  transform: scale(1.02);
}

.nav-item-active {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.2);
  font-weight: 600;
}

/* 右侧用户信息 */
.nav-right {
  display: flex;
  align-items: center;
}

.user-dropdown-wrapper {
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.15);
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: white;
  white-space: nowrap;
}

.user-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.6);
  flex-shrink: 0;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dropdown-arrow {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.85);
  transition: all 0.3s ease;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
  min-width: 120px;
  overflow: hidden;
}

.dropdown-item {
  padding: 12px 16px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s ease;
}

.dropdown-item:hover {
  background-color: #f0f4ff;
  color: #667eea;
}

/* 登录按钮 */
.login-button {
  background-color: white;
  border: none;
  color: #667eea;
  font-size: 14px;
  font-weight: 600;
  padding: 6px 18px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.2);
}

.login-button:hover {
  background-color: #f0f4ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.3);
}

/* 内容区 */
.content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  background-color: transparent;
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .nav-center {
    gap: 4px;
  }

  .nav-item {
    padding: 6px 12px;
  }
}

@media screen and (max-width: 768px) {
  .top-nav {
    padding: 0 16px;
    height: 50px;
  }

  .hamburger {
    display: flex;
  }

  .nav-center {
    position: absolute;
    top: 50px;
    left: 0;
    right: 0;
    flex-direction: column;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 16px;
    gap: 8px;
    display: none;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
  }

  .nav-center.menu-open {
    display: flex;
  }

  .nav-item {
    width: 100%;
    padding: 10px 16px;
    border-radius: 8px;
  }

  .content {
    padding: 0 15px 15px;
  }
}
</style>
