<template>
  <div class="community">
    <!-- 顶部导航栏 -->
    <div class="community-header">
      <div class="logo">
        <span class="logo-text">学习社区</span>
      </div>
      <div class="search-box">
        <el-input placeholder="搜索话题、用户" size="small" />
      </div>
      <div class="header-actions">
        <el-button type="primary" size="small">发布</el-button>
      </div>
    </div>
    
    <div class="community-content">
      <!-- 左侧边栏 -->
      <div class="left-sidebar">
        <div class="sidebar-section">
          <h3>关注</h3>
          <ul class="sidebar-menu">
            <li class="active">全部关注</li>
            <li>最新帖子</li>
            <li>特别关注</li>
            <li>好友圈</li>
          </ul>
        </div>
        <div class="sidebar-section">
          <h3>自定义分组</h3>
          <ul class="sidebar-menu">
            <li>名人明星</li>
            <li>同事</li>
            <li>同学</li>
            <li>悄悄关注</li>
          </ul>
        </div>
      </div>
      
      <!-- 中间内容区 -->
      <div class="main-content">
        <!-- 发布帖子 -->
        <div class="post-create">
          <div class="user-avatar">
            <img :src="getFullAvatarUrl(userStore.userInfo?.avatar)" :alt="userStore.userInfo?.name" />
          </div>
          <div class="post-input">
            <el-input
              v-model="postForm.content"
              type="textarea"
              :rows="2"
              placeholder="有什么新鲜事想分享给大家？"
            />
            <div class="post-actions">
              <div class="action-buttons">
                <el-button type="text" size="small">
                  <el-icon><Picture /></el-icon> 图片
                </el-button>
                <el-button type="text" size="small">
                  <el-icon><VideoCamera /></el-icon> 视频
                </el-button>
                <el-button type="text" size="small">
                  <el-icon><Document /></el-icon> 文档
                </el-button>
                <el-button type="text" size="small">
                  <el-icon><Location /></el-icon> 地点
                </el-button>
              </div>
              <el-button type="primary" size="small" @click="publishPost" :loading="publishing">发布</el-button>
            </div>
          </div>
        </div>
        
        <!-- 帖子列表 -->
        <div class="posts-list">
          <div v-for="post in posts" :key="post.id" class="post-item">

            
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
                <!-- 关注按钮 -->
                <el-button 
                  v-if="!isFollowing(post.author?.id)"
                  type="primary" 
                  size="small"
                  @click="followUser(post.author?.id)"
                >
                  关注
                </el-button>
                <el-button 
                  v-else
                  type="default" 
                  size="small"
                  @click="unfollowUser(post.author?.id)"
                >
                  已关注
                </el-button>
                
                <!-- 管理员置顶/取消置顶按钮 -->
                <el-button 
                  v-if="userStore.userInfo?.role === 'admin'"
                  type="text" 
                  size="small"
                  @click="toggleTop(post)"
                >
                  {{ post.top ? '取消置顶' : '置顶' }}
                </el-button>
                
                <!-- 删除按钮（作者或管理员可见） -->
                <el-button 
                  v-if="canDeletePost(post)"
                  type="text" 
                  size="small"
                  @click="deletePost(post.id)"
                >
                  删除
                </el-button>
              </div>
            </div>
            
            <div class="post-content">
              <p>{{ post.content }}</p>
            </div>
            <div class="post-footer">
              <div class="post-stats">
                <span class="stat-item" @click="toggleComments(post.id)">
                  <el-icon><ChatDotRound /></el-icon>
                  {{ post.commentCount || 0 }}
                </span>
                <span class="stat-item" @click="likePost(post.id)">
                  <el-icon :class="{ 'liked': post.liked }"><Star /></el-icon>
                  {{ post.likeCount || 0 }}
                </span>
                <span class="stat-item" @click="collectPost(post.id)">
                  <el-icon :class="{ 'collected': post.collected }"><Collection /></el-icon>
                  {{ post.collectCount || 0 }}
                </span>
                <span class="stat-item">
                  <el-icon><Share /></el-icon>
                  分享
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
                  <div class="comment-footer">
                    <span class="comment-reply" @click="replyToComment(comment.id)">回复</span>
                    <span class="comment-like">点赞</span>
                  </div>
                </div>
              </div>
              
              <!-- 发表评论 -->
              <div class="comment-form">
                <div class="comment-input-wrapper">
                  <img :src="getFullAvatarUrl(userStore.userInfo?.avatar)" :alt="userStore.userInfo?.name" class="comment-avatar" />
                  <el-input
                    v-model="commentForm.content"
                    type="textarea"
                    :rows="1"
                    placeholder="写下你的评论..."
                  />
                </div>
                <div class="comment-form-actions">
                  <el-button v-if="replyingToCommentId" type="text" @click="cancelReply">取消回复</el-button>
                  <el-button type="primary" size="small" @click="submitComment(post.id)">发表评论</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 右侧边栏 -->
      <div class="right-sidebar">
        <!-- 热门话题 -->
        <div class="sidebar-section">
          <div class="section-header">
            <h3>热门话题</h3>
            <a href="#" class="more-link">更多</a>
          </div>
          <ul class="hot-topics">
            <li v-for="(topic, index) in hotTopics" :key="index" class="topic-item">
              <span class="topic-rank">{{ index + 1 }}</span>
              <div class="topic-content">
                <a href="#" class="topic-title">{{ topic.title }}</a>
                <span class="topic-count">{{ topic.count }} 讨论</span>
              </div>
            </li>
          </ul>
        </div>
        
        <!-- 你可能感兴趣的人 -->
        <div class="sidebar-section">
          <div class="section-header">
            <h3>你可能感兴趣的人</h3>
            <a href="#" class="more-link">换一批</a>
          </div>
          <ul class="recommended-users">
            <li v-for="(user, index) in recommendedUsers" :key="index" class="user-item">
              <img :src="getFullAvatarUrl(user.avatar)" :alt="user.name" class="user-avatar" />
              <div class="user-info">
                <h4>{{ user.name }}</h4>
                <p>{{ user.description }}</p>
              </div>
              <el-button type="primary" size="small">关注</el-button>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ChatDotRound, Star, Collection, Picture, VideoCamera, Document, Location, Share } from '@element-plus/icons-vue'
