
package com.aijob.agent.controller;

import com.aijob.agent.common.Result;
import com.aijob.agent.dto.MatchRequest;
import com.aijob.agent.dto.MatchResponse;
import com.aijob.agent.service.MatchService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 简历与岗位匹配接口。
 */
@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /**
     * 执行新的匹配。
     *
     * POST /api/matches
     */
    @PostMapping
    public Result<MatchResponse> match(
            @RequestBody MatchRequest request) {

        return Result.success(
                matchService.match(request)
        );
    }

    /**
     * 查询指定匹配记录。
     *
     * GET /api/matches/1
     */
    @GetMapping("/{id}")
    public Result<MatchResponse> detail(
            @PathVariable Long id) {

        return Result.success(
                matchService.getMatchById(id)
        );
    }

    /**
     * 查询指定简历的匹配历史。
     *
     * GET /api/matches/resume/1
     */
    @GetMapping("/resume/{resumeId}")
    public Result<List<MatchResponse>> byResume(
            @PathVariable Long resumeId) {

        return Result.success(
                matchService.listByResumeId(resumeId)
        );
    }

    /**
     * 查询指定岗位的匹配历史。
     *
     * GET /api/matches/job/1
     */
    @GetMapping("/job/{jobId}")
    public Result<List<MatchResponse>> byJob(
            @PathVariable Long jobId) {

        return Result.success(
                matchService.listByJobId(jobId)
        );
    }
}