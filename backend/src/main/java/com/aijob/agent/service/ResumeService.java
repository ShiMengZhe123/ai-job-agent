
package com.aijob.agent.service;

import com.aijob.agent.dto.ResumeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 简历业务接口。
 */
public interface ResumeService {

    /**
     * 上传、解析并保存简历。
     */
    ResumeResponse uploadResume(MultipartFile file);

    /**
     * 查询全部简历。
     */
    List<ResumeResponse> listResumes();

    /**
     * 根据主键查询简历。
     */
    ResumeResponse getResumeById(Long id);

    /**
     * 删除简历记录及对应的本地文件。
     */
    void deleteResume(Long id);
}