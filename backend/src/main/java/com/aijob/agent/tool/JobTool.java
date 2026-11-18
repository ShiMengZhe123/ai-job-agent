
package com.aijob.agent.tool;

import com.aijob.agent.entity.Job;
import com.aijob.agent.repository.JobRepository;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Agent 招聘岗位查询工具。
 */
@Component
public class JobTool {

    private final JobRepository jobRepository;

    public JobTool(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Tool(
            name = "get_job",
            description = "根据岗位数据库ID查询真实的招聘岗位名称、公司、岗位描述和技能要求。"
    )
    public Map<String, Object> getJob(
            @ToolParam(description = "岗位数据库ID")
            Long jobId) {

        Map<String, Object> result = new LinkedHashMap<>();

        if (jobId == null || jobId <= 0) {
            result.put("error", "岗位ID不合法");
            return result;
        }

        Job job = jobRepository
                .findById(jobId)
                .orElse(null);

        if (job == null) {
            result.put("error", "岗位不存在");
            return result;
        }

        result.put("id", job.getId());
        result.put("title", job.getTitle());
        result.put("company", job.getCompany());
        result.put("location", job.getLocation());
        result.put("description", job.getDescription());
        result.put("requirements", job.getRequirements());

        return result;
    }
}