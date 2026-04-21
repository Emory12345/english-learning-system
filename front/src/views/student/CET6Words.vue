<template>
  <div class="cet6-words">
    <h2>六级单词</h2>
    
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索单词..."
        clearable
        style="margin-bottom: 20px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>
    
    <div class="word-list">
      <div v-for="word in filteredWords" :key="word.id" class="word-item">
        <div class="word-info">
          <h3>{{ word.word }}</h3>
          <p class="phonetic">{{ word.phonetic }}</p>
          <p class="meaning">{{ word.definition }}</p>
          <p class="chinese-meaning" v-if="word.chineseMeaning">{{ word.chineseMeaning }}</p>
          <p class="example">{{ word.example }}</p>
        </div>
        <div class="word-actions">
          <el-button type="primary" size="small" @click="playAudio(word.id)">播放发音</el-button>
          <el-button type="success" size="small" @click="addToList(word.id)">添加到学习列表</el-button>
        </div>
      </div>
      
      <!-- 分页组件 -->
      <div class="pagination" v-if="!searchKeyword && totalPages > 1">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalElements"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

// 六级单词数据
const words = ref([])
const allWords = ref([]) // 存储所有六级单词用于搜索
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const totalElements = ref(0)
const totalPages = ref(0)

// 检查是否包含中文字符
const hasChinese = (text: string) => {
  return /[\u4e00-\u9fa5]/.test(text)
}

// 过滤后的单词
const filteredWords = computed(() => {
  if (!searchKeyword.value) {
    return words.value
  }
  const keyword = searchKeyword.value
  const wordsToSearch = allWords.value.length > 0 ? allWords.value : words.value
  if (hasChinese(keyword)) {
    // 输入中文 → 只搜索中文释义
    return wordsToSearch.filter((word: any) => 
      word.chineseMeaning && word.chineseMeaning.includes(keyword)
    )
  } else {
    // 输入英文 → 只搜索英文单词
    const lowerKeyword = keyword.toLowerCase()
    return wordsToSearch.filter((word: any) => 
      word.word.toLowerCase().includes(lowerKeyword)
    )
  }
})

// 监听搜索关键词变化，重置页码到第一页
watch(searchKeyword, () => {
  currentPage.value = 1
})

// 从后端API获取所有六级单词（用于搜索）
const fetchAllWords = async () => {
  try {
    console.log('Fetching all CET6 words for search...')
    const response = await fetch('http://localhost:8080/api/words/age-group/26')
    if (!response.ok) {
      throw new Error('Failed to fetch all CET6 words')
    }
    const data = await response.json()
    const formattedWords = data.map((word: any) => ({
      id: word.id,
      word: word.word,
      phonetic: word.phonetic,
      definition: word.meaning,
      chineseMeaning: word.chineseMeaning,
      example: word.example
    }))
    allWords.value = formattedWords
    console.log('All CET6 words loaded:', allWords.value.length)
  } catch (error) {
    console.error('Failed to fetch all CET6 words:', error)
  }
}

// 从API获取六级单词（带分页）
const fetchWords = async () => {
  try {
    console.log('Fetching CET6 words from API...')
    const response = await fetch(`http://localhost:8080/api/words/age-group/26/page?page=${currentPage.value - 1}&size=${pageSize.value}`)
    console.log('Response status:', response.status)
    if (!response.ok) {
      throw new Error('Failed to fetch CET6 words with status: ' + response.status)
    }
    const data = await response.json()
    console.log('API response:', data)
    
    // 转换数据格式
    const formattedWords = data.content.map((word: any) => ({
      id: word.id,
      word: word.word,
      phonetic: word.phonetic,
      definition: word.meaning, // 使用meaning字段作为definition
      chineseMeaning: word.chineseMeaning, // 中文释义
      example: word.example
    }))
    console.log('Formatted CET6 words:', formattedWords)
    words.value = formattedWords
    totalElements.value = data.totalElements
    totalPages.value = data.totalPages
    
    console.log('CET6 words updated:', words.value)
    console.log('CET6 total elements:', totalElements.value)
    console.log('CET6 total pages:', totalPages.value)
  } catch (error) {
    console.error('Failed to fetch CET6 words:', error)
    ElMessage.error('获取六级单词失败，请稍后重试')
  }
}

// 分页处理函数
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchWords()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  fetchWords()
}

// 播放发音
const playAudio = (wordId: string) => {
  // 在单词列表中查找单词
  let word = words.value.find((w: any) => w.id === wordId)
  if (!word) {
    word = allWords.value.find((w: any) => w.id === wordId)
  }
  if (word && 'speechSynthesis' in window) {
    const utterance = new SpeechSynthesisUtterance(word.word)
    utterance.lang = 'en-US'
    utterance.rate = 0.8
    window.speechSynthesis.speak(utterance)
  } else {
    console.log('Browser does not support speech synthesis')
  }
}

// 添加到学习列表
const addToList = async (wordId: string) => {
  try {
    console.log('Adding word to learning list with id:', wordId)
    // 为了测试方便，使用固定的用户ID
    const userId = 1
    const response = await fetch(`http://localhost:8080/api/word-learning-list/add?userId=${userId}&wordId=${wordId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    console.log('Response status:', response.status)
    const result = await response.text()
    console.log('Response result:', result)
    if (response.ok) {
      ElMessage.success(result)
    } else {
      ElMessage.error(result)
    }
  } catch (error) {
    console.error('Failed to add word to learning list:', error)
    ElMessage.error('添加失败，请稍后重试')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchWords()
  fetchAllWords()
})
</script>

<style scoped>
.cet6-words {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.cet6-words h2 {
  margin-bottom: 20px;
  color: #303133;
}

.word-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.word-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.word-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.phonetic {
  margin: 0 0 5px 0;
  color: #909399;
  font-size: 14px;
}

.meaning {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}

.chinese-meaning {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #67c23a;
  font-weight: 600;
}

.example {
  margin: 0;
  font-size: 14px;
  color: #606266;
  font-style: italic;
}

.word-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 10px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .word-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .word-actions {
    margin-top: 15px;
    align-self: flex-end;
  }
}
</style>