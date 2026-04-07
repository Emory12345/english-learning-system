<template>
  <div class="cet-english">
    <h2>四六级英语</h2>
    
    <!-- 四六级选项卡 -->
    <el-tabs v-model="activeLevel" style="margin-bottom: 20px;">
      <el-tab-pane label="四级" name="CET4">
        <div class="level-content">
          <!-- 学习模块标签页 -->
          <el-tabs v-model="activeTab">
            <el-tab-pane label="单词" name="words">
              <el-card class="module-card">
                <div class="word-list">
                  <div v-for="word in cet4Words" :key="word.id" class="word-item">
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
                  
                  <!-- 四级分页组件 -->
                  <div class="pagination" v-if="cet4TotalPages > 1">
                    <el-pagination
                      v-model:current-page="cet4CurrentPage"
                      v-model:page-size="cet4PageSize"
                      :page-sizes="[10, 20, 50, 100]"
                      layout="total, sizes, prev, pager, next, jumper"
                      :total="cet4TotalElements"
                      @size-change="handleCET4SizeChange"
                      @current-change="handleCET4CurrentChange"
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
            <div v-for="video in cet4SpeakingVideos" :key="video.id" class="video-card">
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
            <el-empty v-if="cet4SpeakingVideos.length === 0" description="暂无口语视频" />
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
            <div v-for="homework in cet4SpeakingHomework" :key="homework.id" class="homework-item">
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
            <el-empty v-if="cet4SpeakingHomework.length === 0" description="暂无口语作业" />
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
            <div v-for="video in cet4ReadingVideos" :key="video.id" class="video-card">
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
            <el-empty v-if="cet4ReadingVideos.length === 0" description="暂无阅读视频" />
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
            <div v-for="homework in cet4ReadingHomework" :key="homework.id" class="homework-item">
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
            <el-empty v-if="cet4ReadingHomework.length === 0" description="暂无阅读作业" />
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
            <div v-for="video in cet4ListeningVideos" :key="video.id" class="video-card">
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
            <el-empty v-if="cet4ListeningVideos.length === 0" description="暂无听力视频" />
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
            <div v-for="homework in cet4ListeningHomework" :key="homework.id" class="homework-item">
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
            <el-empty v-if="cet4ListeningHomework.length === 0" description="暂无听力作业" />
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
            <div v-for="video in cet4WritingVideos" :key="video.id" class="video-card">
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
            <el-empty v-if="cet4WritingVideos.length === 0" description="暂无写作视频" />
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
            <div v-for="homework in cet4WritingHomework" :key="homework.id" class="homework-item">
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
            <el-empty v-if="cet4WritingHomework.length === 0" description="暂无写作作业" />
          </div>
        </el-card>
      </el-tab-pane>
          </el-tabs>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="六级" name="CET6">
        <div class="level-content">
          <!-- 学习模块标签页 -->
          <el-tabs v-model="activeTab">
            <el-tab-pane label="单词" name="words">
              <el-card class="module-card">
                <div class="word-list">
                  <div v-for="word in cet6Words" :key="word.id" class="word-item">
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
                  
                  <!-- 六级分页组件 -->
                  <div class="pagination" v-if="cet6TotalPages > 1">
                    <el-pagination
                      v-model:current-page="cet6CurrentPage"
                      v-model:page-size="cet6PageSize"
                      :page-sizes="[10, 20, 50, 100]"
                      layout="total, sizes, prev, pager, next, jumper"
                      :total="cet6TotalElements"
                      @size-change="handleCET6SizeChange"
                      @current-change="handleCET6CurrentChange"
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
                  <div v-for="video in cet6SpeakingVideos" :key="video.id" class="video-card">
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
                  <el-empty v-if="cet6SpeakingVideos.length === 0" description="暂无口语视频" />
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
                  <div v-for="homework in cet6SpeakingHomework" :key="homework.id" class="homework-item">
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
                  <el-empty v-if="cet6SpeakingHomework.length === 0" description="暂无口语作业" />
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
                  <div v-for="video in cet6ReadingVideos" :key="video.id" class="video-card">
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
                  <el-empty v-if="cet6ReadingVideos.length === 0" description="暂无阅读视频" />
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
                  <div v-for="homework in cet6ReadingHomework" :key="homework.id" class="homework-item">
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
                  <el-empty v-if="cet6ReadingHomework.length === 0" description="暂无阅读作业" />
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
                  <div v-for="video in cet6ListeningVideos" :key="video.id" class="video-card">
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
                  <el-empty v-if="cet6ListeningVideos.length === 0" description="暂无听力视频" />
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
                  <div v-for="homework in cet6ListeningHomework" :key="homework.id" class="homework-item">
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
                  <el-empty v-if="cet6ListeningHomework.length === 0" description="暂无听力作业" />
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
                  <div v-for="video in cet6WritingVideos" :key="video.id" class="video-card">
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
                  <el-empty v-if="cet6WritingVideos.length === 0" description="暂无写作视频" />
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
                  <div v-for="homework in cet6WritingHomework" :key="homework.id" class="homework-item">
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
                  <el-empty v-if="cet6WritingHomework.length === 0" description="暂无写作作业" />
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
        <el-form-item label="上传音频">
          <el-upload
            class="upload-demo"
            :http-request="customUpload"
            :file-list="fileList"
            :auto-upload="true"
            :show-file-list="false"
            accept="audio/*"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                只能上传MP3、WAV等音频文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="submitForm.audio">
          <div class="uploaded-audio">
            <audio :src="`http://localhost:8080${submitForm.audio}`" controls></audio>
            <el-button type="danger" size="small" @click="removeAudio">删除音频</el-button>
          </div>
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
import { ElMessage } from 'element-plus'
import { api } from '../../api'

