
<script setup>
import { ref } from 'vue'
import { startInterview, submitAnswer } from '../api'

const resumeId = ref(1)
const jobId = ref(1)
const sessionId = ref(null)
const questionNumber = ref(0)
const question = ref('')
const answer = ref('')
const feedback = ref('')
const completed = ref(false)
const loading = ref(false)
const error = ref('')
const history = ref([])

async function beginInterview() {
  if (loading.value) return

  loading.value = true
  error.value = ''

  try {
    const result = await startInterview(
        Number(resumeId.value),
        Number(jobId.value)
    )

    sessionId.value = result.sessionId
    questionNumber.value = result.questionNumber
    question.value = result.question
    answer.value = ''
    feedback.value = ''
    completed.value = false
    history.value = []
  } catch (e) {
    error.value = e.response?.data?.message
        || e.message
        || '开始面试失败'
  } finally {
    loading.value = false
  }
}

async function sendAnswer() {
  const text = answer.value.trim()
  if (!text || loading.value || completed.value) return

  loading.value = true
  error.value = ''

  try {
    const currentQuestion = question.value
    const currentNumber = questionNumber.value

    const result = await submitAnswer(sessionId.value, text)

    history.value.push({
      number: currentNumber,
      question: currentQuestion,
      answer: text,
      feedback: result.feedback
    })

    feedback.value = result.feedback || ''
    answer.value = ''

    completed.value = result.status === 'COMPLETED'
    question.value = result.nextQuestion || ''

    if (!completed.value) {
      questionNumber.value = result.questionNumber
          || currentNumber + 1
    }
  } catch (e) {
    error.value = e.response?.data?.message
        || e.message
        || '提交回答失败'
  } finally {
    loading.value = false
  }
}

function resetInterview() {
  sessionId.value = null
  questionNumber.value = 0
  question.value = ''
  answer.value = ''
  feedback.value = ''
  completed.value = false
  history.value = []
  error.value = ''
}
</script>

<template>
  <div class="interview-page">
    <h1>AI 模拟面试</h1>
    <p class="subtitle">
      基于 Qwen3 4B 的个性化 Java 技术面试
    </p>

    <el-alert
        v-if="error"
        :title="error"
        type="error"
        show-icon
        class="error-alert"
    />

    <el-card v-if="sessionId === null" shadow="never">
      <h3>开始一场模拟面试</h3>
      <p class="hint">请选择已有简历和目标岗位</p>

      <el-form label-width="100px">
        <el-form-item label="简历 ID">
          <el-input-number v-model="resumeId" :min="1" />
        </el-form-item>

        <el-form-item label="岗位 ID">
          <el-input-number v-model="jobId" :min="1" />
        </el-form-item>
      </el-form>

      <el-button
          type="primary"
          size="large"
          :loading="loading"
          @click="beginInterview"
      >
        开始 AI 面试
      </el-button>
    </el-card>

    <template v-else>
      <el-card shadow="never" class="progress-card">
        <div class="progress-heading">
          <span>面试进度</span>
          <el-tag :type="completed ? 'success' : 'primary'">
            {{ completed ? '已完成' : '进行中' }}
          </el-tag>
        </div>

        <el-progress
            :percentage="completed ? 100 : Math.round((questionNumber - 1) / 3 * 100)"
            :stroke-width="10"
        />

        <p class="hint">共 3 道技术面试题</p>
      </el-card>

      <el-card v-if="!completed" shadow="never" class="question-card">
        <el-tag type="primary">
          第 {{ questionNumber }} / 3 题
        </el-tag>

        <h2>{{ question }}</h2>

        <el-input
            v-model="answer"
            type="textarea"
            :rows="7"
            maxlength="4000"
            show-word-limit
            placeholder="请在这里输入你的面试回答……"
        />

        <div class="actions">
          <el-button
              type="primary"
              :loading="loading"
              :disabled="!answer.trim()"
              @click="sendAnswer"
          >
            提交回答并获取点评
          </el-button>
        </div>
      </el-card>

      <el-card
          v-if="feedback"
          shadow="never"
          class="feedback-card"
      >
        <h3>AI 面试点评</h3>
        <p>{{ feedback }}</p>
      </el-card>

      <el-card
          v-if="history.length > 0"
          shadow="never"
          class="history-card"
      >
        <h3>面试记录</h3>

        <el-collapse>
          <el-collapse-item
              v-for="item in history"
              :key="item.number"
              :title="`第 ${item.number} 题：${item.question}`"
              :name="item.number"
          >
            <h4>我的回答</h4>
            <p>{{ item.answer }}</p>
            <h4>AI 点评</h4>
            <p>{{ item.feedback }}</p>
          </el-collapse-item>
        </el-collapse>
      </el-card>

      <el-card v-if="completed" shadow="never">
        <el-result
            icon="success"
            title="模拟面试完成"
            sub-title="三轮面试已结束，回答与点评已提交给后端保存"
        >
          <template #extra>
            <el-button
                type="primary"
                @click="resetInterview"
            >
              开始新的面试
            </el-button>
          </template>
        </el-result>
      </el-card>
    </template>
  </div>
</template>

<style scoped>
.interview-page {
  max-width: 1000px;
  margin: 0 auto;
}

h1 {
  font-size: 26px;
  margin: 0 0 8px;
  color: #243550;
}

.subtitle,
.hint {
  color: #8b95a6;
}

.subtitle {
  margin-bottom: 24px;
}

.el-card {
  margin-bottom: 20px;
  border-radius: 14px;
}

.progress-heading {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  font-weight: 600;
}

.question-card h2 {
  font-size: 21px;
  font-weight: 600;
  line-height: 1.8;
  margin: 22px 0;
  color: #263b57;
}

.actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.feedback-card {
  background: #f5f9ff;
}

.feedback-card p,
.history-card p {
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  line-height: 1.9;
}

.error-alert {
  margin-bottom: 20px;
}
</style>