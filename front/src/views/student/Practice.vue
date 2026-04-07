<template>
  <div class="practice">
    <h2>在线练习</h2>
    
    <!-- 练习筛选 -->
    <el-card class="filter-card">
      <div class="filter-form">
        <el-form :inline="true" :model="filterForm">
          <el-form-item label="练习类型">
            <el-select v-model="filterForm.type" placeholder="请选择练习类型">
              <el-option label="全部" value="" />
              <el-option label="听力" value="listening" />
              <el-option label="口语" value="speaking" />
              <el-option label="阅读" value="reading" />
              <el-option label="写作" value="writing" />
            </el-select>
          </el-form-item>
          <el-form-item label="难度级别">
            <el-select v-model="filterForm.level" placeholder="请选择难度级别">
              <el-option label="全部" value="" />
              <el-option label="初级" value="beginner" />
              <el-option label="中级" value="intermediate" />
              <el-option label="高级" value="advanced" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 练习列表 -->
    <el-card class="practice-card">
      <div class="practice-list">
        <div v-for="practice in filteredPractices" :key="practice.id" class="practice-item">
          <div class="practice-info">
            <h3>{{ practice.title }}</h3>
            <p>{{ practice.description }}</p>
            <div class="practice-meta">
              <span class="practice-type">{{ practice.type }}</span>
              <span class="practice-level">{{ practice.level }}</span>
              <span class="practice-questions">{{ practice.questions }} 题</span>
              <span class="practice-duration">{{ practice.duration }} 分钟</span>
            </div>
          </div>
          <div class="practice-actions">
            <el-button type="primary" size="small" @click="startPractice(practice.id)">
              {{ practice.completed ? '查看结果' : '开始练习' }}
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="practices.length"
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
  type: '',
  level: ''
})

// 模拟练习数据
const practices = ref([
  {
    id: '1',
    title: '初级听力练习',
    description: '测试你的英语听力水平，包括日常对话和简单短文',
    type: '听力',
    level: '初级',
    questions: 10,
    duration: 20,
    completed: true
  },
  {
    id: '2',
    title: '中级口语练习',
    description: '提高你的英语口语能力，包括情景对话和话题讨论',
    type: '口语',
    level: '中级',
    questions: 8,
    duration: 30,
    completed: false
  },
  {
    id: '3',
    title: '高级阅读练习',
    description: '提升你的英语阅读能力，包括学术文章和新闻报道',
    type: '阅读',
    level: '高级',
    questions: 12,
    duration: 40,
    completed: false
  },
  {
    id: '4',
    title: '初级写作练习',
    description: '练习英语写作，包括日常邮件和简短作文',
    type: '写作',
    level: '初级',
    questions: 5,
    duration: 30,
    completed: false
  },
  {
    id: '5',
    title: '中级听力练习',
    description: '进一步提高你的英语听力水平，适应不同口音和语速',
    type: '听力',
    level: '中级',
    questions: 12,
    duration: 25,
    completed: false
  }
])

// 筛选后的练习
const filteredPractices = computed(() => {
  return practices.value.filter(practice => {
    const matchesType = !filterForm.value.type || practice.type === filterForm.value.type
    const matchesLevel = !filterForm.value.level || practice.level === filterForm.value.level
    return matchesType && matchesLevel
  })
})

// 搜索练习
const handleSearch = () => {
  currentPage.value = 1
}

// 分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page
}

// 开始练习
const startPractice = (practiceId: string) => {
  // 实际项目中这里会跳转到练习详情页面
  ElMessage.info(`开始练习：${practiceId}`)
}
</script>

<style scoped>
.practice {
  padding: 20px 0;
}

.practice h2 {
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

.practice-list {
  margin-bottom: 20px;
}

.practice-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.practice-item:hover {
  background-color: #f5f7fa;
  border-radius: 4px;
}

.practice-item:last-child {
  border-bottom: none;
}

.practice-info {
  flex: 1;
}

.practice-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
}

.practice-info p {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.practice-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.practice-type,
.practice-level {
  padding: 2px 8px;
  background-color: #ecf5ff;
  color: #409EFF;
  border-radius: 10px;
  font-size: 12px;
}

.practice-questions,
.practice-duration {
  font-size: 12px;
  color: #909399;
}

.practice-actions {
  margin-left: 20px;
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
  
  .practice-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .practice-actions {
    margin-left: 0;
    align-self: flex-end;
  }
}
</style>