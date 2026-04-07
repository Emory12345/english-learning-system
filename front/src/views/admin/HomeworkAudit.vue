<template>
  <div class="homework-audit">
    <h2>作业审核</h2>
    
    <!-- 作业列表 -->
    <el-card class="homeworks-card">
      <el-table :data="homeworks" style="width: 100%">
        <el-table-column prop="id" label="作业ID" />
        <el-table-column prop="title" label="作业标题" />
        <el-table-column prop="teacher.name" label="教师" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="createdAt" label="发布时间">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewHomework(scope.row)">查看详情</el-button>
            <el-button size="small" type="primary" @click="approveHomework(scope.row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="rejectHomework(scope.row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="homeworks.length"
          :page-size="10"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

const currentPage = ref(1)
const homeworks = ref<any[]>([])

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 获取待审核作业列表
const fetchPendingHomeworks = async () => {
  try {
    const response = await api.admin.getPendingHomeworks()
    homeworks.value = response
  } catch (error) {
    console.error('获取待审核作业失败:', error)
    ElMessage.error('获取待审核作业失败')
  }
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 查看作业详情
const viewHomework = (homework: any) => {
  ElMessage.info(`作业详情: ${homework.title}`)
}

// 通过作业
const