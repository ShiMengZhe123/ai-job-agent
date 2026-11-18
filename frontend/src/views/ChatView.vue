
<script setup>
import { ref, nextTick } from 'vue'
import { chatWithAgent } from '../api'

const input = ref('')
const messages = ref([])
const loading = ref(false)
const error = ref('')
const chatBox = ref(null)

async function scrollBottom() {
  await nextTick()
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }
}

async function sendMessage() {
  const text = input.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  input.value = ''
  error.value = ''
  loading.value = true
  await scrollBottom()

  try {
    const result = await chatWithAgent(text)
    messages.value.push({
      role: 'assistant',
      content: result.message || 'AI 没有返回内容'
    })
  } catch (e) {
    error.value = e.response?.data?.message
        || e.message
        || 'AI 请求失败'
  } finally {
    loading.value = false
    await scrollBottom()
  }
}

function clearMessages() {
  messages.value = []
  error.value = ''
}
</script>

<template>
  <div class="chat-page">
    <div class="page-heading">
      <div>
        <h1>AI 智能求职助手</h1>
        <p>简历分析 · 岗位查询 · 智能匹配</p>
      </div>
      <el-button @click="clearMessages">清空对话</el-button>
    </div>

    <el-card shadow="never" class="chat-card">
      <div ref="chatBox" class="messages">
        <el-empty
            v-if="messages.length === 0"
            description="你好！我是你的 AI 求职助手"
        >
          <el-button
              type="primary"
              plain
              @click="input = '请查询1号岗位的技能要求'"
          >
            查询岗位
          </el-button>
        </el-empty>

        <div
            v-for="(item, index) in messages"
            :key="index"
            :class="['message', item.role]"
        >
          <strong>
            {{ item.role === 'user' ? '我' : 'Qwen3 助手' }}
          </strong>
          <p>{{ item.content }}</p>
        </div>

        <div v-if="loading" class="thinking">
          Qwen3 正在思考……
        </div>
      </div>

      <el-alert
          v-if="error"
          :title="error"
          type="error"
          show-icon
          class="error-message"
      />

      <el-input
          v-model="input"
          type="textarea"
          :rows="3"
          maxlength="3000"
          show-word-limit
          placeholder="例如：请查询1号简历，并分析适合的岗位"
          @keydown.ctrl.enter.prevent="sendMessage"
      />

      <div class="footer">
        <span>Ctrl + Enter 发送</span>
        <el-button
            type="primary"
            :loading="loading"
            :disabled="!input.trim()"
            @click="sendMessage"
        >
          发送消息
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.chat-page {
  max-width: 1100px;
  margin: 0 auto;
}

.page-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

h1 {
  margin: 0 0 8px;
  font-size: 26px;
  color: #243550;
}

.page-heading p {
  margin: 0;
  color: #8792a4;
}

.chat-card {
  border-radius: 14px;
}

.messages {
  height: 480px;
  overflow-y: auto;
  padding: 20px 8px;
}

.message {
  padding: 16px 20px;
  margin-bottom: 18px;
  border-radius: 12px;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  line-height: 1.8;
}

.message p {
  margin-bottom: 0;
}

.message.user {
  background: #e7efff;
  margin-left: 15%;
}

.message.assistant {
  background: #f1f3f7;
  margin-right: 15%;
}

.thinking {
  padding: 12px;
  color: #8190a6;
}

.error-message {
  margin-bottom: 14px;
}

.footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  color: #97a1b1;
  font-size: 13px;
}
</style>