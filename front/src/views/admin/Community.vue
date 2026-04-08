<template>
  <div class="community">
    <h2>学习社区管理</h2>
    
    <!-- 发布帖子 -->
    <el-card class="post-card">
      <el-form :model="postForm">
        <el-form-item>
          <el-input
            v-model="postForm.title"
            placeholder="请输入帖子标题"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入帖子内容"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="publishPost" :loading="publishing">发布帖子</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 帖子列表 -->
    <el-card class="posts-card">
      <div class="posts-list">
        <div v-for="post in posts" :key="post.id" class="post-item">
          <!-- 置顶标签 -->
          <div v-if="post.top" class="top-badge">
            <el-tag type="danger" effect="dark">置顶</el-tag>
          </div>
          
          <div class="post-header">
            <div class="user-info">
              <img :src="getFullAvatarUrl(post.author?.avatar)" :alt="post.author?.name" class="avatar" />
              <div>
                <h4>{{ post.author?.name || '匿名用户' }}</h4>
                <span class="post-time">{{ formatTime(post.createdAt) }}</span>
              </div>
            </div>
            
            <!-- 操作按钮 -->
            <div class="post-actions">
              <!-- 置顶/取消置顶按钮 -->
              <el-button 
                type="text" 
                size="small"
                @click="toggleTop(post)"
              >
                {{ post.top ? '取消置顶' : '置顶' }}
              </el-button>
              
              <!-- 删除按钮 -->
              <el-button 
                type="text" 
                size="small"
                @click="deletePost(post.id)"
              >
                删除
              </el-button>
            </div>
          </div>
          
          <div class="post-content">
            <h3>{{ post.title }}</h3>
            <p>{{ post.content }}</p>
          </div>
          <div class="post-footer">
            <div class="post-stats">
              <span class="stat-item" @click="toggleComments(post.id)">
                <el-icon><ChatDotRound /></el-icon>
                {{ post.commentCount || 0 }} 评论
              </span>
              <span class="stat-item">
                <el-icon><Star /></el-icon>
                {{ post.likeCount || 0 }} 点赞
              </span>
              <span class="stat-item">
                <el-icon><Collection /></el-icon>
                {{ post.collectCount || 0 }} 收藏
              </span>
            </div>
          </div>
          
          <!-- 评论区 -->
          <div class="comments-section" v-if="expandedPostId === post.id">
            <div class="comments-list">
              <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <div class="comment-user-info">
                    <img :src="getFullAvatarUrl(comment.author?.avatar)" :alt="comment.author?.name" class="comment-avatar" />
                    <div>
                      <h5>{{ comment.author?.name || '匿名用户' }}</h5>
                      <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                    </div>
                  </div>
                </div>
                <div class="comment-content">
                  {{ comment.content }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatDotRound, Star, Collection } from '@element-plus/icons-vue'
import { api } from '@/api'

const publishing = ref(false)
const expandedPostId = ref<string | null>(null)

// 发布帖子表单
const postForm = ref({
  title: '',
  content: ''
})

const API_BASE_URL = 'http://localhost:8080'

// 计算完整的头像URL
const getFullAvatarUrl = (avatar: string | undefined) => {
  if (!avatar) {
    return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar&image_size=square'
  }
  if (avatar.startsWith('http')) {
    return avatar
  }
  return `${API_BASE_URL}${avatar}`
}

// 帖子数据
const posts = ref<any[]>([])

// 从后端获取帖子数据
const fetchPosts = async () => {
  try {
    const data = await api.community.getPosts()
    posts.value = data.map((post: any) => ({
      ...post,
      commentCount: post.commentCount || 0,
      likeCount: post.likeCount || 0,
      collectCount: post.collectCount || 0,
      comments: [],
      top: post.top || false
    }))
  } catch (error) {
    console.error('Failed to fetch posts:', error)
    ElMessage.error('获取帖子失败')
  }
}

// 发布帖子
const publishPost = async () => {
  if (postForm.value.title && postForm.value.content) {
    try {
      publishing.value = true
      await api.community.createPost(postForm.value.title, postForm.value.content, '')
      ElMessage.success('发布成功')
      postForm.value.title = ''
      postForm.value.content = ''
      // 重新获取帖子数据
      await fetchPosts()
    } catch (error: any) {
      ElMessage.error(error.message || '发布失败')
    } finally {
      publishing.value = false
    }
  } else {
    ElMessage.error('请填写标题和内容')
  }
}

// 删除帖子
const deletePost = async (postId: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这个帖子吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.community.deletePost(postId)
    ElMessage.success('删除成功')
    await fetchPosts()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 置顶/取消置顶帖子
const toggleTop = async (post: any) => {
  try {
    await api.community.toggleTopPost(post.id.toString(), !post.top)
    ElMessage.success(post.top ? '取消置顶成功' : '置顶成功')
    await fetchPosts()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 切换评论区
const toggleComments = (postId: string) => {
  if (expandedPostId.value === postId) {
    expandedPostId.value = null
  } else {
    expandedPostId.value = postId
    fetchComments(postId)
  }
}

// 获取帖子评论
const fetchComments = async (postId: string) => {
  try {
    const comments = await api.community.getComments(postId)
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      post.comments = comments.map((comment: any) => ({
        ...comment,
        likeCount: comment.likeCount || 0
      }))
      post.commentCount = comments.length
    }
  } catch (error) {
    console.error('Failed to fetch comments:', error)
    ElMessage.error('获取评论失败')
  }
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 组件挂载时获取帖子数据
onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.community {
  padding: 20px 0;
}

.community h2 {
  margin-bottom: 20px;
  color: #303133;
}

.post-card {
  margin-bottom: 20px;
}

.posts-card {
  margin-bottom: 20px;
}

.posts-list {
  margin-bottom: 20px;
}

.post-item {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  transition: all 0.3s;
  position: relative;
}

.post-item:hover {
  background-color: #f5f7fa;
  border-radius: 4px;
}

.post-item:last-child {
  border-bottom: none;
}

.top-badge {
  position: absolute;
  top: 10px;
  right: 10px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
}

.user-info h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  font-weight: 600;
}

.post-time {
  font-size: 12px;
  color: #909399;
}

.post-actions {
  display: flex;
  gap: 10px;
}

.post-content h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
}

.post-content p {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #909399;
  cursor: pointer;
  transition: color 0.3s;
}

.stat-item:hover {
  color: #409eff;
}

.stat-item .el-icon {
  margin-right: 5px;
  font-size: 14px;
}

.comments-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.comments-list {
  margin-bottom: 20px;
}

.comment-item {
  padding: 15px;
  background-color: #f9fafc;
  border-radius: 4px;
  margin-bottom: 10px;
}

.comment-header {
  margin-bottom: 10px;
}

.comment-user-info {
  display: flex;
  align-items: center;
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
}

.comment-user-info h5 {
  margin: 0 0 3px 0;
  font-size: 13px;
  font-weight: 600;
}

.comment-time {
  font-size: 11px;
  color: #909399;
}

.comment-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 10px;
}

@media screen and (max-width: 768px) {
  .post-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .post-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .post-stats {
    width: 100%;
    justify-content: space-around;
  }
}
</style>
