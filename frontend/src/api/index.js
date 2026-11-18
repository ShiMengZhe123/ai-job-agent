
import axios from 'axios'

const request = axios.create({
    baseURL: '/api',
    timeout: 180000
})

request.interceptors.response.use(
    response => {
        const result = response.data

        if (result.code !== 200) {
            return Promise.reject(
                new Error(result.message || '请求失败')
            )
        }

        return result.data
    },
    error => Promise.reject(error)
)

export function chatWithAgent(message) {
    return request.post('/agent/chat', { message })
}

export function startInterview(resumeId, jobId) {
    return request.post('/interviews/start', {
        resumeId,
        jobId
    })
}

export function submitAnswer(sessionId, answer) {
    return request.post('/interviews/answer', {
        sessionId,
        answer
    })
}

/* ===== 简历管理 ===== */

// 获取简历列表
export function getResumes() {
    return request.get('/resumes')
}

// 获取简历详情
export function getResume(id) {
    return request.get(`/resumes/${id}`)
}

// 上传 PDF / DOCX 简历
export function uploadResume(file) {
    const formData = new FormData()
    formData.append('file', file)

    return request.post('/resumes/upload', formData, {
        timeout: 180000
    })
}

// 删除简历
export function deleteResume(id) {
    return request.delete(`/resumes/${id}`)
}

/* ===== 岗位管理 ===== */

export function getJobs() {
    return request.get('/jobs')
}

export function createJob(data) {
    return request.post('/jobs', data)
}

export function updateJob(id, data) {
    return request.put(`/jobs/${id}`, data)
}

export function deleteJob(id) {
    return request.delete(`/jobs/${id}`)
}

/* ===== 岗位匹配 ===== */

export function matchResumeJob(resumeId, jobId) {
    return request.post('/matches', {
        resumeId,
        jobId
    })
}

export function getMatchHistory(resumeId) {
    return request.get(`/matches/resume/${resumeId}`)
}