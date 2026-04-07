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
              <el-option label="口语" value="口语" />
              <el-option label="听力" value="听力" />
              <el-option label="阅读" value="阅读" />
              <el-option label="写作" value="写作" />
            </el-select>
          </el-form-item>
          <el-form-item label="难度级别">
            <el-select v-model="filterForm.level" placeholder="请选择难度级别">
              <el-option label="全部" value="" />
              <el-option label="初级" value="初级" />
              <el-option label="中级" value="中级" />
              <el-option label="高级" value="高级" />
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
            <img :src="`http://localhost:8080${course.image}`" :alt="course.title" />
          </div>
          <div class="course-info">
            <h3>{{ course.title }}</h3>
            <p>{{ course.description }}</p>
            <div class="course-meta">
              <span class="course-type">{{ course.type }}</span>
              <span class="course-level">{{ course.level }}</span>
              <span class="course-duration">{{ course.duration }} 课时</span>
            </div>
            <div class="course-actions">
              <el-button type="primary" size="small" @click="enrollCourse(course.id)">
                {{ course.enrolled ? '已报名' : '立即报名' }}
              </el-button>
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
    const matchesType = !filterForm.value.type || course.type === filterForm.value.type
    const matchesLevel = !filterForm.value.level || course.level === filterForm.value.level
    const matchesKeyword = !filterForm.value.keyword || course.title.toLowerCase().includes(filterForm.value.keyword.toLowerCase())
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
const selectedFilters = computed(() => {
  const filters = []
  if (filterForm.value.type) {
    filters.push({ key: 'type', label: '课程类型', value: filterForm.value.type })
  }
  if (filterForm.value.level) {
    filters.push({ key: 'level', label: '难度级别', value: filterForm.value.level })
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
.course-selection {
  padding: 20px 0;
}

.course-selection h2 {
  margin-bottom: 20px;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.selected-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #e4e7ed;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.course-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.course-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.course-image {
  height: 180px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-info {
  padding: 15px;
}

.course-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
}

.course-info p {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.course-type,
.course-level {
  padding: 2px 8px;
  background-color: #ecf5ff;
  color: #409EFF;
  border-radius: 10px;
  font-size: 12px;
}

.course-duration {
  font-size: 12px;
  color: #909399;
  margin-left: auto;
}

.course-actions {
  display: flex;
  gap: 10px;
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
  
  .course-grid {
    grid-template-columns: 1fr;
  }
}
</style>