
package com.aijob.agent.controller;

import com.aijob.agent.common.Result;
import com.aijob.agent.dto.JobRequest;
import com.aijob.agent.entity.Job;
import com.aijob.agent.service.JobService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 招聘岗位管理接口
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * 新增岗位
     *
     * POST /api/jobs
     */
    @PostMapping
    public Result<Job> createJob(
            @RequestBody JobRequest request) {

        Job job = jobService.createJob(request);

        return Result.success(job);
    }

    /**
     * 查询全部岗位
     *
     * GET /api/jobs
     */
    @GetMapping
    public Result<List<Job>> listJobs() {

        return Result.success(
                jobService.listJobs()
        );
    }

    /**
     * 根据 ID 查询岗位
     *
     * GET /api/jobs/{id}
     */
    @GetMapping("/{id}")
    public Result<Job> getJob(
            @PathVariable Long id) {

        return Result.success(
                jobService.getJobById(id)
        );
    }

    /**
     * 更新岗位
     *
     * PUT /api/jobs/{id}
     */
    @PutMapping("/{id}")
    public Result<Job> updateJob(
            @PathVariable Long id,
            @RequestBody JobRequest request) {

        return Result.success(
                jobService.updateJob(id, request)
        );
    }

    /**
     * 删除岗位
     *
     * DELETE /api/jobs/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteJob(
            @PathVariable Long id) {

        jobService.deleteJob(id);

        return Result.success();
    }
}