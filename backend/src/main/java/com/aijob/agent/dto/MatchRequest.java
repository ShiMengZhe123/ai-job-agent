
package com.aijob.agent.dto;

/**
 * 简历与岗位匹配请求。
 */
public class MatchRequest {

    /**
     * 简历数据库 ID
     */
    private Long resumeId;

    /**
     * 岗位数据库 ID
     */
    private Long jobId;

    public MatchRequest() {
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }
}