// 激活的等级（CET4或CET6）
const activeLevel = ref('CET4')

// 激活的标签页
const activeTab = ref('words')

// 四级单词数据
const cet4Words = ref([])
const cet4CurrentPage = ref(1)
const cet4PageSize = ref(20)
const cet4TotalElements = ref(0)
const cet4TotalPages = ref(0)

// 六级单词数据
const cet6Words = ref([])
const cet6CurrentPage = ref(1)
const cet6PageSize = ref(20)
const cet6TotalElements = ref(0)
const cet6TotalPages = ref(0)

// 四级视频数据
const cet4SpeakingVideos = ref<any[]>([])
const cet4ReadingVideos = ref<any[]>([])
const cet4ListeningVideos = ref<any[]>([])
const cet4WritingVideos = ref<any[]>([])

// 六级视频数据
const cet6SpeakingVideos = ref<any[]>([])
const cet6ReadingVideos = ref<any[]>([])
const cet6ListeningVideos = ref<any[]>([])
const cet6WritingVideos = ref<any[]>([])

// 作业数据变量
const cet4SpeakingHomework = ref<any[]>([])
const cet4ReadingHomework = ref<any[]>([])
const cet4ListeningHomework = ref<any[]>([])
const cet4WritingHomework = ref<any[]>([])
const cet6SpeakingHomework = ref<any[]>([])
const cet6ReadingHomework = ref<any[]>([])
const cet6ListeningHomework = ref<any[]>([])
const cet6WritingHomework = ref<any[]>([])

// 学生作业提交
const mySubmissions = ref<any[]>([])

// 提交作业对话框状态
const submitDialogVisible = ref(false)
const submitForm = ref({ title: '', content: '', audio: '' })
const submitting = ref(false)
const currentHomeworkId = ref('')
const fileList = ref([])

