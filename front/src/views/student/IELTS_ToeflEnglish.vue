<template>
  <div class="ielts-toefl-english">
    <h2>雅思托福</h2>
    
    <!-- 雅思托福选项卡 -->
    <el-tabs v-model="activeLevel" style="margin-bottom: 20px;">
      <el-tab-pane label="雅思" name="IELTS">
        <div class="level-content">
          <!-- 学习模块标签页 -->
          <el-tabs v-model="activeTab">
            <el-tab-pane label="单词" name="words">
              <el-card class="module-card">
                <div class="word-list">
                  <div v-for="word in ieltsWords" :key="word.id" class="word-item">
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
                  
                  <!-- 雅思分页组件 -->
                  <div class="pagination" v-if="ieltsTotalPages > 1">
                    <el-pagination
                      v-model:current-page="ieltsCurrentPage"
                      v-model:page-size="ieltsPageSize"
                      :page-sizes="[10, 20, 50, 100]"
                      layout="total, sizes, prev, pager, next, jumper"
                      :total="ieltsTotalElements"
                      @size-change="handleIeltsSizeChange"
                      @current-change="handleIeltsCurrentChange"
                    />
                  </div>
                </div>
              </el-card>
            </el-tab-pane>

      <el-tab-pane label="口语" name="speaking">
        <!-- 口语视频 -->
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>口语视频学习</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in ieltsSpeakingVideos" :key="video.id" class="video-card">
              <div class="video-thumbnail">
                <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                  您的浏览器不支持视频播放
                </video>
              </div>
              <div class="video-info">
                <h4>{{ video.title }}</h4>
                <p>{{ video.description }}</p>
                <p class="video-type">类型: {{ video.type }}</p>
              </div>
            </div>
            <el-empty v-if="ieltsSpeakingVideos.length === 0" description="暂无口语视频" />
          </div>
        </el-card>
        
        <!-- 口语作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>口语作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in ieltsSpeakingHomework" :key="homework.id" class="homework-item">
              <div class="homework-info">
                <h4>{{ homework.title }}</h4>
                <p class="homework-content">{{ homework.content }}</p>
                <div v-if="homework.image" class="homework-image">
                  <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                </div>
                <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
                
                <!-- 已提交作业信息 -->
                <div v-if="getSubmissionForHomework(homework.id)" class="submission-info">
                  <el-divider />
                  <h5>我的提交：</h5>
                  <p class="submission-content">{{ getSubmissionForHomework(homework.id).content }}</p>
                  <div v-if="getSubmissionForHomework(homework.id).image" class="submission-image">
                    <img :src="`http://localhost:8080${getSubmissionForHomework(homework.id).image}`" alt="我提交的图片" />
                  </div>
                  <p class="submission-time">提交时间: {{ formatTime(getSubmissionForHomework(homework.id).submissionDate) }}</p>
                  
                  <!-- 批改结果 -->
                  <div v-if="getSubmissionForHomework(homework.id).status === 'graded'" class="grade-result">
                    <h5>批改结果：</h5>
                    <p class="score">分数：{{ getSubmissionForHomework(homework.id).score }}分</p>
                    <p class="feedback">评语：{{ getSubmissionForHomework(homework.id).feedback }}</p>
                  </div>
                </div>
              </div>
              <div class="homework-actions">
                <el-button 
                  v-if="!getSubmissionForHomework(homework.id)" 
                  type="primary" 
                  size="small" 
                  @click="submitHomework(homework.id)"
                >
                  提交作业
                </el-button>
                <el-tag v-else :type="getSubmissionForHomework(homework.id).status === 'graded' ? 'success' : 'info'" size="small">
                  {{ getSubmissionForHomework(homework.id).status === 'graded' ? '已批改' : '已提交' }}
                </el-tag>
              </div>
            </div>
            <el-empty v-if="ieltsSpeakingHomework.length === 0" description="暂无口语作业" />
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="阅读" name="reading">
        <!-- 阅读视频 -->
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>阅读视频学习</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in ieltsReadingVideos" :key="video.id" class="video-card">
              <div class="video-thumbnail">
                <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                  您的浏览器不支持视频播放
                </video>
              </div>
              <div class="video-info">
                <h4>{{ video.title }}</h4>
                <p>{{ video.description }}</p>
                <p class="video-type">类型: {{ video.type }}</p>
              </div>
            </div>
            <el-empty v-if="ieltsReadingVideos.length === 0" description="暂无阅读视频" />
          </div>
        </el-card>
        
        <!-- 阅读作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>阅读作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in ieltsReadingHomework" :key="homework.id" class="homework-item">
              <div class="homework-info">
                <h4>{{ homework.title }}</h4>
                <p class="homework-content">{{ homework.content }}</p>
                <div v-if="homework.image" class="homework-image">
                  <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                </div>
                <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
              </div>
              <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
            </div>
            <el-empty v-if="ieltsReadingHomework.length === 0" description="暂无阅读作业" />
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="听力" name="listening">
        <!-- 听力视频 -->
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>听力视频学习</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in ieltsListeningVideos" :key="video.id" class="video-card">
              <div class="video-thumbnail">
                <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                  您的浏览器不支持视频播放
                </video>
              </div>
              <div class="video-info">
                <h4>{{ video.title }}</h4>
                <p>{{ video.description }}</p>
                <p class="video-type">类型: {{ video.type }}</p>
              </div>
            </div>
            <el-empty v-if="ieltsListeningVideos.length === 0" description="暂无听力视频" />
          </div>
        </el-card>
        
        <!-- 听力作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>听力作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in ieltsListeningHomework" :key="homework.id" class="homework-item">
              <div class="homework-info">
                <h4>{{ homework.title }}</h4>
                <p class="homework-content">{{ homework.content }}</p>
                <div v-if="homework.image" class="homework-image">
                  <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                </div>
                <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
              </div>
              <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
            </div>
            <el-empty v-if="ieltsListeningHomework.length === 0" description="暂无听力作业" />
          </div>
        </el-card>
      </el-tab-pane>
      
      <el-tab-pane label="写作" name="writing">
        <!-- 写作视频 -->
        <el-card class="module-card">
          <template #header>
            <div class="card-header">
              <span>写作视频学习</span>
              <el-tag type="info">视频学习</el-tag>
            </div>
          </template>
          <div class="video-grid">
            <div v-for="video in ieltsWritingVideos" :key="video.id" class="video-card">
              <div class="video-thumbnail">
                <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                  您的浏览器不支持视频播放
                </video>
              </div>
              <div class="video-info">
                <h4>{{ video.title }}</h4>
                <p>{{ video.description }}</p>
                <p class="video-type">类型: {{ video.type }}</p>
              </div>
            </div>
            <el-empty v-if="ieltsWritingVideos.length === 0" description="暂无写作视频" />
          </div>
        </el-card>
        
        <!-- 写作作业 -->
        <el-card class="module-card" style="margin-top: 20px">
          <template #header>
            <div class="card-header">
              <span>写作作业</span>
              <el-tag type="warning">练习巩固</el-tag>
            </div>
          </template>
          <div class="homework-list">
            <div v-for="homework in ieltsWritingHomework" :key="homework.id" class="homework-item">
              <div class="homework-info">
                <h4>{{ homework.title }}</h4>
                <p class="homework-content">{{ homework.content }}</p>
                <div v-if="homework.image" class="homework-image">
                  <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                </div>
                <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
              </div>
              <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
            </div>
            <el-empty v-if="ieltsWritingHomework.length === 0" description="暂无写作作业" />
          </div>
        </el-card>
      </el-tab-pane>
          </el-tabs>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="托福" name="TOEFL">
        <div class="level-content">
          <!-- 学习模块标签页 -->
          <el-tabs v-model="activeTab">
            <el-tab-pane label="单词" name="words">
              <el-card class="module-card">
                <div class="word-list">
                  <div v-for="word in toeflWords" :key="word.id" class="word-item">
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
                  
                  <!-- 托福分页组件 -->
                  <div class="pagination" v-if="toeflTotalPages > 1">
                    <el-pagination
                      v-model:current-page="toeflCurrentPage"
                      v-model:page-size="toeflPageSize"
                      :page-sizes="[10, 20, 50, 100]"
                      layout="total, sizes, prev, pager, next, jumper"
                      :total="toeflTotalElements"
                      @size-change="handleToeflSizeChange"
                      @current-change="handleToeflCurrentChange"
                    />
                  </div>
                </div>
              </el-card>
            </el-tab-pane>
            
            <el-tab-pane label="口语" name="speaking">
              <!-- 口语视频 -->
              <el-card class="module-card">
                <template #header>
                  <div class="card-header">
                    <span>口语视频学习</span>
                    <el-tag type="info">视频学习</el-tag>
                  </div>
                </template>
                <div class="video-grid">
                  <div v-for="video in toeflSpeakingVideos" :key="video.id" class="video-card">
                    <div class="video-thumbnail">
                      <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                        您的浏览器不支持视频播放
                      </video>
                    </div>
                    <div class="video-info">
                      <h4>{{ video.title }}</h4>
                      <p>{{ video.description }}</p>
                      <p class="video-type">类型: {{ video.type }}</p>
                    </div>
                  </div>
                  <el-empty v-if="toeflSpeakingVideos.length === 0" description="暂无口语视频" />
                </div>
              </el-card>
              
              <!-- 口语作业 -->
              <el-card class="module-card" style="margin-top: 20px">
                <template #header>
                  <div class="card-header">
                    <span>口语作业</span>
                    <el-tag type="warning">练习巩固</el-tag>
                  </div>
                </template>
                <div class="homework-list">
                  <div v-for="homework in toeflSpeakingHomework" :key="homework.id" class="homework-item">
                    <div class="homework-info">
                      <h4>{{ homework.title }}</h4>
                      <p class="homework-content">{{ homework.content }}</p>
                      <div v-if="homework.image" class="homework-image">
                        <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                      </div>
                      <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
                    </div>
                    <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
                  </div>
                  <el-empty v-if="toeflSpeakingHomework.length === 0" description="暂无口语作业" />
                </div>
              </el-card>
            </el-tab-pane>
            
            <el-tab-pane label="阅读" name="reading">
              <!-- 阅读视频 -->
              <el-card class="module-card">
                <template #header>
                  <div class="card-header">
                    <span>阅读视频学习</span>
                    <el-tag type="info">视频学习</el-tag>
                  </div>
                </template>
                <div class="video-grid">
                  <div v-for="video in toeflReadingVideos" :key="video.id" class="video-card">
                    <div class="video-thumbnail">
                      <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                        您的浏览器不支持视频播放
                      </video>
                    </div>
                    <div class="video-info">
                      <h4>{{ video.title }}</h4>
                      <p>{{ video.description }}</p>
                      <p class="video-type">类型: {{ video.type }}</p>
                    </div>
                  </div>
                  <el-empty v-if="toeflReadingVideos.length === 0" description="暂无阅读视频" />
                </div>
              </el-card>
              
              <!-- 阅读作业 -->
              <el-card class="module-card" style="margin-top: 20px">
                <template #header>
                  <div class="card-header">
                    <span>阅读作业</span>
                    <el-tag type="warning">练习巩固</el-tag>
                  </div>
                </template>
                <div class="homework-list">
                  <div v-for="homework in toeflReadingHomework" :key="homework.id" class="homework-item">
                    <div class="homework-info">
                      <h4>{{ homework.title }}</h4>
                      <p class="homework-content">{{ homework.content }}</p>
                      <div v-if="homework.image" class="homework-image">
                        <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                      </div>
                      <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
                    </div>
                    <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
                  </div>
                  <el-empty v-if="toeflReadingHomework.length === 0" description="暂无阅读作业" />
                </div>
              </el-card>
            </el-tab-pane>
            
            <el-tab-pane label="听力" name="listening">
              <!-- 听力视频 -->
              <el-card class="module-card">
                <template #header>
                  <div class="card-header">
                    <span>听力视频学习</span>
                    <el-tag type="info">视频学习</el-tag>
                  </div>
                </template>
                <div class="video-grid">
                  <div v-for="video in toeflListeningVideos" :key="video.id" class="video-card">
                    <div class="video-thumbnail">
                      <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                        您的浏览器不支持视频播放
                      </video>
                    </div>
                    <div class="video-info">
                      <h4>{{ video.title }}</h4>
                      <p>{{ video.description }}</p>
                      <p class="video-type">类型: {{ video.type }}</p>
                    </div>
                  </div>
                  <el-empty v-if="toeflListeningVideos.length === 0" description="暂无听力视频" />
                </div>
              </el-card>
              
              <!-- 听力作业 -->
              <el-card class="module-card" style="margin-top: 20px">
                <template #header>
                  <div class="card-header">
                    <span>听力作业</span>
                    <el-tag type="warning">练习巩固</el-tag>
                  </div>
                </template>
                <div class="homework-list">
                  <div v-for="homework in toeflListeningHomework" :key="homework.id" class="homework-item">
                    <div class="homework-info">
                      <h4>{{ homework.title }}</h4>
                      <p class="homework-content">{{ homework.content }}</p>
                      <div v-if="homework.image" class="homework-image">
                        <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                      </div>
                      <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
                    </div>
                    <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
                  </div>
                  <el-empty v-if="toeflListeningHomework.length === 0" description="暂无听力作业" />
                </div>
              </el-card>
            </el-tab-pane>
            
            <el-tab-pane label="写作" name="writing">
              <!-- 写作视频 -->
              <el-card class="module-card">
                <template #header>
                  <div class="card-header">
                    <span>写作视频学习</span>
                    <el-tag type="info">视频学习</el-tag>
                  </div>
                </template>
                <div class="video-grid">
                  <div v-for="video in toeflWritingVideos" :key="video.id" class="video-card">
                    <div class="video-thumbnail">
                      <video :src="`http://localhost:8080${video.videoUrl}`" controls width="100%" height="180px">
                        您的浏览器不支持视频播放
                      </video>
                    </div>
                    <div class="video-info">
                      <h4>{{ video.title }}</h4>
                      <p>{{ video.description }}</p>
                      <p class="video-type">类型: {{ video.type }}</p>
                    </div>
                  </div>
                  <el-empty v-if="toeflWritingVideos.length === 0" description="暂无写作视频" />
                </div>
              </el-card>
              
              <!-- 写作作业 -->
              <el-card class="module-card" style="margin-top: 20px">
                <template #header>
                  <div class="card-header">
                    <span>写作作业</span>
                    <el-tag type="warning">练习巩固</el-tag>
                  </div>
                </template>
                <div class="homework-list">
                  <div v-for="homework in toeflWritingHomework" :key="homework.id" class="homework-item">
                    <div class="homework-info">
                      <h4>{{ homework.title }}</h4>
                      <p class="homework-content">{{ homework.content }}</p>
                      <div v-if="homework.image" class="homework-image">
                        <img :src="`http://localhost:8080${homework.image}`" :alt="homework.title" />
                      </div>
                      <p class="homework-time">发布时间: {{ formatTime(homework.createdAt) }}</p>
                    </div>
                    <el-button type="primary" size="small" @click="submitHomework(homework.id)">提交作业</el-button>
                  </div>
                  <el-empty v-if="toeflWritingHomework.length === 0" description="暂无写作作业" />
                </div>
              </el-card>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <!-- 提交作业对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="提交作业"
      width="500px"
    >
      <el-form :model="submitForm" label-width="80px">
        <el-form-item label="作业标题">
          <el-input v-model="submitForm.title" disabled />
        </el-form-item>
        <el-form-item label="作业内容">
          <el-input
            v-model="submitForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入作业内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="submitDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSubmit" :loading="submitting">确定提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { api } from '../../api/index'
