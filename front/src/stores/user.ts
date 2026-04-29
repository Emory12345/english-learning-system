import { defineStore } from 'pinia'

interface UserState {
  isLoggedIn: boolean
  role: 'student' | 'teacher' | 'admin' | ''
  token: string
  userInfo: {
    id: string
    name: string
    email: string
    avatar?: string
    phone?: string
    bio?: string
  }
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    isLoggedIn: false,
    role: '',
    token: '',
    userInfo: {
      id: '',
      name: '',
      email: ''
    }
  }),
  getters: {
    getUserInfo: (state) => state.userInfo,
    getRole: (state) => state.role
  },
  actions: {
    // 初始化：从localStorage恢复登录状态
    initialize() {
      const token = localStorage.getItem('token')
      const role = localStorage.getItem('role')
      const userInfoStr = localStorage.getItem('userInfo')
      
      if (token && role && userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr)
          this.isLoggedIn = true
          this.role = role as 'student' | 'teacher' | 'admin'
          this.token = token
          this.userInfo = userInfo
        } catch (e) {
          console.error('Failed to restore user state from localStorage:', e)
          this.logout()
        }
      }
    },
    // 登录
    login(role: 'student' | 'teacher' | 'admin', userInfo: {
        id: string
        name: string
        email: string
        avatar?: string
        phone?: string
        bio?: string
      }, token: string) {
        this.isLoggedIn = true
        this.role = role
        this.token = token
        this.userInfo = userInfo
        // 保存到localStorage
        localStorage.setItem('token', token)
        localStorage.setItem('role', role)
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    // 登出
    logout() {
      this.isLoggedIn = false
      this.role = ''
      this.token = ''
      this.userInfo = {
        id: '',
        name: '',
        email: ''
      }
      // 从localStorage中删除
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('userInfo')
    }
  }
})