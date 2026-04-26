<template>
  <div class="community">
    <h2>学习社区管理</h2>
    
    <!-- 发布帖子 -->
    <el-card class="post-card">
      <el-form :model="postForm">
        <el-form-item>
          <el-input
            v-model="postForm.title"
            placeholder="有什么新鲜事想分享给大家？"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="4"
            placeholder="分享你的学习心得、经验或问题..."
          />
        </el-form-item>
        <el-form-item>
          <div class="post-actions-row">
            <!-- 功能按钮 -->
            <div class="action-buttons">
              <el-button
                type="text"
                @click="selectImage"
                title="添加图片"
              >
                <el-icon><Picture /></el-icon>
                <span>图片</span>
              </el-button>
              <el-button
                type="text"
                @click="selectVideo"
                title="添加视频"
              >
                <el-icon><VideoCamera /></el-icon>
                <span>视频</span>
              </el-button>
              <el-button
                type="text"
                @click="selectDocument"
                title="添加文档"
              >
                <el-icon><Document /></el-icon>
                <span>文档</span>
              </el-button>
              <el-button
                type="text"
                @click="selectLocation"
                title="添加地点"
              >
                <el-icon><Location /></el-icon>
                <span>地点</span>
              </el-button>
            </div>
            <!-- 发布按钮 -->
            <el-button type="primary" @click="publishPost" :loading="publishing">发布帖子</el-button>
          </div>
        </el-form-item>
        <!-- 附件预览 -->
        <el-form-item v-if="attachments.length > 0">
          <div class="attachments-preview">
            <div v-for="(attachment, index) in attachments" :key="index" class="attachment-item">
              <div class="attachment-info">
                <el-icon v-if="attachment.type === 'image'">
                  <Picture />
                </el-icon>
                <el-icon v-else-if="attachment.type === 'video'">
                  <VideoCamera />
                </el-icon>
                <el-icon v-else-if="attachment.type === 'document'">
                  <Document />
                </el-icon>
                <span class="attachment-name">{{ attachment.name }}</span>
              </div>
              <el-button type="text" size="small" @click="removeAttachment(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </el-form-item>
        <!-- 地点信息 -->
        <el-form-item v-if="postForm.location">
          <div class="location-info">
            <el-icon><Location /></el-icon>
            <span>{{ postForm.location }}</span>
            <el-button type="text" size="small" @click="removeLocation">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <!-- 文件上传输入 -->
      <input
        ref="fileInput"
        type="file"
        multiple
        style="display: none"
        @change="handleFileSelect"
      />
    </el-card>
    
    <!-- 地点选择对话框 -->
    <el-dialog
      v-model="locationDialogVisible"
      title="选择地点"
      width="400px"
    >
      <el-input
        v-model="locationInput"
        placeholder="请输入地点"
        style="margin-bottom: 20px"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="locationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmLocation">确认</el-button>
        </div>
      </template>
    </el-dialog>
    
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
import { ElMessage, ElMessageBox, ElDialog, ElInput } from 'element-plus'
import { ChatDotRound, Star, Collection, Picture, VideoCamera, Document, Location, Delete } from '@element-plus/icons-vue'
import { api } from '@/api'

const publishing = ref(false)
const expandedPostId = ref<string | null>(null)
const fileInput = ref<HTMLInputElement | null>(null)

// 发布帖子表单
const postForm = ref({
  title: '',
  content: '',
  location: ''
})

// 附件列表
const attachments = ref<any[]>([])

// 地点选择对话框
const locationDialogVisible = ref(false)
const locationInput = ref('')

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

// 选择图片
const selectImage = () => {
  if (fileInput.value) {
    fileInput.value.accept = 'image/*'
    fileInput.value.multiple = true
    fileInput.value.click()
  }
}

// 选择视频
const selectVideo = () => {
  if (fileInput.value) {
    fileInput.value.accept = 'video/*'
    fileInput.value.multiple = false
    fileInput.value.click()
  }
}

// 选择文档
const selectDocument = () => {
  if (fileInput.value) {
    fileInput.value.accept = '.doc,.docx,.pdf,.txt,.md'
    fileInput.value.multiple = true
    fileInput.value.click()
  }
}

// 处理文件选择
const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  if (files) {
    for (let i = 0; i < files.length; i++) {
      const file = files[i]
      let type = 'document'
      if (file.type.startsWith('image/')) {
        type = 'image'
      } else if (file.type.startsWith('video/')) {
        type = 'video'
      }
      attachments.value.push({
        name: file.name,
        type: type,
        file: file
      })
    }
  }
  // 清空input的值，以便可以重复选择同一个文件
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// 移除附件
const removeAttachment = (index: number) => {
  attachments.value.splice(index, 1)
}

// 选择地点
const selectLocation = () => {
  locationDialogVisible.value = true
}

// 确认地点
const confirmLocation = () => {
  if (locationInput.value.trim()) {
    postForm.value.location = locationInput.value.trim()
    locationDialogVisible.value = false
    locationInput.value = ''
  } else {
    ElMessage.error('请输入地点')
  }
}

// 移除地点
const removeLocation = () => {
  postForm.value.location = ''
}

// 发布帖子
const publishPost = async () => {
  if (postForm.value.title && postForm.value.content) {
    try {
      publishing.value = true
      
      // 处理附件上传
      if (attachments.value.length > 0) {
        for (const attachment of attachments.value) {
          if (attachment.type === 'image') {
            await api.upload.image(attachment.file)
          } else if (attachment.type === 'video') {
            await api.upload.video(attachment.file, '', '', '', '')
          }
          // 文档上传暂时跳过
        }
      }
      
      await api.community.createPost(postForm.value.title, postForm.value.content, '')
      ElMessage.success('发布成功')
      postForm.value.title = ''
      postForm.value.content = ''
      postForm.value.location = ''
      attachments.value = []
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

/* 功能按钮行 */
.post-actions-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.action-buttons {
  display: flex;
  gap: 20px;
}

.action-buttons .el-button {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #606266;
}

.action-buttons .el-button:hover {
  color: #409eff;
}

.action-buttons .el-icon {
  font-size: 16px;
}

/* 附件预览 */
.attachments-preview {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 10px;
  background-color: #f9fafc;
}

.attachment-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #e4e7ed;
}

.attachment-item:last-child {
  border-bottom: none;
}

.attachment-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.attachment-info .el-icon {
  font-size: 18px;
  color: #409eff;
}

.attachment-name {
  font-size: 14px;
  color: #606266;
}

/* 地点信息 */
.location-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  background-color: #f9fafc;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.location-info .el-icon {
  font-size: 16px;
  color: #409eff;
}

.location-info span {
  font-size: 14px;
  color: #606266;
  flex: 1;
}

/* 对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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
