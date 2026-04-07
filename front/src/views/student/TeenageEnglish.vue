<template>
  <div class="teenage-english">
    <h2>青少年英语</h2>
    
    <!-- 学习模块 -->
    <el-card class="module-card">
      <template #header>
        <div class="card-header">
          <span>单词教学</span>
        </div>
      </template>
      <div class="word-teaching">
        <div class="word-list">
          <div v-for="word in words" :key="word.id" class="word-item">
            <div class="word-info">
              <h3>{{ word.word }}</h3>
              <p class="phonetic">{{ word.phonetic }}</p>
              <p class="meaning">{{ word.meaning }}</p>
            </div>
            <div class="word-actions">
              <el-button type="primary" size="small" @click="playAudio(word.id)">播放发音</el-button>
              <el-button type="success" size="small" @click="addToList(word.id)">添加到学习列表</el-button>
            </div>
          </div>
        </div>
        <div class="word-practice">
          <h4>单词练习</h4>
          <div class="practice-question">
            <p>{{ currentQuestion.question }}</p>
            <el-radio-group v-model="selectedAnswer">
              <el-radio v-for="option in currentQuestion.options" :key="option" :label="option">
                {{ option }}
              </el-radio>
            </el-radio-group>
            <el-button type="primary" @click="submitAnswer" style="margin-top: 20px">提交答案</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 单词数据
const words = ref([
  {
    id: '1',
    word: 'apple',
    phonetic: '/ˈæpl/',
    meaning: '苹果'
  },
  {
    id: '2',
    word: 'banana',
    phonetic: '/bəˈnɑːnə/',
    meaning: '香蕉'
  },
  {
    id: '3',
    word: 'cat',
    phonetic: '/kæt/',
    meaning: '猫'
  },
  {
    id: '4',
    word: 'dog',
    phonetic: '/dɒɡ/',
    meaning: '狗'
  },
  {
    id: '5',
    word: 'elephant',
    phonetic: '/ˈelɪfənt/',
    meaning: '大象'
  }
])

// 练习题目
const questions = ref([
  {
    id: '1',
    question: 'What is the meaning of "apple"?',
    options: ['苹果', '香蕉', '猫', '狗'],
    correctAnswer: '苹果'
  },
  {
    id: '2',
    question: 'What is the meaning of "cat"?',
    options: ['狗', '猫', '大象', '苹果'],
    correctAnswer: '猫'
  }
])

const currentQuestion = ref(questions.value[0])
const selectedAnswer = ref('')

// 播放发音
const playAudio = (wordId: string) => {
  // 实际项目中可以调用语音API
  console.log('Playing audio for word:', wordId)
}

// 添加到学习列表
const addToList = (wordId: string) => {
  console.log('Adding word to list:', wordId)
}

// 提交答案
const submitAnswer = () => {
  if (selectedAnswer.value === currentQuestion.value.correctAnswer) {
    alert('正确！')
  } else {
    alert('错误，请再试一次！')
  }
}
</script>

<style scoped>
.teenage-english {
  padding: 20px 0;
}

.teenage-english h2 {
  margin-bottom: 20px;
  color: #303133;
}

.module-card {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.word-teaching {
  display: flex;
  gap: 30px;
}

.word-list {
  flex: 1;
}

.word-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.word-item:last-child {
  border-bottom: none;
}

.word-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: 600;
}

.phonetic {
  margin: 0 0 5px 0;
  color: #909399;
  font-size: 14px;
}

.meaning {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.word-actions {
  display: flex;
  gap: 10px;
}

.word-practice {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.word-practice h4 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
}

.practice-question {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.practice-question p {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #303133;
}

@media screen and (max-width: 768px) {
  .word-teaching {
    flex-direction: column;
  }
  
  .word-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .word-actions {
    align-self: flex-end;
  }
}
</style>