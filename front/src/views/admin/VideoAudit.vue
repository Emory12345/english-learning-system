<template>
  <div class="video-audit">
    <h2>课程审核</h2>
    
    <!-- 视频列表 -->
    <el-card class="videos-card">
      <el-table :data="videos" style="width: 100%">
        <el-table-column prop="id" label="视频ID" />
        <el-table-column prop="title" label="视频标题" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="createdAt" label="创建时间">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="viewVideo(scope.row)">查看详情</el-button>
            <el-button size="small" type="primary" @click="approveVideo(scope.row.id)">通过</el-button>
            <el-button size="small" type="danger" @click="rejectVideo(scope.row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="videos.length"
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
const videos = ref<any[]>([])

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 获取待审核视频列表
const fetchPendingVideos = async () => {
  try {
    const response = await api.admin.getPendingVideos()
    videos.value = response
  } catch (error) {
    console.error('获取待审核视频失败:', error)
    ElMessage.error('获取待审核视频失败')
  }
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 查看视频详情
const viewVideo = (video: any) => {
  ElMessage.info(`视频详情: ${video.title}`)
  // 可以打开一个新窗口播放视频
  if (video.videoUrl) {
    const videoUrl = `http://localhost:8080${video.videoUrl}`
    window.open(videoUrl, '_blank', 'width=800,height=600')
  }
}

// 通过视频
const approveVideo = async (videoId: number) => {
  try {
    await api.admin.auditVideo({ videoId: videoId.toString(), status: 'approved' })
    ElMessage.success('视频审核通过')
    await fetchPendingVideos()
  } catch (error) {
    console.error('视频审核失败:', error)
    ElMessage.error('视频审核失败')
  }
}

// 拒绝视频
const rejectVideo = async (videoId: number) => {
  try {
    await api.admin.auditVideo({ videoId: videoId.toString(), status: 'rejected' })
    ElMessage.success('视频已拒绝')
    await fetchPendingVideos()
  } catch (error) {
    console.error('视频拒绝失败:', error)
    ElMessage.error('视频拒绝失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchPendingVideos()
})
</script>

<style scoped>
.video-audit {
  padding: 20px 0;
}

.video-audit h2 {
  margin-bottom: 20px;
  color: #303133;
}

.videos-card {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>