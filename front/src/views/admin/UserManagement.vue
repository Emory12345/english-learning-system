<template>
  <div class="user-management">
    <h2>用户管理</h2>
    
    <!-- 搜索和筛选 -->
    <el-card class="filter-card">
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="用户类型">
            <el-select v-model="filterForm.role" placeholder="请选择用户类型">
              <el-option label="全部" value="" />
              <el-option label="学生" value="student" />
              <el-option label="教师" value="teacher" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input v-model="filterForm.keyword" placeholder="搜索用户名或邮箱" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="users-card">
      <el-table :data="filteredUsers" style="width: 100%">
        <el-table-column prop="id" label="用户ID" />
        <el-table-column prop="name" label="用户名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="用户类型" />
        <el-table-column prop="registerTime" label="注册时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="editUser(scope.row.id)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="users.length"
          :page-size="10"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const currentPage = ref(1)

// 筛选表单
const filterForm = ref({
  role: '',
  keyword: ''
})

// 模拟用户数据
const users = ref([
  {
    id: '1',
    name: '张三',
    email: 'zhangsan@example.com',
    role: 'student',
    registerTime: '2024-01-01 10:00'
  },
  {
    id: '2',
    name: '李四',
    email: 'lisi@example.com',
    role: 'teacher',
    registerTime: '2024-01-02 11:00'
  },
  {
    id: '3',
    name: '王五',
    email: 'wangwu@example.com',
    role: 'student',
    registerTime: '2024-01-03 12:00'
  },
  {
    id: '4',
    name: '赵六',
    email: 'zhaoliu@example.com',
    role: 'student',
    registerTime: '2024-01-04 13:00'
  },
  {
    id: '5',
    name: '孙七',
    email: 'sunqi@example.com',
    role: 'teacher',
    registerTime: '2024-01-05 14:00'
  }
])

// 筛选后的用户
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesRole = !filterForm.value.role || user.role === filterForm.value.role
    const matchesKeyword = !filterForm.value.keyword || 
      user.name.toLowerCase().includes(filterForm.value.keyword.toLowerCase()) || 
      user.email.toLowerCase().includes(filterForm.value.keyword.toLowerCase())
    return matchesRole && matchesKeyword
  })
})

// 搜索用户
const handleSearch = () => {
  currentPage.value = 1
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 编辑用户
const editUser = (userId: string) => {
  // 实际项目中这里会打开编辑用户的弹窗
  ElMessage.info(`编辑用户：${userId}`)
}

// 删除用户
const deleteUser = (userId: string) => {
  // 实际项目中这里会弹出确认框
  ElMessage.info(`删除用户：${userId}`)
}
</script>

<style scoped>
.user-management {
  padding: 20px 0;
}

.user-management h2 {
  margin-bottom: 20px;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  align-items: center;
}

.users-card {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

@media screen and (max-width: 768px) {
  .filter-form {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
}
</style>