import { api } from '@/api'
import { useUserStore } from '@/stores/user'

const publishing = ref(false)
const expandedPostId = ref<string | null>(null)
const replyingToCommentId = ref<string | null>(null)
const userStore = useUserStore()

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

// 发布帖子表单
const postForm = ref({
  content: ''
})

// 评论表单
const commentForm = ref({
  content: ''
})

// 帖子数据
const posts = ref<any[]>([])

// 关注状态
const followingUsers = ref<Set<string>>(new Set())

// 热门话题
const hotTopics = ref([
  { title: '考研英语复习技巧', count: 12543 },
  { title: '如何提高英语口语', count: 9876 },
  { title: '英语学习方法分享', count: 8765 },
  { title: '英语听力训练', count: 7654 },
  { title: '英语词汇记忆', count: 6543 }
])

// 推荐用户
const recommendedUsers = ref([
  { name: '英语学习达人', avatar: '', description: '专注英语教学10年' },
  { name: '考研英语专家', avatar: '', description: '考研英语辅导名师' },
  { name: '英语口语教练', avatar: '', description: '地道美式英语发音' }
])

// 检查是否可以删除帖子
const canDeletePost = (post: any) => {
  if (!userStore.userInfo) return false
  if (userStore.userInfo.role === 'admin') return true
  return post.author?.id === userStore.userInfo.id
}

// 检查是否已关注用户
const isFollowing = (userId: string | undefined) => {
  if (!userId) return false
  return followingUsers.value.has(userId)
}

// 关注用户
const followUser = async (userId: string | undefined) => {
  if (!userId) return
  try {
    // 模拟关注操作
    followingUsers.value.add(userId)
    ElMessage.success('关注成功')
  } catch (error) {
    console.error('Failed to follow user:', error)
    ElMessage.error('关注失败')
  }
}

