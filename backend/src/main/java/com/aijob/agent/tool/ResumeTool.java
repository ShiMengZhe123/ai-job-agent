
package com.aijob.agent.tool;

import com.aijob.agent.entity.Resume;
import com.aijob.agent.repository.ResumeRepository;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Agent 简历查询工具。
 */
@Component
public class ResumeTool {

    private final ResumeRepository resumeRepository;

    public ResumeTool(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    @Tool(
            name = "get_resume",
            description = "根据数据库简历ID查询候选人的简历信息，包括文件名、技能和简历文本。"
    )
    public Map<String, Object> getResume(
            @ToolParam(description = "简历数据库ID")
            Long resumeId) {

        Map<String, Object> result = new LinkedHashMap<>();

        if (resumeId == null || resumeId <= 0) {
            result.put("error", "简历ID不合法");
            return result;
        }

        Resume resume = resumeRepository
                .findById(resumeId)
                .orElse(null);

        if (resume == null) {
            result.put("error", "简历不存在");
            return result;
        }

        result.put("id", resume.getId());
        result.put("fileName", resume.getFileName());

        String content = resume.getContent();

        if (content != null && content.length() > 8000) {
            content = content.substring(0, 8000);
            result.put("truncated", true);
        }

        result.put("content",
                content == null ? "" : content);

        result.put("skills",
                resume.getSkills() == null
                        ? ""
                        : resume.getSkills());

        return result;
    }
}