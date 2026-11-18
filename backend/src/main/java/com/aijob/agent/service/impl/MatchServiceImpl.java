
package com.aijob.agent.service.impl;

import com.aijob.agent.common.ErrorCode;
import com.aijob.agent.dto.MatchRequest;
import com.aijob.agent.dto.MatchResponse;
import com.aijob.agent.entity.Job;
import com.aijob.agent.entity.MatchRecord;
import com.aijob.agent.entity.Resume;
import com.aijob.agent.exception.BusinessException;
import com.aijob.agent.repository.JobRepository;
import com.aijob.agent.repository.MatchRecordRepository;
import com.aijob.agent.repository.ResumeRepository;
import com.aijob.agent.service.MatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 简历与岗位规则匹配服务。
 *
 * 1. 查询简历和岗位
 * 2. 提取岗位技能关键词
 * 3. 检查简历内容
 * 4. 计算匹配分数
 * 5. 生成分析结果
 * 6. 保存匹配历史
 */
@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger log =
            LoggerFactory.getLogger(MatchServiceImpl.class);

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final MatchRecordRepository matchRecordRepository;

    public MatchServiceImpl(
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            MatchRecordRepository matchRecordRepository) {

        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.matchRecordRepository = matchRecordRepository;
    }

    /**
     * 执行新的匹配
     */
    @Override
    @Transactional
    public MatchResponse match(MatchRequest request) {

        // 1. 检查请求参数
        if (request == null
                || request.getResumeId() == null
                || request.getJobId() == null
                || request.getResumeId() <= 0
                || request.getJobId() <= 0) {

            throw new BusinessException(
                    ErrorCode.PARAM_ERROR
            );
        }

        // 2. 查询简历
        Resume resume = resumeRepository
                .findById(request.getResumeId())
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESUME_NOT_FOUND
                        )
                );

        // 3. 查询岗位
        Job job = jobRepository
                .findById(request.getJobId())
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.JOB_NOT_FOUND
                        )
                );

        String resumeContent = resume.getContent();
        String jobRequirements = job.getRequirements();

        if (resumeContent == null
                || resumeContent.isBlank()) {

            throw new BusinessException(
                    ErrorCode.MATCH_ERROR
            );
        }

        if (jobRequirements == null
                || jobRequirements.isBlank()) {

            throw new BusinessException(
                    4001,
                    "该岗位没有填写技能要求，无法计算匹配分数"
            );
        }

        // 4. 提取岗位技能
        List<String> requiredSkills =
                extractRequiredSkills(jobRequirements);

        if (requiredSkills.isEmpty()) {
            throw new BusinessException(
                    4001,
                    "无法提取有效的岗位技能要求"
            );
        }

        // 5. 比对简历内容
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : requiredSkills) {

            if (containsSkill(resumeContent, skill)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        // 6. 计算 0-100 分
        int score = (int) Math.round(
                matchedSkills.size() * 100.0
                        / requiredSkills.size()
        );

        // 7. 生成分析文字
        String analysis = buildAnalysis(
                score,
                matchedSkills,
                missingSkills
        );

        // 8. 保存历史记录
        MatchRecord record = new MatchRecord();

        record.setResumeId(resume.getId());
        record.setJobId(job.getId());
        record.setScore(score);
        record.setAnalysis(analysis);

        MatchRecord saved =
                matchRecordRepository.saveAndFlush(record);

        log.info(
                "岗位匹配成功，记录ID={}，简历ID={}，岗位ID={}，得分={}",
                saved.getId(),
                resume.getId(),
                job.getId(),
                score
        );

        // 9. 构建响应
        MatchResponse response = new MatchResponse();

        response.setId(saved.getId());
        response.setResumeId(resume.getId());
        response.setJobId(job.getId());
        response.setJobTitle(job.getTitle());
        response.setScore(score);
        response.setMatchedSkills(matchedSkills);
        response.setMissingSkills(missingSkills);
        response.setAnalysis(analysis);
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    /**
     * 从岗位 requirements 中提取技能。
     *
     * 支持：
     * 英文逗号、中文逗号、分号、换行
     *
     * 去除重复关键词，最多处理50项。
     */
    private List<String> extractRequiredSkills(String text) {

        Map<String, String> uniqueSkills =
                new LinkedHashMap<>();

        String[] parts = text.split("[,，;；\\r\\n]+");

        for (String part : parts) {

            String skill = part.trim()
                    .replaceAll("\\s+", " ");

            if (skill.isBlank()) {
                continue;
            }

            String key = skill.toLowerCase(Locale.ROOT);

            uniqueSkills.putIfAbsent(key, skill);

            if (uniqueSkills.size() >= 50) {
                break;
            }
        }

        return new ArrayList<>(uniqueSkills.values());
    }

    /**
     * 判断简历是否包含某个技能关键词。
     *
     * 对英文字母和数字添加边界检查，
     * 避免将 JavaScript 误匹配为 Java。
     */
    private boolean containsSkill(
            String content,
            String skill) {

        if (content == null || skill == null) {
            return false;
        }

        String normalizedContent = content
                .replaceAll("\\s+", " ");

        String normalizedSkill = skill
                .replaceAll("\\s+", " ")
                .trim();

        if (normalizedSkill.isEmpty()) {
            return false;
        }

        String regex =
                "(?<![A-Za-z0-9])"
                        + Pattern.quote(normalizedSkill)
                        + "(?![A-Za-z0-9])";

        Pattern pattern = Pattern.compile(
                regex,
                Pattern.CASE_INSENSITIVE
                        | Pattern.UNICODE_CASE
        );

        return pattern.matcher(normalizedContent).find();
    }

    /**
     * 生成可以保存在数据库中的分析文字。
     */
    private String buildAnalysis(
            int score,
            List<String> matchedSkills,
            List<String> missingSkills) {

        StringBuilder sb = new StringBuilder();

        sb.append("匹配方式：规则关键词匹配\n");
        sb.append("匹配得分：").append(score).append("\n");

        sb.append("已匹配技能：");

        if (matchedSkills.isEmpty()) {
            sb.append("无");
        } else {
            sb.append(String.join("、", matchedSkills));
        }

        sb.append("\n");

        sb.append("缺失技能：");

        if (missingSkills.isEmpty()) {
            sb.append("无");
        } else {
            sb.append(String.join("、", missingSkills));
        }

        sb.append("\n");

        if (score >= 80) {
            sb.append(
                    "建议：关键词覆盖率较高，可以进一步准备相关项目案例。"
            );
        } else if (score >= 60) {
            sb.append(
                    "建议：部分技能已经符合要求，建议补充缺失技能。"
            );
        } else {
            sb.append(
                    "建议：当前关键词覆盖率较低，建议优先学习岗位核心技能。"
            );
        }

        sb.append(
                "\n说明：该分数仅表示关键词覆盖率，不代表实际录用概率。"
        );

        return sb.toString();
    }

    /**
     * 根据匹配记录构建响应。
     *
     * 历史记录中的匹配信息来自数据库，
     * 不重新计算分数。
     */
    private MatchResponse toResponse(MatchRecord record) {

        MatchResponse response = new MatchResponse();

        response.setId(record.getId());
        response.setResumeId(record.getResumeId());
        response.setJobId(record.getJobId());
        response.setScore(record.getScore());
        response.setAnalysis(record.getAnalysis());
        response.setCreatedAt(record.getCreatedAt());

        // 从保存的分析文本恢复技能列表
        response.setMatchedSkills(
                parseSkillsFromAnalysis(
                        record.getAnalysis(),
                        "已匹配技能："
                )
        );

        response.setMissingSkills(
                parseSkillsFromAnalysis(
                        record.getAnalysis(),
                        "缺失技能："
                )
        );

        // 如果岗位还存在，就获取岗位名称
        jobRepository.findById(record.getJobId())
                .ifPresent(job ->
                        response.setJobTitle(job.getTitle())
                );

        return response;
    }

    /**
     * 从历史分析文本中恢复关键词列表。
     */
    private List<String> parseSkillsFromAnalysis(
            String analysis,
            String prefix) {

        if (analysis == null || analysis.isBlank()) {
            return List.of();
        }

        for (String line : analysis.split("\\R")) {

            if (line.startsWith(prefix)) {

                String value =
                        line.substring(prefix.length()).trim();

                if (value.isBlank() || value.equals("无")) {
                    return List.of();
                }

                return List.of(value.split("、"));
            }
        }

        return List.of();
    }

    /**
     * 查询一条匹配记录。
     */
    @Override
    @Transactional(readOnly = true)
    public MatchResponse getMatchById(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException(
                    ErrorCode.PARAM_ERROR
            );
        }

        MatchRecord record = matchRecordRepository
                .findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                404,
                                "匹配记录不存在"
                        )
                );

        return toResponse(record);
    }

    /**
     * 查询某份简历的匹配历史。
     */
    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> listByResumeId(Long resumeId) {

        if (resumeId == null || resumeId <= 0) {
            throw new BusinessException(
                    ErrorCode.PARAM_ERROR
            );
        }

        return matchRecordRepository
                .findByResumeIdOrderByCreatedAtDesc(resumeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 查询某个岗位的匹配历史。
     */
    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> listByJobId(Long jobId) {

        if (jobId == null || jobId <= 0) {
            throw new BusinessException(
                    ErrorCode.PARAM_ERROR
            );
        }

        return matchRecordRepository
                .findByJobIdOrderByCreatedAtDesc(jobId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}