// 取消关注用户
const unfollowUser = async (userId: string | undefined) => {
  if (!userId) return
  try {
    // 模拟取消关注操作
    followingUsers.value.delete(userId)
    ElMessage.success('取消关注成功')
  } catch (error) {
    console.error('Failed to unfollow user:', error)
    ElMessage.error('取消关注失败')
  }
}

// 从后端获取帖子数据
const fetchPosts = async () => {
  try {
    const data = await api.community.getPosts()
    // 为每个帖子添加评论数、点赞数和点赞状态
    posts.value = data.map((post: any) => ({
      ...post,
      commentCount: post.commentCount || 0,
      likeCount: post.likeCount || 0,
      collectCount: post.collectCount || 0,
      liked: false,
      collected: false,
      comments: [],
      top: post.top || false
    }))
    // 检查用户是否已点赞或收藏
    if (userStore.userInfo) {
      for (const post of posts.value) {
        try {
          const liked = await api.community.checkInteraction(post.id.toString(), 'like')
          post.liked = liked
          const collected = await api.community.checkInteraction(post.id.toString(), 'collect')
          post.collected = collected
        } catch (error) {
          console.error('Failed to check interaction:', error)
        }
      }
    }
  } catch (error) {
    console.error('Failed to fetch posts:', error)
    ElMessage.error('获取帖子失败')
  }
}

// 发布帖子
const publishPost = async () => {
  if (postForm.value.content) {
    try {
      publishing.value = true
      // 模拟发布帖子
      await api.community.createPost('新帖子', postForm.value.content, '')
      ElMessage.success('发布成功')
      postForm.value.content = ''
      // 重新获取帖子数据
      await fetchPosts()
    } catch (error: any) {
      ElMessage.error(error.message || '发布失败')
    } finally {
      publishing.value = false
    }
  } else {
    ElMessage.error('请填写内容')
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
    // 获取帖子评论
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
        likeCount: comment.likeCount || 0,
        liked: false
      }))
      post.commentCount = comments.length
    }
  } catch (error) {
    console.error('Failed to fetch comments:', error)
    ElMessage.error('获取评论失败')
  }
}

// 发表评论
const submitComment = async (postId: string) => {
  if (commentForm.value.content) {
    try {
      await api.community.createComment(postId, commentForm.value.content, replyingToCommentId.value || undefined)
      ElMessage.success('评论成功')
      commentForm.value.content = ''
      replyingToCommentId.value = null
      // 重新获取评论
      await fetchComments(postId)
    } catch (error: any) {
      ElMessage.error(error.message || '评论失败')
    }
  } else {
    ElMessage.error('请填写评论内容')
  }
}

// 回复评论
const replyToComment = (commentId: string) => {
  replyingToCommentId.value = commentId
}

// 取消回复
const cancelReply = () => {
  replyingToCommentId.value = null
}

// 点赞帖子
const likePost = async (postId: string) => {
  try {
    await api.community.likePost(postId)
    // 更新前端数据
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      post.liked = !post.liked
      post.likeCount += post.liked ? 1 : -1
    }
    ElMessage.success(post?.liked ? '点赞成功' : '取消点赞成功')
  } catch (error) {
    console.error('Failed to like post:', error)
    ElMessage.error('操作失败')
  }
}

