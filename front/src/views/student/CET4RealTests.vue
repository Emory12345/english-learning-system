<template>
  <div class="cet4-real-tests">
    <h2>大学英语四级考试真题</h2>
    <p class="description">本网站提供了从2019年到2025年的大学英语四级考试（CET4）真题和解析，包括作文、听力、阅读和翻译等部分。考生可以按照考试流程进行练习。</p>
    
    <!-- 真题详情弹窗 -->
    <div v-if="showDetail" class="modal-overlay" @click="closeDetail">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ currentPaperTitle }}</h3>
          <button class="close-btn" @click="closeDetail">×</button>
        </div>
        <div class="modal-body">
          <div v-if="loading" class="loading">加载中...</div>
          <div v-else-if="questions.length > 0 && questions[0]">
            <!-- 试卷标题 -->
            <div class="paper-title">
              <h2>{{ questions[0].title }}(含听力)</h2>
            </div>
            
            <!-- 听力播放器 -->
            <div class="audio-player">
              <audio controls>
                <source src="/cet4_2022_09_1.mp3" type="audio/mpeg">
                您的浏览器不支持音频播放。
              </audio>
            </div>
            
            <!-- 真题内容 -->
            <div class="paper-content">
              <h3>大学英语四级考试 {{ questions[0].title }}</h3>
              
              <div v-if="questions[0].sections" v-for="(section, sectionIndex) in questions[0].sections" :key="sectionIndex" class="section-item">
                <div class="section-title">
                  <h4>{{ section.title }} {{ section.time }}</h4>
                </div>
                <div v-if="section.title === 'Part II'" class="audio-player">
                  <audio controls>
                    <source src="/cet4_2022_09_1.mp3" type="audio/mpeg">
                    您的浏览器不支持音频播放。
                  </audio>
                </div>
                <div v-if="section.directions || section.direction" class="section-direction">
                  {{ section.directions || section.direction }}
                </div>
                <div v-if="section.passage" class="section-passage">
                  {{ section.passage }}
                </div>
                <div v-if="section.content" class="section-content">
                  {{ section.content }}
                </div>
                <div v-if="section.wordBank" class="section-word-bank">
                  <h6>Word Bank:</h6>
                  <div class="word-bank-items">
                    <div 
                      v-for="(word, idx) in section.wordBank" 
                      :key="idx"
                      class="word-bank-item"
                      @click="selectWordBankItem(sectionIndex, idx)"
                    >
                      {{ word }}
                    </div>
                  </div>
                </div>
                <!-- 作文上传 - 只在主Part I section显示 -->
                <div v-if="section.title === 'Part I Writing'" class="subjective-submit-area">
                  <h5>作文上传</h5>
                  <div class="upload-area-inline">
                    <input type="file" id="writingFile" accept="image/*" @change="handleWritingFileUpload" />
                    <label for="writingFile" class="upload-label-inline">
                      <span v-if="!writingImage">点击上传作文图片</span>
                      <span v-else>已选择: {{ writingImage.name }}</span>
                    </label>
                  </div>
                  <div v-if="writingImagePreview" class="preview-inline">
                    <img :src="writingImagePreview" alt="作文预览" class="image-preview-inline" />
                  </div>
                </div>
                <!-- 翻译上传 - 只在主Part IV section显示 -->
                <div v-if="section.title === 'Part IV Translation'" class="subjective-submit-area">
                  <h5>翻译上传</h5>
                  <div class="upload-area-inline">
                    <input type="file" id="translationFile" accept="image/*" @change="handleTranslationFileUpload" />
                    <label for="translationFile" class="upload-label-inline">
                      <span v-if="!translationImage">点击上传翻译图片</span>
                      <span v-else>已选择: {{ translationImage.name }}</span>
                    </label>
                  </div>
                  <div v-if="translationImagePreview" class="preview-inline">
                    <img :src="translationImagePreview" alt="翻译预览" class="image-preview-inline" />
                  </div>
                </div>
                <div v-if="section.questions" class="section-questions">
                  <div v-for="(question, qIndex) in section.questions" :key="question.id || qIndex" class="question-item">
                    <div class="question-content">{{ question.content }}</div>
                    <div v-if="question.options" class="question-options">
                      <div 
                        v-for="(option, idx) in question.options" 
                        :key="idx"
                        class="option-item"
                        @click="selectOption(sectionIndex, qIndex, idx)"
                      >
                        <div class="radio-button">
                          <div 
                            class="radio-inner"
                            :class="{ 'selected': question.selectedAnswer === idx, 'correct': showAnswer && option.startsWith(question.answer), 'incorrect': showAnswer && question.selectedAnswer === idx && !option.startsWith(question.answer) }"
                          ></div>
                        </div>
                        <div class="option-text">{{ option }}</div>
                      </div>
                    </div>
                    <div v-if="showAnswer && question.answer" class="question-answer">
                      <strong>答案：</strong>{{ question.answer }}
                    </div>
                  </div>
                </div>
                <div v-if="section.sections" class="sub-sections">
                  <div v-for="(subSection, subSectionIndex) in section.sections" :key="subSectionIndex" class="sub-section-item">
                    <div class="sub-section-title">
                      <h5>{{ subSection.title }}</h5>
                    </div>
                    <div v-if="subSection.directions" class="sub-section-direction">
                      {{ subSection.directions }}
                    </div>
                    <div v-if="subSection.passage" class="sub-section-passage">
                      {{ subSection.passage }}
                    </div>
                    <div v-if="subSection.wordBank" class="section-word-bank">
                      <h6>Word Bank:</h6>
                      <div class="word-bank-items">
                        <div 
                          v-for="(word, idx) in subSection.wordBank" 
                          :key="idx"
                          class="word-bank-item"
                          @click="selectWordBankItem(sectionIndex, idx)"
                        >
                          {{ word }}
                        </div>
                      </div>
                    </div>
                    <div v-if="subSection.questions" class="section-questions">
                      <div v-for="(question, qIndex) in subSection.questions" :key="question.id || qIndex" class="question-item">
                        <div class="question-content">{{ question.content }}</div>
                        <div v-if="question.options" class="question-options">
                          <div 
                            v-for="(option, idx) in question.options" 
                            :key="idx"
                            class="option-item"
                            @click="selectOption(sectionIndex, qIndex, idx)"
                          >
                            <div class="radio-button">
                              <div 
                                class="radio-inner"
                                :class="{ 'selected': question.selectedAnswer === idx, 'correct': showAnswer && option.startsWith(question.answer), 'incorrect': showAnswer && question.selectedAnswer === idx && !option.startsWith(question.answer) }"
                              ></div>
                            </div>
                            <div class="option-text">{{ option }}</div>
                          </div>
                        </div>
                        <div v-else-if="subSection.title === 'Section B'" class="matching-options">
                          <div class="matching-option">
                            <input 
                              type="text" 
                              v-model="question.selectedAnswer" 
                              class="matching-input"
                              placeholder="请输入选项字母 (A-M)"
                            />
                          </div>
                        </div>
                        <div v-if="showAnswer && question.answer" class="question-answer">
                          <strong>答案：</strong>{{ question.answer }}
                        </div>
                      </div>
                    </div>
                    <div v-if="subSection.passages" class="passages">
                      <div v-for="(passage, passageIndex) in subSection.passages" :key="passageIndex" class="passage-item">
                        <div v-if="passage.content" class="passage-content">
                          {{ passage.content }}
                        </div>
                        <div v-if="passage.questions_info" class="passage-questions-info">
                          {{ passage.questions_info }}
                        </div>
                        <div v-if="passage.questions" class="passage-questions">
                          <div v-for="(question, qIndex) in passage.questions" :key="question.id || qIndex" class="question-item">
                            <div class="question-content" v-if="question.content">{{ question.content }}</div>
                            <div v-if="question.options" class="question-options">
                              <div 
                                v-for="(option, idx) in question.options" 
                                :key="idx"
                                class="option-item"
                                @click="selectPassageOption(sectionIndex, subSectionIndex, passageIndex, qIndex, idx)"
                              >
                                <div class="radio-button">
                                  <div 
                                    class="radio-inner"
                                    :class="{ 'selected': question.selectedAnswer === idx, 'correct': showAnswer && option.startsWith(question.answer), 'incorrect': showAnswer && question.selectedAnswer === idx && !option.startsWith(question.answer) }"
                                  ></div>
                                </div>
                                <div class="option-text">{{ option }}</div>
                              </div>
                            </div>
                            <div v-if="showAnswer && question.answer" class="question-answer">
                              <strong>答案：</strong>{{ question.answer }}
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else>
            <div class="error-message">
              <p>加载真题失败，请重试。</p>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <div v-if="!isSubmitted">
            <button class="btn btn-submit" @click="submitHomework">提交作业</button>
          </div>
          <div v-else>
            <div class="submission-status">
              <span class="status-badge" :class="submissionStatus">{{ submissionStatusText }}</span>
              <button v-if="submissionStatus === 'graded'" class="btn" @click="toggleAnswer">查看分数和答案</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    

    
    <!-- 2025年四级真题 -->
    <div class="year-section">
      <h3>2025年四级真题及答案</h3>
      <div class="tests-grid">
        <div class="test-card" @click="showPaperDetail('2025年06月四级真题第1套')">2025年06月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2025年06月四级真题第1套')">2025年06月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2025年06月四级真题第2套')">2025年06月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2025年06月四级真题第2套')">2025年06月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2025年06月四级真题第3套')">2025年06月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2025年06月四级真题第3套')">2025年06月四级真题第3套答案</div>
        <div class="test-card" @click="showPaperDetail('2025年12月四级真题第1套')">2025年12月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2025年12月四级真题第1套')">2025年12月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2025年12月四级真题第2套')">2025年12月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2025年12月四级真题第2套')">2025年12月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2025年12月四级真题第3套')">2025年12月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2025年12月四级真题第3套')">2025年12月四级真题第3套答案</div>
      </div>
    </div>
    
    <!-- 2024年四级真题 -->
    <div class="year-section">
      <h3>2024年四级真题及答案</h3>
      <div class="tests-grid">
        <div class="test-card" @click="showPaperDetail('2024年06月四级真题第1套')">2024年06月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2024年06月四级真题第1套')">2024年06月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2024年06月四级真题第2套')">2024年06月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2024年06月四级真题第2套')">2024年06月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2024年06月四级真题第3套')">2024年06月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2024年06月四级真题第3套')">2024年06月四级真题第3套答案</div>
        <div class="test-card" @click="showPaperDetail('2024年12月四级真题第1套')">2024年12月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2024年12月四级真题第1套')">2024年12月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2024年12月四级真题第2套')">2024年12月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2024年12月四级真题第2套')">2024年12月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2024年12月四级真题第3套')">2024年12月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2024年12月四级真题第3套')">2024年12月四级真题第3套答案</div>
      </div>
    </div>
    
    <!-- 2023年四级真题 -->
    <div class="year-section">
      <h3>2023年四级真题及答案</h3>
      <div class="tests-grid">
        <div class="test-card" @click="showPaperDetail('2023年06月四级真题第1套')">2023年06月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2023年06月四级真题第1套')">2023年06月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2023年06月四级真题第2套')">2023年06月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2023年06月四级真题第2套')">2023年06月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2023年06月四级真题第3套')">2023年06月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2023年06月四级真题第3套')">2023年06月四级真题第3套答案</div>
        <div class="test-card" @click="showPaperDetail('2023年12月四级真题第1套')">2023年12月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2023年12月四级真题第1套')">2023年12月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2023年12月四级真题第2套')">2023年12月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2023年12月四级真题第2套')">2023年12月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2023年12月四级真题第3套')">2023年12月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2023年12月四级真题第3套')">2023年12月四级真题第3套答案</div>
      </div>
    </div>
    
    <!-- 2022年四级真题 -->
    <div class="year-section">
      <h3>2022年四级真题及答案</h3>
      <div class="tests-grid">
        <div class="test-card" @click="showPaperDetail('2022年06月四级真题第1套')">2022年06月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2022年06月四级真题第1套')">2022年06月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2022年06月四级真题第2套')">2022年06月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2022年06月四级真题第2套')">2022年06月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2022年06月四级真题第3套')">2022年06月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2022年06月四级真题第3套')">2022年06月四级真题第3套答案</div>
        <div class="test-card" @click="showPaperDetail('2022年09月四级真题')">2022年09月四级真题（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2022年09月四级真题')">2022年09月四级真题答案</div>
        <div class="test-card" @click="showPaperDetail('2022年12月四级真题第1套')">2022年12月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2022年12月四级真题第1套')">2022年12月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2022年12月四级真题第2套')">2022年12月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2022年12月四级真题第2套')">2022年12月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2022年12月四级真题第3套')">2022年12月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2022年12月四级真题第3套')">2022年12月四级真题第3套答案</div>
      </div>
    </div>
    
    <!-- 2021年四级真题 -->
    <div class="year-section">
      <h3>2021年四级真题及答案</h3>
      <div class="tests-grid">
        <div class="test-card" @click="showPaperDetail('2021年06月四级真题第1套')">2021年06月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2021年06月四级真题第1套')">2021年06月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2021年06月四级真题第2套')">2021年06月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2021年06月四级真题第2套')">2021年06月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2021年06月四级真题第3套')">2021年06月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2021年06月四级真题第3套')">2021年06月四级真题第3套答案</div>
        <div class="test-card" @click="showPaperDetail('2021年12月四级真题第1套')">2021年12月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2021年12月四级真题第1套')">2021年12月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2021年12月四级真题第2套')">2021年12月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2021年12月四级真题第2套')">2021年12月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2021年12月四级真题第3套')">2021年12月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2021年12月四级真题第3套')">2021年12月四级真题第3套答案</div>
      </div>
    </div>
    
    <!-- 2020年四级真题 -->
    <div class="year-section">
      <h3>2020年四级真题及答案</h3>
      <div class="tests-grid">
        <div class="test-card" @click="showPaperDetail('2020年06月四级真题第1套')">2020年06月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2020年06月四级真题第1套')">2020年06月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2020年06月四级真题第2套')">2020年06月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2020年06月四级真题第2套')">2020年06月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2020年06月四级真题第3套')">2020年06月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2020年06月四级真题第3套')">2020年06月四级真题第3套答案</div>
        <div class="test-card" @click="showPaperDetail('2020年12月四级真题第1套')">2020年12月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2020年12月四级真题第1套')">2020年12月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2020年12月四级真题第2套')">2020年12月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2020年12月四级真题第2套')">2020年12月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2020年12月四级真题第3套')">2020年12月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2020年12月四级真题第3套')">2020年12月四级真题第3套答案</div>
      </div>
    </div>
    
    <!-- 2019年四级真题 -->
    <div class="year-section">
      <h3>2019年四级真题及答案</h3>
      <div class="tests-grid">
        <div class="test-card" @click="showPaperDetail('2019年06月四级真题第1套')">2019年06月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2019年06月四级真题第1套')">2019年06月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2019年06月四级真题第2套')">2019年06月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2019年06月四级真题第2套')">2019年06月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2019年06月四级真题第3套')">2019年06月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2019年06月四级真题第3套')">2019年06月四级真题第3套答案</div>
        <div class="test-card" @click="showPaperDetail('2019年12月四级真题第1套')">2019年12月四级真题第1套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2019年12月四级真题第1套')">2019年12月四级真题第1套答案</div>
        <div class="test-card" @click="showPaperDetail('2019年12月四级真题第2套')">2019年12月四级真题第2套（含听力）</div>
        <div class="test-card" @click="showPaperDetail('2019年12月四级真题第2套')">2019年12月四级真题第2套答案</div>
        <div class="test-card" @click="showPaperDetail('2019年12月四级真题第3套')">2019年12月四级真题第3套</div>
        <div class="test-card" @click="showPaperDetail('2019年12月四级真题第3套')">2019年12月四级真题第3套答案</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { api } from '@/api';