// 从API获取四级单词（带分页）
const fetchCET4Words = async () => {
  try {
    console.log('Fetching CET4 words from API...')
    const response = await fetch(`http://localhost:8080/api/words/age-group/25/page?page=${cet4CurrentPage.value - 1}&size=${cet4PageSize.value}`)
    console.log('Response status:', response.status)
    if (!response.ok) {
      throw new Error('Failed to fetch CET4 words with status: ' + response.status)
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
    console.log('Formatted CET4 words:', formattedWords)
    cet4Words.value = formattedWords
    cet4TotalElements.value = data.totalElements
    cet4TotalPages.value = data.totalPages
    
    console.log('CET4 words updated:', cet4Words.value)
    console.log('CET4 total elements:', cet4TotalElements.value)
    console.log('CET4 total pages:', cet4TotalPages.value)
  } catch (error) {
    console.error('Failed to fetch CET4 words:', error)
    ElMessage.error('获取四级单词失败，请稍后重试')
  }
}

// 从API获取六级单词（带分页）
const fetchCET6Words = async () => {
  try {
    console.log('Fetching CET6 words from API...')
    const response = await fetch(`http://localhost:8080/api/words/age-group/26/page?page=${cet6CurrentPage.value - 1}&size=${cet6PageSize.value}`)
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
    cet6Words.value = formattedWords
    cet6TotalElements.value = data.totalElements
    cet6TotalPages.value = data.totalPages
    
    console.log('CET6 words updated:', cet6Words.value)
    console.log('CET6 total elements:', cet6TotalElements.value)
    console.log('CET6 total pages:', cet6TotalPages.value)
  } catch (error) {
    console.error('Failed to fetch CET6 words:', error)
    ElMessage.error('获取六级单词失败，请稍后重试')
  }
}

// 四级分页处理函数
const handleCET4SizeChange = (size: number) => {
  cet4PageSize.value = size
  cet4CurrentPage.value = 1
  fetchCET4Words()
}

const handleCET4CurrentChange = (current: number) => {
  cet4CurrentPage.value = current
  fetchCET4Words()
}

// 六级分页处理函数
const handleCET6SizeChange = (size: number) => {
  cet6PageSize.value = size
  cet6CurrentPage.value = 1
  fetchCET6Words()
}

const handleCET6CurrentChange = (current: number) => {
  cet6CurrentPage.value = current
  fetchCET6Words()
}

// 页面加载时获取数据
onMounted(() => {
  fetchCET4Words()
  fetchCET6Words()
  fetchVideos()
  fetchHomework()
  fetchMySubmissions()
})

// 播放发音
const playAudio = async (wordId: string) => {
  try {
    // 获取单词信息
    const response = await fetch(`http://localhost:8080/api/words/${wordId}`)
    if (response.ok) {
      const word = await response.json()
      // 构建发音URL
      const audioUrl = `https://api.dictionaryapi.dev/media/pronunciations/en/${word.word.toLowerCase()}-us.mp3`
      // 创建音频对象并播放
      const audio = new Audio(audioUrl)
      await audio.play()
    }
  } catch (error) {
    console.error('Failed to play audio:', error)
    ElMessage.error('播放发音失败，请稍后重试')
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

// 从后端API获取视频数据
const fetchVideos = async () => {
  try {
    // 调用后端API获取视频数据
    console.log('Fetching videos from API...')
    const response = await api.videos.getAll()
    console.log('API response:', response)
    
    // 过滤并分类四级视频
    const cet4Videos = response.filter((video: any) => video.category === '四级英语')
    cet4SpeakingVideos.value = cet4Videos.filter((video: any) => video.type === '口语')
    cet4ReadingVideos.value = cet4Videos.filter((video: any) => video.type === '阅读')
    cet4ListeningVideos.value = cet4Videos.filter((video: any) => video.type === '听力')
    cet4WritingVideos.value = cet4Videos.filter((video: any) => video.type === '写作')
    console.log('CET4 Speaking videos:', cet4SpeakingVideos.value)
    
    // 过滤并分类六级视频
    const cet6Videos = response.filter((video: any) => video.category === '六级英语')
    cet6SpeakingVideos.value = cet6Videos.filter((video: any) => video.type === '口语')
    cet6ReadingVideos.value = cet6Videos.filter((video: any) => video.type === '阅读')
    cet6ListeningVideos.value = cet6Videos.filter((video: any) => video.type === '听力')
    cet6WritingVideos.value = cet6Videos.filter((video: any) => video.type === '写作')
    console.log('CET6 Speaking videos:', cet6SpeakingVideos.value)
    
    // 检查视频URL
    if (cet4Videos.length > 0) {
      console.log('First CET4 video URL:', cet4Videos[0].videoUrl)
      console.log('Full CET4 video URL:', `http://localhost:8080${cet4Videos[0].videoUrl}`)
    }
    if (cet6Videos.length > 0) {
      console.log('First CET6 video URL:', cet6Videos[0].videoUrl)
      console.log('Full CET6 video URL:', `http://localhost:8080${cet6Videos[0].videoUrl}`)
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
    const cet4Response = await api.homeworks.getByCategory('四级英语')
    const cet6Response = await api.homeworks.getByCategory('六级英语')
    console.log('CET4 API response:', cet4Response)
    console.log('CET6 API response:', cet6Response)
    
    // 按类型分类四级作业
    cet4SpeakingHomework.value = cet4Response.filter((homework: any) => homework.type === '口语')
    cet4ReadingHomework.value = cet4Response.filter((homework: any) => homework.type === '阅读')
    cet4ListeningHomework.value = cet4Response.filter((homework: any) => homework.type === '听力')
    cet4WritingHomework.value = cet4Response.filter((homework: any) => homework.type === '写作')
    
    // 按类型分类六级作业
    cet6SpeakingHomework.value = cet6Response.filter((homework: any) => homework.type === '口语')
    cet6ReadingHomework.value = cet6Response.filter((homework: any) => homework.type === '阅读')
    cet6ListeningHomework.value = cet6Response.filter((homework: any) => homework.type === '听力')
    cet6WritingHomework.value = cet6Response.filter((homework: any) => homework.type === '写作')
    
    console.log('CET4 Speaking homework:', cet4SpeakingHomework.value)
    console.log('CET6 Speaking homework:', cet6SpeakingHomework.value)
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
    ...cet4SpeakingHomework.value, ...cet4ReadingHomework.value, ...cet4ListeningHomework.value, ...cet4WritingHomework.value,
    ...cet6SpeakingHomework.value, ...cet6ReadingHomework.value, ...cet6ListeningHomework.value, ...cet6WritingHomework.value
  ]
}

// 自定义上传方法
const customUpload = async (options: any) => {
  try {
    const response = await api.upload.audio(options.file)
    if (response.success) {
      submitForm.value.audio = response.audioUrl
      ElMessage.success('音频上传成功')
    } else {
      ElMessage.error(response.message || '上传失败')
    }
  } catch (error) {
    ElMessage.error('音频上传失败，请重试')
    console.error('Upload error:', error)
  }
}

// 删除音频
const removeAudio = () => {
  submitForm.value.audio = ''
  fileList.value = []
  ElMessage.success('音频已删除')
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
    submitForm.value.audio = ''
    fileList.value = []
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
      audio: submitForm.value.audio
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
.cet-english {
  padding: 20px 0;
}

.cet-english h2 {
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

.uploaded-audio {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.uploaded-audio audio {
  max-width: 300px;
  border-radius: 4px;
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
  
  .uploaded-image img {
    max-width: 150px;
  }
}
</style>