import { ElMessage } from 'element-plus'

// 激活的等级（IELTS或TOEFL）
const activeLevel = ref('IELTS')

// 激活的标签页
const activeTab = ref('words')

// 雅思单词数据
const ieltsWords = ref([])
const ieltsCurrentPage = ref(1)
const ieltsPageSize = ref(20)
const ieltsTotalElements = ref(0)
const ieltsTotalPages = ref(0)

// 托福单词数据
const toeflWords = ref([])
const toeflCurrentPage = ref(1)
const toeflPageSize = ref(20)
const toeflTotalElements = ref(0)
const toeflTotalPages = ref(0)

// 加载状态
const loading = ref(false)

// 雅思视频数据
const ieltsSpeakingVideos = ref<any[]>([])
const ieltsReadingVideos = ref<any[]>([])
const ieltsListeningVideos = ref<any[]>([])
const ieltsWritingVideos = ref<any[]>([])

// 托福视频数据
const toeflSpeakingVideos = ref<any[]>([])
const toeflReadingVideos = ref<any[]>([])
const toeflListeningVideos = ref<any[]>([])
const toeflWritingVideos = ref<any[]>([])

// 作业数据变量
const ieltsSpeakingHomework = ref<any[]>([])
const ieltsReadingHomework = ref<any[]>([])
const ieltsListeningHomework = ref<any[]>([])
const ieltsWritingHomework = ref<any[]>([])
const toeflSpeakingHomework = ref<any[]>([])
const toeflReadingHomework = ref<any[]>([])
const toeflListeningHomework = ref<any[]>([])
const toeflWritingHomework = ref<any[]>([])