import axios from 'axios';

// 状态变量
const showDetail = ref(false);
const currentPaperTitle = ref('');
const questions = ref<any[]>([]);
const loading = ref(false);
const showAnswer = ref(false);
const isSubmitted = ref(false);
const submissionStatus = ref('');
const submissionStatusText = ref('');
const isSubmitting = ref(false);
const currentHomeworkId = ref<number | null>(null);
const writingImage = ref<any>(null);
const writingImagePreview = ref('');
const translationImage = ref<any>(null);
const translationImagePreview = ref('');

// 显示真题详情
const showPaperDetail = async (title: string) => {
  console.log('showPaperDetail called with title:', title);
  currentPaperTitle.value = title;
  showDetail.value = true;
  loading.value = true;
  
  try {
    // 检查是否是2022年9月四级真题
    if (title === '2022年09月四级真题') {
      // 从本地JSON文件读取真题内容
      console.log('Loading 2022-09-cet4-complete.json');
      const response = await axios.get('/2022-09-cet4-complete.json');
      console.log('Response data:', response.data);
      questions.value = [response.data];
      console.log('Questions value:', questions.value);
      
      // 检查是否已提交
      await checkSubmissionStatus();
    } else {
      // 对于其他真题，使用模拟数据
      questions.value = [{
        id: 1,
        content: `这是${title}的内容，正在建设中...`,
        options: JSON.stringify(['选项A', '选项B', '选项C', '选项D']),
        correctAnswer: JSON.stringify('A')
      }];
      isSubmitted.value = false;
      submissionStatus.value = '';
      submissionStatusText.value = '';
    }
  } catch (error) {
    console.error('获取真题失败:', error);
  } finally {
    loading.value = false;
  }
};

