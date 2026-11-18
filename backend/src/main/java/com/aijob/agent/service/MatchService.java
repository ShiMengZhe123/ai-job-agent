
package com.aijob.agent.service;

import com.aijob.agent.dto.MatchRequest;
import com.aijob.agent.dto.MatchResponse;

import java.util.List;

/**
 * 简历岗位匹配业务接口
 */
public interface MatchService {

    /**
     * 执行匹配并保存记录
     */
    MatchResponse match(MatchRequest request);

    /**
     * 根据记录 ID 查询匹配结果
     */
    MatchResponse getMatchById(Long id);

    /**
     * 查询某份简历的匹配历史
     */
    List<MatchResponse> listByResumeId(Long resumeId);

    /**
     * 查询某个岗位的匹配历史
     */
    List<MatchResponse> listByJobId(Long jobId);
}