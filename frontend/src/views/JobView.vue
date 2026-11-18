
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getJobs,
  getResumes,
  createJob,
  matchResumeJob,
  getMatchHistory
} from '../api'

const jobs = ref([])
const resumes = ref([])
const selectedResumeId = ref(null)
const selectedJobId = ref(null)

const loading = ref(false)
const matching = ref(false)
const matchResult = ref(null)
const history = ref([])

const addDialog = ref(false)

const jobForm = ref({
  title: '',
  company: '',
  location: '',
  description: '',
  requirements: ''
})

async function loadData() {
  loading.value = true

  try {
    const [jobData, resumeData] = await Promise.all([
      getJobs(),
      getResumes()
    ])

    jobs.value = jobData || []
    resumes.value = resumeData || []

    if (!selectedResumeId.value && resumes.value.length) {
      selectedResumeId.value = resumes.value[0].id
    }
  } catch (e) {
    ElMessage.error(
        e.response?.data?.message || e.message || '数据加载失败'
    )
  } finally {
    loading.value = false
  }
}

async function saveJob() {
  if (!jobForm.value.title.trim()) {
    ElMessage.warning('请输入岗位名称')
    return
  }

  try {
    await createJob(jobForm.value)
    ElMessage.success('岗位新增成功')
    addDialog.value = false

    jobForm.value = {
      title: '',
      company: '',
      location: '',
      description: '',
      requirements: ''
    }

    await loadData()
  } catch (e) {
    ElMessage.error(
        e.response?.data?.message || e.message || '新增失败'
    )
  }
}

function selectJob(row) {
  selectedJobId.value = row.id
  matchResult.value = null
}

async function executeMatch() {
  if (!selectedResumeId.value || !selectedJobId.value) {
    ElMessage.warning('请先选择简历和岗位')
    return
  }

  matching.value = true
  matchResult.value = null

  try {
    const result = await matchResumeJob(
        selectedResumeId.value,
        selectedJobId.value
    )

    matchResult.value = result

    ElMessage.success('匹配计算完成')
    await loadHistory()
  } catch (e) {
    ElMessage.error(
        e.response?.data?.message || e.message || '匹配失败'
    )
  } finally {
    matching.value = false
  }
}

async function loadHistory() {
  if (!selectedResumeId.value) {
    history.value = []
    return
  }

  try {
    history.value = await getMatchHistory(
        selectedResumeId.value
    ) || []
  } catch (e) {
    history.value = []
    ElMessage.error(
        e.response?.data?.message || e.message || '加载历史失败'
    )
  }
}

onMounted(loadData)
</script>

