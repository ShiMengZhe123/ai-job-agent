
package com.aijob.agent.tool;

import com.aijob.agent.dto.MatchRequest;
import com.aijob.agent.dto.MatchResponse;
import com.aijob.agent.service.MatchService;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Agent 岗位匹配工具。
 */
@Component
public class MatchTool {

    private final MatchService matchService;

    public MatchTool(MatchService matchService) {
        this.matchService = matchService;
    }

    @Tool(
            name = "match_resume_job",
            description = "当用户明确要求执行简历与岗位匹配时，根据简历ID和岗位ID计算规则匹配分数、匹配技能与缺失技能，并将匹配结果保存到数据库。"
    )
    public MatchResponse matchResumeJob(
            @ToolParam(description = "简历数据库ID")
            Long resumeId,

            @ToolParam(description = "岗位数据库ID")
            Long jobId) {

        MatchRequest request = new MatchRequest();

        request.setResumeId(resumeId);
        request.setJobId(jobId);

        return matchService.match(request);
    }
}