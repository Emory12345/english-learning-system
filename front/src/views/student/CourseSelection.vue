<template>
  <div class="course-selection">
    <h2>选课学习</h2>
    
    <!-- 课程筛选 -->
    <el-card class="filter-card">
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="课程类型">
            <el-select v-model="filterForm.type" placeholder="请选择课程类型">
              <el-option label="全部" value="" />
              <el-option label="青少年英语" value="teenage" />
              <el-option label="考研英语" value="kaoyan" />
              <el-option label="四六级英语" value="cet" />
              <el-option label="雅思托福" value="ielts_toefl" />
              <el-option label="商务英语" value="business" />
            </el-select>
          </el-form-item>
          <el-form-item label="难度级别">
            <el-select v-model="filterForm.level" placeholder="请选择难度级别">
              <el-option label="全部" value="" />
              <el-option label="入门" value="beginner" />
              <el-option label="基础" value="basic" />
              <el-option label="进阶" value="intermediate" />
              <el-option label="高级" value="advanced" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input v-model="filterForm.keyword" placeholder="搜索课程名称" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 显示所选筛选条件 -->
      <div class="selected-filters" v-if="hasSelectedFilters">
        <el-tag v-for="filter in selectedFilters" :key="filter.key" closable @close="removeFilter(filter.key)">
          {{ filter.label }}: {{ filter.value }}
        </el-tag>
        <el-button v-if="hasSelectedFilters" type="text" @click="clearAllFilters">清除全部</el-button>
      </div>
    </el-card>

    <!-- 课程列表 -->
    <el-card class="courses-card">
      <div class="course-grid">
        <div v-for="course in filteredCourses" :key="course.id" class="course-card">
          <div class="course-image">
            <img :src="course.coverImage || 'https://via.placeholder.com/300x180'" :alt="course.name" />
          </div>
          <div class="course-info">
            <h3>{{ course.name }}</h3>
            <p>{{ course.description }}</p>
            <div class="course-meta">
              <span class="course-type">{{ course.category }}</span>
              <span class="course-level">{{ course.level }}</span>
              <span class="course-duration">{{ course.duration }} 分钟</span>
            </div>
            <div class="course-actions">
              <el-button type="primary" size="small" @click="enrollCourse(course.id)">立即报名</el-button>
              <el-button size="small" @click="goToVideoPlayer(course.id)">查看详情</el-button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="courses.length"
          :page-size="10"
          :current-page="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '@/api'

const router = useRouter()
const currentPage = ref(1)
const loading = ref(false)

// 筛选表单
const filterForm = ref({
  type: '',
  level: '',
  keyword: ''
})

// 课程数据
const courses = ref<any[]>([])

// 从后端获取课程数据
const fetchCourses = async () => {
  try {
    loading.value = true
    const data = await api.courses.getList()
    courses.value = data
  } catch (error) {
    console.error('Failed to fetch courses:', error)
    ElMessage.error('获取课程失败')
  } finally {
    loading.value = false
  }
}

// 筛选后的课程
const filteredCourses = computed(() => {
  return courses.value.filter(course => {
    const matchesType = !filterForm.value.type || course.category === filterForm.value.type
    const matchesLevel = !filterForm.value.level || course.level === filterForm.value.level
    const matchesKeyword = !filterForm.value.keyword || course.name.toLowerCase().includes(filterForm.value.keyword.toLowerCase())
    return matchesType && matchesLevel && matchesKeyword
  })
})

// 搜索课程
const handleSearch = () => {
  currentPage.value = 1
  // 搜索功能已经通过computed实现，无需重新获取数据
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 计算属性：是否有选中的筛选条件
const hasSelectedFilters = computed(() => {
  return filterForm.value.type || filterForm.value.level || filterForm.value.keyword
})

// 计算属性：选中的筛选条件
const getTypeLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    'teenage': '青少年英语',
    'kaoyan': '考研英语',
    'cet': '四六级英语',
    'ielts_toefl': '雅思托福',
    'business': '商务英语'
  }
  return typeMap[type] || type
}

const getLevelLabel = (level: string) => {
  const levelMap: Record<string, string> = {
    'beginner': '入门',
    'basic': '基础',
    'intermediate': '进阶',
    'advanced': '高级'
  }
  return levelMap[level] || level
}

const selectedFilters = computed(() => {
  const filters = []
  if (filterForm.value.type) {
    filters.push({ key: 'type', label: '课程类型', value: getTypeLabel(filterForm.value.type) })
  }
  if (filterForm.value.level) {
    filters.push({ key: 'level', label: '难度级别', value: getLevelLabel(filterForm.value.level) })
  }
  if (filterForm.value.keyword) {
    filters.push({ key: 'keyword', label: '关键词', value: filterForm.value.keyword })
  }
  return filters
})

// 移除指定的筛选条件
const removeFilter = (key: string) => {
  filterForm.value[key] = ''
  currentPage.value = 1
}

// 清除所有筛选条件
const clearAllFilters = () => {
  filterForm.value.type = ''
  filterForm.value.level = ''
  filterForm.value.keyword = ''
  currentPage.value = 1
}

// 报名课程
const enrollCourse = async (courseId: string) => {
  try {
    await api.courses.enroll(courseId)
    ElMessage.success('报名成功')
    // 重新获取课程数据，更新报名状态
    await fetchCourses()
  } catch (error: any) {
    ElMessage.error(error.message || '报名失败')
  }
}

