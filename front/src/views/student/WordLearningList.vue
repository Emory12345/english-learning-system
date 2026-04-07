<template>
  <div class="word-learning-list">
    <h2>我的学习列表</h2>
    <p class="subtitle">查看和管理你添加的单词</p>
    
    <el-card class="module-card">
      <template #header>
        <div class="card-header">
          <span>学习列表</span>
          <el-tag type="success">学习中</el-tag>
        </div>
      </template>
      <div class="word-list">
        <div v-for="item in learningList" :key="item.id" class="word-item">
          <div class="word-info">
            <h3>{{ item.word.word }}</h3>
            <p class="phonetic">{{ item.word.phonetic }}</p>
            <p class="meaning">{{ item.word.meaning }}</p>
            <p class="chinese-meaning" v-if="item.word.chineseMeaning">{{ item.word.chineseMeaning }}</p>
            <p class="example">{{ item.word.example }}</p>
            <div class="word-status">
              <el-tag :type="item.isLearned ? 'success' : 'warning'">
                {{ item.isLearned ? '已学习' : '学习中' }}
              </el-tag>
            </div>
          </div>
          <div class="word-actions">
            <el-button type="primary" size="small" @click="playAudio(item.word.id)">播放发音</el-button>
            <el-button type="danger" size="small" @click="removeFromList(item.id)">从列表中移除</el-button>
          </div>
        </div>
        <div v-if="learningList.length === 0" class="empty-list">
          <p>学习列表为空，开始添加单词吧！</p>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 学习列表数据
const learningList = ref([])

// 从后端API获取学习列表数据
const fetchLearningList = async () => {
  try {
    // 为了测试方便，使用固定的用户ID
    const userId = 1
    const response = await fetch(`http://localhost:8080/api/word-learning-list/user/${userId}`)
    if (response.ok) {
      const data = await response.json()
      learningList.value = data
    }
  } catch (error) {
    console.error('Failed to fetch learning list:', error)
  }
}

// 页面加载时获取学习列表数据
onMounted(() => {
  fetchLearningList()
})

// 播放发音
const playAudio = (wordId: string) => {
  const word = learningList.value.find(item => item.word.id === wordId)
  if (word && 'speechSynthesis' in window) {
    const utterance = new SpeechSynthesisUtterance(word.word.word)
    utterance.lang = 'en-US'
    utterance.rate = 0.8
    window.speechSynthesis.speak(utterance)
  } else {
    console.log('Browser does not support speech synthesis')
  }
}



// 从列表中移除
const removeFromList = async (itemId: string) => {
  try {
    // 为了测试方便，使用固定的用户ID
    const userId = 1
    const wordId = learningList.value.find(item => item.id === itemId)?.word.id
    if (!wordId) return
    
    const response = await fetch(`http://localhost:8080/api/word-learning-list/remove?userId=${userId}&wordId=${wordId}`, {
      method: 'POST'
    })
    
    if (response.ok) {
      const result = await response.text()
      ElMessage.success(result)
      // 重新获取学习列表数据
      fetchLearningList()
    } else {
      const result = await response.text()
      ElMessage.error(result)
    }
  } catch (error) {
    console.error('Failed to remove word from list:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}
</script>

<style scoped>
.word-learning-list {
  padding: 20px 0;
}

.word-learning-list h2 {
  margin-bottom: 10px;
  color: #303133;
  font-size: 28px;
}

.subtitle {
  color: #606266;
  margin-bottom: 30px;
  font-size: 16px;
}

.module-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
}

.word-info h3 {
  margin: 0 0 5px 0;
  font-size: 20px;
  font-weight: 600;
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
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
  font-style: italic;
}

.word-status {
  margin-top: 10px;
}

.word-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.empty-list {
  text-align: center;
  padding: 40px;
  color: #909399;
  font-size: 16px;
}

@media screen and (max-width: 768px) {
  .word-item {
    flex-direction: column;
    gap: 10px;
  }
  
  .word-actions {
    align-self: flex-end;
  }
}
</style>