// 收藏帖子
const collectPost = async (postId: string) => {
  try {
    await api.community.collectPost(postId)
    // 更新前端数据
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      post.collected = !post.collected
      post.collectCount += post.collected ? 1 : -1
    }
    ElMessage.success(post?.collected ? '收藏成功' : '取消收藏成功')
  } catch (error) {
    console.error('Failed to collect post:', error)
    ElMessage.error('操作失败')
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
/* 全局样式 */
.community {
  min-height: 100vh;
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 顶部导航栏 */
.community-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #e6162d;
}

.search-box {
  flex: 1;
  max-width: 400px;
  margin: 0 20px;
}

.search-box .el-input {
  width: 100%;
}

.header-actions {
  display: flex;
  align-items: center;
}

/* 主内容区域 */
.community-content {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  gap: 20px;
}

/* 左侧边栏 */
.left-sidebar {
  width: 200px;
  flex-shrink: 0;
}

.sidebar-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.sidebar-section h3 {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-menu li {
  padding: 8px 0;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s;
}

.sidebar-menu li:hover {
  color: #e6162d;
}

.sidebar-menu li.active {
  color: #e6162d;
  font-weight: 600;
}

/* 中间内容区 */
.main-content {
  flex: 1;
  min-width: 0;
}

/* 发布帖子 */
.post-create {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 12px;
}

.user-avatar img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.post-input {
  flex: 1;
}

.post-input .el-textarea {
  margin-bottom: 10px;
}

.post-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.action-buttons .el-button {
  color: #666;
}

.action-buttons .el-button:hover {
  color: #e6162d;
}

/* 帖子列表 */
.posts-list {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.post-item {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
  position: relative;
}

.post-item:hover {
  background-color: #fafafa;
}

.post-item:last-child {
  border-bottom: none;
}



.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info h4 {
  margin: 0 0 2px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-actions {
  display: flex;
  gap: 8px;
}

.post-content {
  margin-bottom: 12px;
}

.post-content p {
  margin: 0;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.post-stats {
  display: flex;
  gap: 20px;
  flex: 1;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
  gap: 4px;
}

.stat-item:hover {
  color: #e6162d;
}

.liked {
  color: #e6162d !important;
}

.collected {
  color: #ff9500 !important;
}

/* 评论区 */
.comments-section {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.comments-list {
  margin-bottom: 15px;
}

.comment-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  margin-bottom: 8px;
}

.comment-user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-user-info h5 {
  margin: 0 0 2px 0;
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.comment-time {
  font-size: 11px;
  color: #999;
}

.comment-content {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  margin-bottom: 8px;
  padding-left: 40px;
}

.comment-footer {
  display: flex;
  gap: 15px;
  padding-left: 40px;
}

.comment-reply, .comment-like {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
}

.comment-reply:hover, .comment-like:hover {
  color: #e6162d;
}

.comment-form {
  margin-top: 15px;
}

.comment-input-wrapper {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.comment-form .el-textarea {
  flex: 1;
}

.comment-form-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 右侧边栏 */
.right-sidebar {
  width: 250px;
  flex-shrink: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-header h3 {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.more-link {
  font-size: 12px;
  color: #999;
  text-decoration: none;
  transition: color 0.3s;
}

.more-link:hover {
  color: #e6162d;
}

/* 热门话题 */
.hot-topics {
  list-style: none;
  padding: 0;
  margin: 0;
}

.topic-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.topic-item:last-child {
  border-bottom: none;
}

.topic-rank {
  font-size: 16px;
  font-weight: bold;
  color: #999;
  min-width: 20px;
}

.topic-rank:nth-child(-n+3) {
  color: #e6162d;
}

.topic-content {
  flex: 1;
}

.topic-title {
  font-size: 13px;
  color: #333;
  text-decoration: none;
  line-height: 1.4;
  display: block;
  margin-bottom: 4px;
  transition: color 0.3s;
}

.topic-title:hover {
  color: #e6162d;
}

.topic-count {
  font-size: 11px;
  color: #999;
}

/* 推荐用户 */
.recommended-users {
  list-style: none;
  padding: 0;
  margin: 0;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.user-item:last-child {
  border-bottom: none;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-info h4 {
  margin: 0 0 2px 0;
  font-size: 13px;
  font-weight: 600;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-info p {
  margin: 0;
  font-size: 11px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .community-content {
    flex-direction: column;
  }
  
  .left-sidebar,
  .right-sidebar {
    width: 100%;
  }
  
  .left-sidebar {
    order: 1;
  }
  
  .main-content {
    order: 2;
  }
  
  .right-sidebar {
    order: 3;
  }
}

@media screen and (max-width: 768px) {
  .community-header {
    padding: 10px;
  }
  
  .search-box {
    margin: 0 10px;
  }
  
  .community-content {
    padding: 10px;
    gap: 10px;
  }
  
  .post-create {
    padding: 10px;
  }
  
  .post-item {
    padding: 12px;
  }
  
  .sidebar-section {
    padding: 12px;
  }
}
</style>
