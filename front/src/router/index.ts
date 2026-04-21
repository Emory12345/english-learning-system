import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '../stores/user'

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { requiresAuth: false }
  },
  // 学生端路由
  {
    path: '/student',
    name: 'StudentLayout',
    component: () => import('../components/Layout.vue'),
    meta: { requiresAuth: true, roles: ['student'] },
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('../views/student/Home.vue')
      },
      {
        path: 'course-selection',
        name: 'CourseSelection',
        component: () => import('../views/student/CourseSelection.vue')
      },
      {
        path: 'video-player/:courseId',
        name: 'VideoPlayer',
        component: () => import('../views/student/VideoPlayer.vue')
      },
      {
        path: 'practice',
        name: 'Practice',
        component: () => import('../views/student/Practice.vue')
      },
      {
        path: 'community',
        name: 'Community',
        component: () => import('../views/student/Community.vue')
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('../views/student/Profile.vue')
      },
      {
        path: 'teenage-english',
        name: 'TeenageEnglish',
        component: () => import('../views/student/TeenageEnglishAgeSelect.vue')
      },
      {
        path: 'teenage-english-3-6',
        name: 'TeenageEnglish3-6',
        component: () => import('../views/student/TeenageEnglish3-6.vue')
      },
      {
        path: 'teenage-english-7-15',
        name: 'TeenageEnglish7-15',
        component: () => import('../views/student/TeenageEnglish7-15.vue')
      },
      {
        path: 'teenage-english-15-18',
        name: 'TeenageEnglish15-18',
        component: () => import('../views/student/TeenageEnglish15-18.vue')
      },

      {
        path: 'cet-english',
        name: 'CETEnglish',
        component: () => import('../views/student/CETEnglish.vue')
      },
      {
        path: 'kaoyan-english',
        name: 'KaoyanEnglish',
        component: () => import('../views/student/KaoyanEnglish.vue')
      },
      {
        path: 'cet4-words',
        name: 'CET4Words',
        component: () => import('../views/student/CET4Words.vue')
      },
      {
        path: 'cet6-words',
        name: 'CET6Words',
        component: () => import('../views/student/CET6Words.vue')
      },
      {
        path: 'cet4-real-tests',
        name: 'CET4RealTests',
        component: () => import('../views/student/CET4RealTests.vue')
      },
      {
        path: 'cet6-real-tests',
        name: 'CET6RealTests',
        component: () => import('../views/student/CET6RealTests.vue')
      },
      {
        path: 'kaoyan-words',
        name: 'KaoyanWords',
        component: () => import('../views/student/KaoyanWords.vue')
      },
      {
        path: 'ielts-toefl-english',
        name: 'IELTS_ToeflEnglish',
        component: () => import('../views/student/IELTS_ToeflEnglish.vue')
      },
      {
        path: 'business-english',
        name: 'BusinessEnglish',
        component: () => import('../views/student/BusinessEnglish.vue')
      }
    ]
  },
  // 教师端路由
  {
    path: '/teacher',
    name: 'TeacherLayout',
    component: () => import('../components/Layout.vue'),
    meta: { requiresAuth: true, roles: ['teacher'] },
    redirect: '/teacher/teenage-english',
    children: [
      {
        path: 'teenage-english',
        name: 'TeacherTeenageEnglish',
        component: () => import('../views/teacher/TeenageEnglish.vue')
      },
      {
        path: 'kaoyan-english',
        name: 'TeacherKaoyanEnglish',
        component: () => import('../views/teacher/KaoyanEnglish.vue')
      },
      {
        path: 'cet-english',
        name: 'TeacherCETEnglish',
        component: () => import('../views/teacher/CETEnglish.vue')
      },
      {
        path: 'ielts-toefl-english',
        name: 'TeacherIELTS_ToeflEnglish',
        component: () => import('../views/teacher/IELTS_ToeflEnglish.vue')
      },
      {
        path: 'business-english',
        name: 'TeacherBusinessEnglish',
        component: () => import('../views/teacher/BusinessEnglish.vue')
      },
      {
        path: 'homework-correction',
        name: 'HomeworkCorrection',
        component: () => import('../views/teacher/HomeworkCorrection.vue')
      },
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: () => import('../views/teacher/Profile.vue')
      },
      {
        path: 'homework-management',
        name: 'HomeworkManagement',
        component: () => import('../views/teacher/HomeworkManagement.vue')
      },
      {
        path: 'community',
        name: 'TeacherCommunity',
        component: () => import('../views/teacher/Community.vue')
      }
    ]
  },
  // 管理后台路由
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('../components/Layout.vue'),
    meta: { requiresAuth: true, roles: ['admin'] },
    redirect: '/admin/data-statistics',
    children: [
      {
        path: 'home',
        name: 'AdminHome',
        component: () => import('../views/admin/Home.vue')
      },
      {
        path: 'user-management',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagement.vue')
      },
      {
        path: 'course-audit',
        name: 'CourseAudit',
        component: () => import('../views/admin/CourseAudit.vue')
      },
      {
        path: 'video-audit',
        name: 'VideoAudit',
        component: () => import('../views/admin/VideoAudit.vue')
      },
      {
        path: 'data-statistics',
        name: 'DataStatistics',
        component: () => import('../views/admin/DataStatistics.vue')
      },
      {
        path: 'system-settings',
        name: 'SystemSettings',
        component: () => import('../views/admin/SystemSettings.vue')
      },
      {
        path: 'community',
        name: 'AdminCommunity',
        component: () => import('../views/admin/Community.vue')
      }
    ]
  },
  // 重定向到登录页
  {
    path: '/',
    redirect: '/login'
  },
  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.meta.requiresAuth
  const roles = to.meta.roles as string[]

  // 如果用户已经登录并且有角色
  if (userStore.isLoggedIn && userStore.role) {
    next()
  } else {
    // 未登录，跳转到登录页
    if (requiresAuth) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router