<template>
  <div class="page">
    <div class="heading">
      <div>
        <h1>岗位管理与智能匹配</h1>
        <p>选择真实简历和目标岗位，计算关键词匹配分数</p>
      </div>

      <el-button type="primary" @click="addDialog = true">
        新增岗位
      </el-button>
    </div>

    <el-card shadow="never" class="section">
      <h3>第一步：选择简历</h3>

      <el-select
          v-model="selectedResumeId"
          placeholder="请选择简历"
          style="width: 100%; max-width: 420px"
          @change="loadHistory"
      >
        <el-option
            v-for="resume in resumes"
            :key="resume.id"
            :label="`${resume.id} - ${resume.fileName}`"
            :value="resume.id"
        />
      </el-select>
    </el-card>

    <el-card shadow="never" class="section">
      <div class="toolbar">
        <h3>第二步：选择目标岗位</h3>
        <el-button @click="loadData">刷新岗位</el-button>
      </div>

      <el-table
          v-loading="loading"
          :data="jobs"
          highlight-current-row
          @current-change="row => row && selectJob(row)"
      >
        <el-table-column prop="id" label="ID" width="70" />

        <el-table-column
            prop="title"
            label="岗位名称"
            min-width="180"
        />

        <el-table-column
            prop="company"
            label="公司"
            min-width="130"
        />

        <el-table-column
            prop="location"
            label="城市"
            width="110"
        />

        <el-table-column
            prop="requirements"
            label="技术要求"
            min-width="260"
            show-overflow-tooltip
        />
      </el-table>

      <p class="hint">
        当前选中岗位 ID：{{ selectedJobId ?? '未选择' }}
      </p>

      <el-button
          type="primary"
          size="large"
          :disabled="!selectedResumeId || !selectedJobId"
          :loading="matching"
          @click="executeMatch"
      >
        开始智能匹配
      </el-button>
    </el-card>

    <el-card
        v-if="matchResult"
        shadow="never"
        class="section"
    >
      <h3>岗位匹配结果</h3>

      <div class="score">
        <el-progress
            type="dashboard"
            :percentage="Math.max(0, Math.min(100, matchResult.score ?? 0))"
            :width="170"
            :stroke-width="12"
        />
        <div>
          <h2>{{ matchResult.jobTitle }}</h2>
          <p>匹配分数基于关键词覆盖率，不代表录用概率。</p>
        </div>
      </div>

      <h4>已匹配技能</h4>

      <el-space wrap>
        <el-tag
            v-for="skill in matchResult.matchedSkills || []"
            :key="skill"
            type="success"
        >
          {{ skill }}
        </el-tag>
        <span v-if="!matchResult.matchedSkills?.length">暂无</span>
      </el-space>

      <h4>缺失技能</h4>

      <el-space wrap>
        <el-tag
            v-for="skill in matchResult.missingSkills || []"
            :key="skill"
            type="warning"
        >
          {{ skill }}
        </el-tag>
        <span v-if="!matchResult.missingSkills?.length">
          无缺失关键词技能
        </span>
      </el-space>

      <h4>匹配分析</h4>
      <p class="analysis">{{ matchResult.analysis }}</p>
    </el-card>

    <el-card shadow="never" class="section">
      <div class="toolbar">
        <h3>历史匹配记录</h3>
        <el-button @click="loadHistory">查询历史</el-button>
      </div>

      <el-table :data="history" stripe>
        <el-table-column prop="id" label="记录 ID" width="100" />
        <el-table-column prop="jobTitle" label="岗位" />
        <el-table-column prop="score" label="分数" width="100" />
        <el-table-column prop="createdAt" label="匹配时间" />
      </el-table>
    </el-card>

    <el-dialog v-model="addDialog" title="新增岗位" width="600px">
      <el-form :model="jobForm" label-width="90px">
        <el-form-item label="岗位名称">
          <el-input v-model="jobForm.title" maxlength="150" />
        </el-form-item>
        <el-form-item label="公司">
          <el-input v-model="jobForm.company" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="jobForm.location" />
        </el-form-item>
        <el-form-item label="岗位描述">
          <el-input
              v-model="jobForm.description"
              type="textarea"
              :rows="3"
          />
        </el-form-item>
        <el-form-item label="技能要求">
          <el-input
              v-model="jobForm.requirements"
              type="textarea"
              :rows="3"
              placeholder="例如：Java, Spring Boot, MySQL, Git"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addDialog = false">取消</el-button>
        <el-button type="primary" @click="saveJob">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  max-width: 1200px;
  margin: 0 auto;
}

.heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.heading h1 {
  margin: 0 0 8px;
  font-size: 26px;
  color: #243550;
}

.heading p, .hint {
  color: #8490a2;
}

.section {
  margin-bottom: 20px;
  border-radius: 14px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.toolbar h3 {
  margin: 0;
}

.score {
  display: flex;
  align-items: center;
  gap: 28px;
  margin: 24px 0;
}

.analysis {
  white-space: pre-wrap;
  line-height: 1.8;
  overflow-wrap: anywhere;
}

h4 {
  margin-top: 24px;
}

.hint {
  margin-top: 15px;
}
</style>