// 跳转到视频播放页面
const goToVideoPlayer = (courseId: string) => {
  router.push(`/student/video-player/${courseId}`)
}

// 组件挂载时获取课程数据
onMounted(() => {
  fetchCourses()
})
</script>

<style scoped>
/* 全局样式变量 */
:root {
  --primary-color: #409EFF;
  --primary-light: #66b1ff;
  --primary-dark: #3a8ee6;
  --secondary-color: #13c2c2;
  --text-color: #303133;
  --text-light: #606266;
  --text-lighter: #909399;
  --background-color: #f5f7fa;
  --card-background: #ffffff;
  --border-color: #e4e7ed;
  --shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 4px 12px rgba(0, 0, 0, 0.15);
  --shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.2);
  --border-radius-sm: 4px;
  --border-radius-md: 8px;
  --border-radius-lg: 12px;
  --transition: all 0.3s ease;
}

.course-selection {
  padding: 20px 0;
  font-family: 'Arial', sans-serif;
}

.course-selection h2 {
  margin-bottom: 30px;
  color: var(--text-color);
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1px;
  position: relative;
  display: inline-block;
}

.course-selection h2::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-light));
  border-radius: 2px;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 30px;
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  transition: var(--transition);
  background-color: var(--card-background);
  border: 1px solid var(--border-color);
}

.filter-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.filter-card .el-card__header {
  padding: 24px;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-card .el-card__header span {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color);
  letter-spacing: 0.5px;
}

.filter-card .el-card__body {
  padding: 24px;
}

/* 筛选表单 */
.filter-form {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 20px;
}

.filter-form .el-form-item {
  margin-right: 10px;
}

.filter-form .el-select,
.filter-form .el-input {
  width: 200px;
  border-radius: var(--border-radius-md);
  transition: var(--transition);
}

.filter-form .el-select:hover,
.filter-form .el-input:hover {
  box-shadow: var(--shadow-sm);
}

/* 已选筛选条件 */
.selected-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  padding-top: 20px;
  border-top: 1px solid var(--border-color);
}

.selected-filters .el-tag {
  border-radius: 20px;
  padding: 6px 16px;
  font-size: 14px;
  background-color: rgba(64, 158, 255, 0.1);
  color: var(--primary-color);
  border-color: var(--primary-light);
  transition: var(--transition);
}

.selected-filters .el-tag:hover {
  background-color: rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

/* 课程网格 */
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

/* 课程卡片 */
.course-card {
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  transition: var(--transition);
  background-color: var(--card-background);
  box-shadow: var(--shadow-sm);
  position: relative;
}

.course-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
}

.course-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-4px);
}

.course-image {
  height: 220px;
  overflow: hidden;
  position: relative;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.course-card:hover .course-image img {
  transform: scale(1.1);
}

.course-info {
  padding: 24px;
}

.course-info h3 {
  margin: 0 0 16px 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color);
  line-height: 1.4;
  height: 56px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-info p {
  margin: 0 0 20px 0;
  font-size: 14px;
  color: var(--text-light);
  line-height: 1.6;
  height: 48px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 课程元信息 */
.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
  align-items: center;
}

.course-type,
.course-level {
  padding: 6px 16px;
  background-color: rgba(64, 158, 255, 0.1);
  color: var(--primary-color);
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: var(--transition);
}

.course-type:hover,
.course-level:hover {
  background-color: rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.course-duration {
  font-size: 14px;
  color: var(--text-light);
  margin-left: auto;
  font-weight: 500;
}

/* 课程操作按钮 */
.course-actions {
  display: flex;
  gap: 12px;
}

.course-actions .el-button {
  flex: 1;
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-weight: 500;
  padding: 10px 16px;
  transition: var(--transition);
  box-shadow: var(--shadow-sm);
}

.course-actions .el-button--primary {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border-color: var(--primary-color);
}

.course-actions .el-button--primary:hover {
  background: linear-gradient(135deg, var(--primary-light), var(--primary-color));
  border-color: var(--primary-light);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.course-actions .el-button--default {
  background-color: var(--card-background);
  border-color: var(--border-color);
  color: var(--text-color);
}

.course-actions .el-button--default:hover {
  background-color: var(--background-color);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.pagination .el-pagination {
  font-size: 14px;
}

.pagination .el-pagination__item {
  border-radius: var(--border-radius-md);
  transition: var(--transition);
}

.pagination .el-pagination__item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.pagination .el-pagination__item.is-active {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border-color: var(--primary-color);
  color: white;
}

.pagination .el-pagination__button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .course-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
  
  .filter-form .el-select,
  .filter-form .el-input {
    width: 180px;
  }
}

@media screen and (max-width: 768px) {
  .course-selection {
    padding: 15px 0;
  }
  
  .course-selection h2 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .filter-card .el-card__header,
  .filter-card .el-card__body {
    padding: 16px;
  }
  
  .filter-form {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .filter-form .el-select,
  .filter-form .el-input {
    width: 100%;
  }
  
  .course-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .course-image {
    height: 200px;
  }
  
  .course-info {
    padding: 16px;
  }
  
  .course-info h3 {
    font-size: 18px;
  }
  
  .course-actions {
    flex-direction: column;
  }
  
  .course-actions .el-button {
    width: 100%;
  }
  
  .pagination {
    margin-top: 30px;
  }
}

@media screen and (max-width: 480px) {
  .course-selection h2 {
    font-size: 20px;
  }
  
  .course-image {
    height: 180px;
  }
  
  .course-info h3 {
    font-size: 16px;
  }
}
</style>