
package com.aijob.agent.dto;

import com.aijob.agent.entity.Resume;

import java.time.LocalDateTime;

/**
 * 简历接口返回对象。
 */
public class ResumeResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private String content;
    private String skills;
    private LocalDateTime createdAt;

    public ResumeResponse() {
    }

    /**
     * 数据库实体转换为接口响应。
     */
    public static ResumeResponse fromEntity(Resume resume) {

        ResumeResponse response = new ResumeResponse();

        response.setId(resume.getId());
        response.setFileName(resume.getFileName());
        response.setFileType(resume.getFileType());
        response.setContent(resume.getContent());
        response.setSkills(resume.getSkills());
        response.setCreatedAt(resume.getCreatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}