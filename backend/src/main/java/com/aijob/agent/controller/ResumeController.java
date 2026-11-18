
package com.aijob.agent.controller;

import com.aijob.agent.common.Result;
import com.aijob.agent.dto.ResumeResponse;
import com.aijob.agent.service.ResumeService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 简历管理 REST API。
 */
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    /**
     * 上传并解析简历。
     *
     * POST /api/resumes/upload
     */
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Result<ResumeResponse> upload(
            @RequestParam("file") MultipartFile file) {

        ResumeResponse response =
                resumeService.uploadResume(file);

        return Result.success(response);
    }

    /**
     * 查询所有简历。
     *
     * GET /api/resumes
     */
    @GetMapping
    public Result<List<ResumeResponse>> list() {

        return Result.success(
                resumeService.listResumes()
        );
    }

    /**
     * 查询单份简历。
     *
     * GET /api/resumes/{id}
     */
    @GetMapping("/{id}")
    public Result<ResumeResponse> detail(
            @PathVariable Long id) {

        return Result.success(
                resumeService.getResumeById(id)
        );
    }

    /**
     * 删除简历。
     *
     * DELETE /api/resumes/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id) {

        resumeService.deleteResume(id);

        return Result.success();
    }
}