// 学生作业提交
const mySubmissions = ref<any[]>([])

// 提交作业对话框状态
const submitDialogVisible = ref(false)
const submitForm = ref({ title: '', content: '' })
const submitting = ref(false)
const currentHomeworkId = ref('')

// 从后端API获取雅思单词数据（带分页）
const fetchIeltsWords = async () => {
  loading.value = true
  try {
    console.log('Fetching IELTS words from API...')
    const response = await fetch(`http://localhost:8080/api/words/age-group/21/page?page=${ieltsCurrentPage.value - 1}&size=${ieltsPageSize.value}`)
    console.log('Response status:', response.status)
    if (!response.ok) {
      throw new Error('Failed to fetch IELTS words with status: ' + response.status)
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
    console.log('Formatted IELTS words:', formattedWords)
    ieltsWords.value = formattedWords
    ieltsTotalElements.value = data.totalElements
    ieltsTotalPages.value = data.totalPages
    
    console.log('IELTS words updated:', ieltsWords.value)
    console.log('IELTS total elements:', ieltsTotalElements.value)
    console.log('IELTS total pages:', ieltsTotalPages.value)
  } catch (error) {
    console.error('Failed to fetch IELTS words:', error)
    // 显示错误信息给用户
    ElMessage.error('获取雅思单词数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 从后端API获取托福单词数据（带分页）
const fetchToeflWords = async () => {
  loading.value = true
  try {
    console.log('Fetching TOEFL words from API...')
    const response = await fetch(`http://localhost:8080/api/words/age-group/22/page?page=${toeflCurrentPage.value - 1}&size=${toeflPageSize.value}`)
    console.log('Response status:', response.status)
    if (!response.ok) {
      throw new Error('Failed to fetch TOEFL words with status: ' + response.status)
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
    console.log('Formatted TOEFL words:', formattedWords)
    toeflWords.value = formattedWords
    toeflTotalElements.value = data.totalElements
    toeflTotalPages.value = data.totalPages
    
    console.log('TOEFL words updated:', toeflWords.value)
    console.log('TOEFL total elements:', toeflTotalElements.value)
    console.log('TOEFL total pages:', toeflTotalPages.value)
  } catch (error) {
    console.error('Failed to fetch TOEFL words:', error)
    // 显示错误信息给用户
    ElMessage.error('获取托福单词数据失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchIeltsWords()
  fetchToeflWords()
  fetchVideos()
  fetchHomework()
  fetchMySubmissions()
})

// 雅思分页处理函数
const handleIeltsSizeChange = (size: number) => {
  ieltsPageSize.value = size
  ieltsCurrentPage.value = 1
  fetchIeltsWords()
}

const handleIeltsCurrentChange = (current: number) => {
  ieltsCurrentPage.value = current
  fetchIeltsWords()
}

// 托福分页处理函数
const handleToeflSizeChange = (size: number) => {
  toeflPageSize.value = size
  toeflCurrentPage.value = 1
  fetchToeflWords()
}

const handleToeflCurrentChange = (current: number) => {
  toeflCurrentPage.value = current
  fetchToeflWords()
}

// 播放发音
const playAudio = (wordId: string) => {
  console.log('Playing audio for word:', wordId)
}

// 添加到学习列表
const addToList = async (wordId: string) => {
  try {
    console.log('Adding word to learning list with id:', wordId);
    // 为了测试方便，使用固定的用户ID
    const userId = 1;
    const response = await fetch(`http://localhost:8080/api/word-learning-list/add?userId=${userId}&wordId=${wordId}`, {
      method: 'POST'
    });
    console.log('Response status:', response.status);
    const result = await response.text();
    console.log('Response result:', result);
    if (response.ok) {
      ElMessage.success(result);
    } else {
      ElMessage.error(result);
    }
  } catch (error) {
    console.error('Failed to add word to learning list:', error);
    ElMessage.error('添加失败，请稍后重试');
  }
}

// 从后端API获取视频数据
const fetchVideos = async () => {
  try {
    // 调用后端API获取视频数据
    console.log('Fetching videos from API...')
    const response = await api.videos.getAll()
    console.log('API response:', response)
    
    // 过滤并分类雅思视频
    const ieltsVideos = response.filter((video: any) => video.category === '雅思')
    ieltsSpeakingVideos.value = ieltsVideos.filter((video: any) => video.type === '口语')
    ieltsReadingVideos.value = ieltsVideos.filter((video: any) => video.type === '阅读')
    ieltsListeningVideos.value = ieltsVideos.filter((video: any) => video.type === '听力')
    ieltsWritingVideos.value = ieltsVideos.filter((video: any) => video.type === '写作')
    console.log('IELTS Speaking videos:', ieltsSpeakingVideos.value)
    
    // 过滤并分类托福视频
    const toeflVideos = response.filter((video: any) => video.category === '托福')
    toeflSpeakingVideos.value = toeflVideos.filter((video: any) => video.type === '口语')
    toeflReadingVideos.value = toeflVideos.filter((video: any) => video.type === '阅读')
    toeflListeningVideos.value = toeflVideos.filter((video: any) => video.type === '听力')
    toeflWritingVideos.value = toeflVideos.filter((video: any) => video.type === '写作')
    console.log('TOEFL Speaking videos:', toeflSpeakingVideos.value)
    
    // 检查视频URL
    if (ieltsVideos.length > 0) {
      console.log('First IELTS video URL:', ieltsVideos[0].videoUrl)
      console.log('Full IELTS video URL:', `http://localhost:8080${ieltsVideos[0].videoUrl}`)
    }
    if (toeflVideos.length > 0) {
      console.log('First TOEFL video URL:', toeflVideos[0].videoUrl)
      console.log('Full TOEFL video URL:', `http://localhost:8080${toeflVideos[0].videoUrl}`)
    }
  } catch (error) {
    console.error('Failed to fetch videos:', error)
    // 使用本地数据作为后备
    console.log('Using local video data')
  }
}

// 从后端API获取作业数据
const fetchHomework = async () => {
  try {
    // 调用后端API获取作业数据
    console.log('Fetching homework from API...')
    const ieltsResponse = await api.homeworks.getByCategory('雅思')
    const toeflResponse = await api.homeworks.getByCategory('托福')
    console.log('IELTS API response:', ieltsResponse)
    console.log('TOEFL API response:', toeflResponse)
    
    // 按类型分类雅思作业
    ieltsSpeakingHomework.value = ieltsResponse.filter((homework: any) => homework.type === '口语')
    ieltsReadingHomework.value = ieltsResponse.filter((homework: any) => homework.type === '阅读')
    ieltsListeningHomework.value = ieltsResponse.filter((homework: any) => homework.type === '听力')
    ieltsWritingHomework.value = ieltsResponse.filter((homework: any) => homework.type === '写作')
    
    // 按类型分类托福作业
    toeflSpeakingHomework.value = toeflResponse.filter((homework: any) => homework.type === '口语')
    toeflReadingHomework.value = toeflResponse.filter((homework: any) => homework.type === '阅读')
    toeflListeningHomework.value = toeflResponse.filter((homework: any) => homework.type === '听力')
    toeflWritingHomework.value = toeflResponse.filter((homework: any) => homework.type === '写作')
    
    console.log('IELTS Speaking homework:', ieltsSpeakingHomework.value)
    console.log('TOEFL Speaking homework:', toeflSpeakingHomework.value)
  } catch (error) {
    console.error('Failed to fetch homework:', error)
    // 使用本地数据作为后备
    console.log('Using local homework data')
  }
}

// 获取学生自己的作业提交
const fetchMySubmissions = async () => {
  try {
    console.log('Fetching my submissions from API...')
    const response = await api.homeworks.getMySubmissions()
    mySubmissions.value = response
    console.log('My submissions:', mySubmissions.value)
  } catch (error) {
    console.error('Failed to fetch my submissions:', error)
  }
}

// 获取某个作业的提交记录
const getSubmissionForHomework = (homeworkId: any) => {
  return mySubmissions.value.find((sub: any) => sub.homework?.id === homeworkId)
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 合并所有作业
const getAllHomework = () => {
  return [
    ...ieltsSpeakingHomework.value, ...ieltsReadingHomework.value, ...ieltsListeningHomework.value, ...ieltsWritingHomework.value,
    ...toeflSpeakingHomework.value, ...toeflReadingHomework.value, ...toeflListeningHomework.value, ...toeflWritingHomework.value
  ]
}

// 打开提交作业对话框
const submitHomework = (homeworkId: string) => {
  console.log('Submitting homework:', homeworkId)
  currentHomeworkId.value = homeworkId
  // 查找作业信息
  const allHomework = getAllHomework()
  const homework = allHomework.find(h => h.id === homeworkId)
  if (homework) {
    submitForm.value.title = homework.title
    submitForm.value.content = ''
    submitDialogVisible.value = true
  }
}

// 确认提交作业
const confirmSubmit = async () => {
  if (!submitForm.value.content) {
    ElMessage.error('请输入作业内容')
    return
  }
  
  if (!currentHomeworkId.value) {
    ElMessage.error('作业ID不能为空')
    return
  }
  
  try {
    submitting.value = true
    console.log('Submitting homework with ID:', currentHomeworkId.value)
    // 调用后端API提交作业
    const response = await api.homeworks.submit({
      homeworkId: currentHomeworkId.value,
      content: submitForm.value.content,
      image: ''
    })
    console.log('作业提交成功:', response)
    ElMessage.success('作业提交成功')
    submitDialogVisible.value = false
  } catch (error: any) {
    console.error('作业提交失败:', error)
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.ielts-toefl-english {
  padding: 20px 0;
}

.ielts-toefl-english h2 {
  margin-bottom: 20px;
  color: #303133;
}

.module-card {
  margin-bottom: 30px;
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
  font-size: 18px;
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

.homework-actions {
  margin-top: 15px;
  display: flex;
  justify-content: flex-end;
}

.submission-info {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #e4e7ed;
}

.submission-info h5 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
  font-weight: 600;
}

.submission-content {
  margin: 0 0 10px 0;
  color: #303133;
  line-height: 1.6;
}

.submission-image {
  margin: 10px 0;
  max-width: 300px;
}

.submission-image img {
  width: 100%;
  border-radius: 4px;
}

.submission-time {
  margin: 10px 0;
  color: #909399;
  font-size: 13px;
}

.grade-result {
  margin-top: 15px;
  padding: 15px;
  background-color: #f0f9eb;
  border-radius: 4px;
  border: 1px solid #e1f3d8;
}

.grade-result .score {
  color: #67C23A;
  font-weight: 600;
  font-size: 16px;
  margin: 5px 0;
}

.grade-result .feedback {
  color: #606266;
  margin: 5px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 10px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.video-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.video-thumbnail {
  position: relative;
  height: 180px;
}

.video-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 48px;
}

.video-info {
  padding: 15px;
}

.video-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
}

.video-info p {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 14px;
}

.video-type {
  color: #409EFF;
  font-size: 12px;
  margin-top: 10px;
}

.homework-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.homework-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  background: #f5f7fa;
}

.homework-info h4 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 18px;
}

.homework-content {
  margin: 0 0 15px 0;
  color: #606266;
  line-height: 1.5;
}

.homework-image {
  margin: 15px 0;
  max-width: 300px;
}

.homework-image img {
  width: 100%;
  border-radius: 4px;
}

.homework-time {
  margin: 10px 0 0 0;
  color: #909399;
  font-size: 14px;
}

.homework-type {
  color: #409EFF;
  font-size: 12px;
  margin-top: 5px;
}

@media screen and (max-width: 768px) {
  .word-item {
    flex-direction: column;
    gap: 10px;
  }
  
  .word-actions {
    align-self: flex-end;
  }
  
  .video-grid {
    grid-template-columns: 1fr;
  }
  
  .video-thumbnail {
    height: 150px;
  }
  
  .homework-image {
    max-width: 100%;
  }
}
</style>