// 检查作业提交状态
const checkSubmissionStatus = async () => {
  try {
    const homeworkId = 1; // 默认使用2022年9月四级真题的作业ID
    currentHomeworkId.value = homeworkId;
    const response = await api.cet4Homework.checkSubmitted(homeworkId);
    if (response.submitted) {
      isSubmitted.value = true;
      submissionStatus.value = response.status;
      if (response.status === 'graded') {
        submissionStatusText.value = `已评分: ${response.score}分`;
      } else if (response.status === 'submitted') {
        submissionStatusText.value = '待评分';
      }
    } else {
      isSubmitted.value = false;
      submissionStatus.value = '';
      submissionStatusText.value = '';
    }
  } catch (error) {
    console.error('检查提交状态失败:', error);
    isSubmitted.value = false;
  }
};



// 处理作文图片上传
const handleWritingFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    writingImage.value = target.files[0];
    writingImagePreview.value = URL.createObjectURL(target.files[0]);
  }
};

// 处理翻译图片上传
const handleTranslationFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    translationImage.value = target.files[0];
    translationImagePreview.value = URL.createObjectURL(target.files[0]);
  }
};

// 将文件转换为Base64
const fileToBase64 = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = reject;
  });
};

// 提交作业
const submitHomework = async () => {
  if (!currentHomeworkId.value) {
    alert('作业ID不存在');
    return;
  }

  isSubmitting.value = true;

  try {
    // 收集所有答案
    const studentAnswer = collectAllAnswers();

    // 构建提交数据
    const submissionData: any = {
      homeworkId: currentHomeworkId.value,
      studentAnswer: JSON.stringify(studentAnswer),
      paperContent: JSON.stringify(questions.value[0]) // 提交完整的试卷内容
    };

    // 发送图片数据
    if (writingImage.value) {
      submissionData.writingImage = await fileToBase64(writingImage.value);
    }
    if (translationImage.value) {
      submissionData.translationImage = await fileToBase64(translationImage.value);
    }

    // 提交到后端
    console.log('Submitting homework with data:', submissionData);
    const response = await api.cet4Homework.submit(submissionData);
    console.log('Response:', response);
    if (response.success) {
      alert('作业提交成功！');
      isSubmitted.value = true;
      submissionStatus.value = 'submitted';
      submissionStatusText.value = '待评分';
    } else {
      alert('提交失败: ' + response.message);
    }
  } catch (error) {
    console.error('提交作业失败:', error);
    alert('提交失败，请重试');
  } finally {
    isSubmitting.value = false;
  }
};

