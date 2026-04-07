<template>
  <div class="layout-container">
    <!-- 顶部导航 -->
    <header class="top-nav">
      <div class="logo">
        <h1>在线英语学习系统</h1>
      </div>
      <div class="user-info">
        <el-dropdown v-if="userStore.isLoggedIn">
          <span class="user-name">{{ userStore.userInfo.name || '用户' }}</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button v-else type="primary" size="small" @click="router.push('/login')">登录</el-button>
      </div>
    </header>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          router
        >
          <!-- 学生端菜单 -->
          <template v-if="userStore.role === 'student'">
            <el-menu-item index="/student/home">
              <template #title>
                <span>首页</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/student/teenage-english">
              <template #title>
                <span>青少年英语</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/student/kaoyan-english">
              <template #title>
                <span>考研英语</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/student/cet-english">
              <template #title>
                <span>四六级英语</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/student/ielts-toefl-english">
              <template #title>
                <span>雅思托福</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/student/business-english">
              <template #title>
                <span>企业英语</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/student/community">
              <template #title>
                <span>学习社区</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/student/word-learning-list">
              <template #title>
                <span>学习列表</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/student/profile">
              <template #title>
                <span>个人中心</span>
              </template>
            </el-menu-item>
          </template>

          <!-- 教师端菜单 -->
          <template v-else-if="userStore.role === 'teacher'">
            <el-menu-item index="/teacher/teenage-english">
              <template #title>
                <span>青少年英语</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/teacher/kaoyan-english">
              <template #title>
                <span>考研英语</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/teacher/cet-english">
              <template #title>
                <span>四六级英语</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/teacher/ielts-toefl-english">
              <template #title>
                <span>雅思托福</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/teacher/business-english">
              <template #title>
                <span>企业英语</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/teacher/homework-correction">
              <template #title>
                <span>待批改作业</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/teacher/homework-management">
              <template #title>
                <span>作业管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/teacher/community">
              <template #title>
                <span>学习社区</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/teacher/profile">
              <template #title>
                <span>个人中心</span>
              </template>
            </el-menu-item>
          </template>

          <!-- 管理后台菜单 -->
          <template v-else-if="userStore.role === 'admin'">
            <el-menu-item index="/admin/data-statistics">
              <template #title>
                <span>首页</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/admin/home">
              <template #title>
                <span>教师审核</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/admin/user-management">
              <template #title>
                <span>用户管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/admin/video-audit">
              <template #title>
                <span>课程审核</span>
              </template>
            </el-menu-item>
          </template>
        </el-menu>
      </aside>

      <!-- 内容区 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const router = useRouter()

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

// 退出登录
const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #409EFF;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo h1 {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  cursor: pointer;
  margin-right: 8px;
}

.main-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 200px;
  background-color: #f5f7fa;
  border-right: 1px solid #e4e7ed;
}

.sidebar-menu {
  border-right: none;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #ecf5ff;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .sidebar {
    width: 180px;
  }
}
</style>