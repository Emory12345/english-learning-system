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
        <el-table-column prop="password" label="密码" />
        <el-table-column prop="role" label="用户类型" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="filteredUsersCount"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 编辑用户弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑用户"
      width="500px"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="editForm.password" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="editForm.role" placeholder="请选择用户类型">
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option label="活跃" value="active" />
            <el-option label="禁用" value="inactive" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'

const currentPage = ref(1)

// 筛选表单
const filterForm = ref({
  role: '',
  keyword: ''
})

// 用户数据
const users = ref<any[]>([])

// 页面大小
const pageSize = ref(10)

// 编辑弹窗可见性
const editDialogVisible = ref(false)

// 编辑表单数据
const editForm = ref({
  id: '',
  name: '',
  email: '',
  password: '',
  role: '',
  status: ''
})

// 筛选后的用户
const filteredUsers = computed(() => {
  const filtered = users.value.filter(user => {
    const matchesRole = !filterForm.value.role || user.role === filterForm.value.role
    const matchesKeyword = !filterForm.value.keyword || 
      user.name.toLowerCase().includes(filterForm.value.keyword.toLowerCase()) || 
      user.email.toLowerCase().includes(filterForm.value.keyword.toLowerCase())
    return matchesRole && matchesKeyword
  })
  
  // 计算分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filtered.slice(start, end)
})

// 筛选后的用户总数
const filteredUsersCount = computed(() => {
  return users.value.filter(user => {
    const matchesRole = !filterForm.value.role || user.role === filterForm.value.role
    const matchesKeyword = !filterForm.value.keyword || 
      user.name.toLowerCase().includes(filterForm.value.keyword.toLowerCase()) || 
      user.email.toLowerCase().includes(filterForm.value.keyword.toLowerCase())
    return matchesRole && matchesKeyword
  }).length
})

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await api.users.getList()
    users.value = response
  } catch (error: any) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  }
}

// 搜索用户
const handleSearch = () => {
  currentPage.value = 1
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 编辑用户
const editUser = (user: any) => {
  // 填充编辑表单
  editForm.value = {
    id: user.id,
    name: user.name,
    email: user.email,
    password: user.password,
    role: user.role,
    status: user.status
  }
  // 显示编辑弹窗
  editDialogVisible.value = true
}

// 保存用户
const saveUser = async () => {
  try {
    // 调用后端API更新用户信息
    await api.users.update(editForm.value.id, editForm.value)
    ElMessage.success('用户信息更新成功')
    // 关闭弹窗
    editDialogVisible.value = false
    // 重新获取用户列表
    await fetchUsers()
  } catch (error: any) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('更新用户信息失败')
  }
}

// 删除用户
const deleteUser = async (userId: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await api.users.delete(userId)
    ElMessage.success('删除成功')
    // 重新获取用户列表
    await fetchUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 页面加载时获取用户列表
onMounted(() => {
  fetchUsers()
})
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