// 收集所有答案
const collectAllAnswers = () => {
  const answers: any = {};
  
  if (questions.value[0] && questions.value[0].sections) {
    questions.value[0].sections.forEach((section: any, sectionIndex: number) => {
      if (section.questions) {
        section.questions.forEach((question: any, qIndex: number) => {
          if (question.selectedAnswer !== undefined) {
            answers[`section_${sectionIndex}_${qIndex}`] = {
              selected: question.selectedAnswer,
              answer: question.answer
            };
          }
        });
      }
      
      if (section.sections) {
        section.sections.forEach((subSection: any, subSectionIndex: number) => {
          if (subSection.questions) {
            subSection.questions.forEach((question: any, qIndex: number) => {
              if (question.selectedAnswer !== undefined) {
                answers[`section_${sectionIndex}_subsection_${subSectionIndex}_${qIndex}`] = {
                  selected: question.selectedAnswer,
                  answer: question.answer
                };
              }
            });
          }
          
          if (subSection.passages) {
            subSection.passages.forEach((passage: any, passageIndex: number) => {
              if (passage.questions) {
                passage.questions.forEach((question: any, qIndex: number) => {
                  if (question.selectedAnswer !== undefined) {
                    answers[`section_${sectionIndex}_subsection_${subSectionIndex}_passage_${passageIndex}_${qIndex}`] = {
                      selected: question.selectedAnswer,
                      answer: question.answer
                    };
                  }
                });
              }
            });
          }
        });
      }
    });
  }
  
  return answers;
};

