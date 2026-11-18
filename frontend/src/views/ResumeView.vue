
<script setup>
import { onMounted, ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getResumes,
  uploadResume,
  deleteResume
} from '../api'

const resumes = ref([])
const loading = ref(false)
const uploading = ref(false)
const selected = ref(null)
const previewVisible = ref(false)
const keyword = ref('')

const filteredResumes = computed(() => {
  const text = keyword.value.trim().toLowerCase()
  if (!text) return resumes.value

  return resumes.value.filter(item =>
      (item.fileName || '').toLowerCase().includes(text)
      || String(item.id).includes(text)
  )
})

async function loadResumes() {
  loading.value = true

  try {
    resumes.value = await getResumes() || []
  } catch (e) {
    ElMessage.error(
        e.response?.data?.message || e.message || '加载简历失败'
    )
  } finally {
    loading.value = false
  }
}

async function handleUpload(options) {
  const file = options.file
  const lower = file.name.toLowerCase()

  if (!lower.endsWith('.pdf') && !lower.endsWith('.docx')) {
    ElMessage.error('仅支持 PDF 和 DOCX 文件')
    return
  }

  uploading.value = true

  try {
    const result = await uploadResume(file)
    ElMessage.success(`简历上传成功，ID：${result.id}`)
    await loadResumes()
  } catch (e) {
    ElMessage.error(
        e.response?.data?.message || e.message || '上传失败'
    )
  } finally {
    uploading.value = false
  }
}

function showResume(row) {
  selected.value = row
  previewVisible.value = true
}

async function removeResume(row) {
  try {
    await ElMessageBox.confirm(
        `确定删除简历「${row.fileName}」吗？`,
        '删除确认',
        { type: 'warning' }
    )

    await deleteResume(row.id)
    ElMessage.success('删除成功')
    await loadResumes()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') {
      ElMessage.error(
          e.response?.data?.message || e.message || '删除失败'
      )
    }
  }
}

onMounted(loadResumes)
</script>

<template>
  <div class="page">
    <div class="heading">
      <div>
        <h1>简历管理</h1>
        <p>上传简历、解析文本、查看已有简历</p>
      </div>

      <el-upload
          :show-file-list="false"
          :http-request="handleUpload"
          accept=".pdf,.docx"
      >
        <el-button type="primary" :loading="uploading">
          上传简历
        </el-button>
      </el-upload>
    </div>

    <el-row :gutter="16" class="stats">
      <el-col :span="12">
        <el-card shadow="never">
          <el-statistic title="已上传简历" :value="resumes.length" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <el-statistic
              title="当前列表"
              :value="filteredResumes.length"
          />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <div class="toolbar">
        <el-input
            v-model="keyword"
            placeholder="搜索简历名称或 ID"
            clearable
            style="max-width: 300px"
        />
        <el-button @click="loadResumes">刷新</el-button>
      </div>

      <el-table
          v-loading="loading"
          :data="filteredResumes"
          stripe
          style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column
            prop="fileName"
            label="简历文件名"
            min-width="240"
            show-overflow-tooltip
        />

        <el-table-column label="文件格式" width="110">
          <template #default="{ row }">
            <el-tag>{{ row.fileType?.toUpperCase() }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column
            prop="createdAt"
            label="上传时间"
            min-width="190"
        />

        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="showResume(row)">
              查看
            </el-button>

            <el-button link type="danger" @click="removeResume(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
        v-model="previewVisible"
        title="简历解析结果"
        width="75%"
    >
      <template v-if="selected">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="简历 ID">
            {{ selected.id }}
          </el-descriptions-item>

          <el-descriptions-item label="文件名">
            {{ selected.fileName }}
          </el-descriptions-item>
        </el-descriptions>

        <h3>简历内容</h3>

        <div class="preview">
          {{ selected.content || '暂无解析内容' }}
        </div>
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

h1 {
  margin: 0 0 8px;
  font-size: 26px;
  color: #243550;
}

.heading p {
  color: #8490a2;
  margin: 0;
}

.stats {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  margin-bottom: 18px;
}

.preview {
  background: #f7f8fa;
  border-radius: 10px;
  padding: 20px;
  margin-top: 12px;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  max-height: 55vh;
  overflow-y: auto;
  line-height: 1.8;
}
</style>