// 关闭详情弹窗
const closeDetail = () => {
  showDetail.value = false;
  questions.value = [];
  showAnswer.value = false;
};

// 切换答案显示
const toggleAnswer = () => {
  showAnswer.value = !showAnswer.value;
};

// 选择答案
const selectOption = (sectionIndex: number, qIndex: number, idx: number) => {
  if (questions.value[0] && questions.value[0].sections[sectionIndex]) {
    const section = questions.value[0].sections[sectionIndex];
    if (section.questions && section.questions[qIndex]) {
      section.questions[qIndex].selectedAnswer = idx;
    }
  }
};

// 选择阅读理解中的答案
const selectPassageOption = (sectionIndex: number, subSectionIndex: number, passageIndex: number, qIndex: number, idx: number) => {
  if (questions.value[0] && questions.value[0].sections[sectionIndex]) {
    const section = questions.value[0].sections[sectionIndex];
    if (section.sections && section.sections[subSectionIndex]) {
      const subSection = section.sections[subSectionIndex];
      if (subSection.passages && subSection.passages[passageIndex]) {
        const passage = subSection.passages[passageIndex];
        if (passage.questions && passage.questions[qIndex]) {
          passage.questions[qIndex].selectedAnswer = idx;
        }
      }
    }
  }
};

// 选择词汇银行中的词汇
const selectWordBankItem = (sectionIndex: number, idx: number) => {
  if (questions.value[0] && questions.value[0].sections[sectionIndex]) {
    const section = questions.value[0].sections[sectionIndex];
    console.log('Selected word bank item:', section.wordBank[idx]);
  }
};

// 页面加载时获取所有试卷
onMounted(async () => {
  try {
    // 暂时注释掉，因为api中没有定义获取试卷的方法
    // const response = await api.get('/api/papers');
    // console.log('所有试卷:', response);
  } catch (error) {
    console.error('获取试卷列表失败:', error);
  }
});
</script>

<style scoped>
.cet4-real-tests {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.cet4-real-tests h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}

.description {
  text-align: center;
  margin-bottom: 40px;
  color: #606266;
  line-height: 1.5;
}

.year-section {
  margin-bottom: 40px;
}

.year-section h3 {
  margin-bottom: 20px;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 10px;
}

.tests-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.test-card {
  background-color: #f9f9f9;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.test-card:hover {
  background-color: #ecf5ff;
  border-color: #d9ecff;
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 80%;
  max-width: 900px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.modal-header h3 {
  margin: 0;
  color: #303133;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #909399;
}

.modal-body {
  padding: 20px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #606266;
}

.question-item {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.question-content {
  margin-bottom: 10px;
  font-size: 16px;
  line-height: 1.5;
}

.question-options {
  margin-bottom: 10px;
}

.option-item {
  margin-bottom: 5px;
  padding: 10px 20px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.option-item:hover {
  background-color: #f5f7fa;
}

.option-item.selected {
  background-color: #ecf5ff;
  border-color: #d9ecff;
}

.option-item.correct {
  background-color: #f0f9ff;
  border-color: #e1f5fe;
  color: #0066cc;
}

.option-item.incorrect {
  background-color: #fff2f0;
  border-color: #ffccc7;
  color: #f56c6c;
}

.question-answer {
  margin-top: 10px;
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

.paper-title {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #e4e7ed;
}

.section-item {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.section-title {
  margin-bottom: 10px;
  font-weight: bold;
}

.section-direction {
  margin-bottom: 15px;
  font-style: italic;
  color: #606266;
}

.section-requirements {
  margin-bottom: 15px;
}

.section-requirements ul {
  padding-left: 20px;
}

.section-requirements li {
  margin-bottom: 5px;
}

.section-content {
  margin-bottom: 15px;
  line-height: 1.5;
}

.section-word-bank {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.section-word-bank h6 {
  margin-bottom: 10px;
  font-weight: bold;
  color: #303133;
}

.word-bank-items {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.word-bank-item {
  padding: 5px 10px;
  background-color: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.word-bank-item:hover {
  background-color: #d9ecff;
  transform: translateY(-1px);
}

.section-passage {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
  line-height: 1.6;
}

.sub-section-passage {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
  line-height: 1.6;
}

.audio-player {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.audio-player audio {
  width: 100%;
  max-width: 500px;
  border-radius: 4px;
}

.question-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
  margin-bottom: 15px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.option-item:hover {
  background-color: #f5f7fa;
}

.radio-button {
  width: 20px;
  height: 20px;
  border: 2px solid #dcdfe6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.3s;
}

.radio-inner {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  transition: all 0.3s;
}

.radio-inner.selected {
  background-color: #409eff;
}

.radio-inner.correct {
  background-color: #67c23a;
}

.radio-inner.incorrect {
  background-color: #f56c6c;
}

.option-text {
  flex: 1;
  font-size: 14px;
  line-height: 1.5;
}

.matching-options {
  margin-top: 10px;
  margin-bottom: 15px;
}

.matching-input {
  width: 100px;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s;
}

.matching-input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

@media (max-width: 768px) {
  /* No specific changes needed for flex layout */
}

.sub-sections {
  margin-top: 15px;
  margin-left: 20px;
}

.sub-section-item {
  margin-bottom: 20px;
}

.sub-section-title {
  margin-bottom: 10px;
  font-weight: bold;
}

.sub-section-directions {
  margin-bottom: 15px;
  font-style: italic;
  color: #606266;
}

.passages {
  margin-top: 15px;
}

.passage-item {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.passage-content {
  margin-bottom: 15px;
  line-height: 1.5;
}

.passage-questions-info {
  margin-bottom: 15px;
  font-weight: bold;
  color: #303133;
}

.passage-questions {
  margin-top: 15px;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  text-align: right;
}

.btn {
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn:hover {
  background-color: #66b1ff;
}

/* 试卷标题样式 */
.paper-title {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e4e7ed;
}

.paper-title h2 {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.paper-title p {
  color: #606266;
  line-height: 1.5;
}

/* 听力播放器样式 */
.audio-player {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.audio-player audio {
  width: 100%;
  max-width: 600px;
}

/* 下载按钮样式 */
.download-button {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.btn-download {
  padding: 10px 20px;
  background-color: #67c23a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 16px;
}

.btn-download:hover {
  background-color: #85ce61;
}

/* 真题内容样式 */
.paper-content {
  margin-top: 30px;
}

.paper-content h3 {
  text-align: center;
  margin-bottom: 30px;
  font-size: 20px;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .tests-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .modal-content {
    width: 90%;
  }
}

@media (max-width: 768px) {
  .tests-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
  }
}

/* 提交按钮样式 */
.btn-submit {
  background-color: #67c23a;
  color: white;
  padding: 10px 20px;
  font-size: 16px;
}

.btn-submit:hover {
  background-color: #85ce61;
}

.btn-submit:disabled {
  background-color: #c0c4cc;
  cursor: not-allowed;
}

/* 提交状态样式 */
.submission-status {
  display: flex;
  align-items: center;
  gap: 15px;
}

.status-badge {
  padding: 5px 15px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: bold;
}

.status-badge.submitted {
  background-color: #e6a23c;
  color: white;
}

.status-badge.graded {
  background-color: #67c23a;
  color: white;
}

/* 主观题提交弹窗样式 */
.submit-dialog {
  max-width: 500px;
}

.submit-section {
  margin-bottom: 20px;
}

.submit-section h4 {
  margin-bottom: 10px;
  color: #303133;
}

.submit-hint {
  color: #606266;
  font-size: 14px;
  margin-bottom: 15px;
}

.upload-area {
  margin-bottom: 15px;
}

.upload-area input[type="file"] {
  display: none;
}

.upload-label {
  display: inline-block;
  padding: 20px 40px;
  background-color: #f5f7fa;
  border: 2px dashed #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.upload-label:hover {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.preview-area {
  margin-top: 15px;
}

.image-preview {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

/* 主观题提交区域样式 */
.subjective-submit-area {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.subjective-submit-area h5 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.upload-area-inline {
  margin-bottom: 10px;
}

.upload-area-inline input[type="file"] {
  display: none;
}

.upload-label-inline {
  display: inline-block;
  padding: 10px 20px;
  background-color: #ecf5ff;
  border: 1px dashed #409eff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  color: #409eff;
}

.upload-label-inline:hover {
  background-color: #d9ecff;
}

.preview-inline {
  margin-top: 10px;
}

.image-preview-inline {
  max-width: 100%;
  max-height: 200px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

/* 写作和翻译区域样式 */
.section-writing,
.section-translation {
  margin: 20px 0;
}

.writing-question {
  margin-bottom: 15px;
}

.translation-content {
  margin-bottom: 20px;
}
</style>