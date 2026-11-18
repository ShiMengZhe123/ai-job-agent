
package com.aijob.agent.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 简历实体类
 * 对应MySQL数据库中的resume表
 */
@Entity
@Table(name = "resume")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 简历原始文件名称
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    // 存储文件路径
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    // 文件类型，如pdf、docx
    @Column(name = "file_type", length = 20)
    private String fileType;

    // 从简历中提取的文本
    @Lob
    @Column(name = "content")
    private String content;

    // AI提取的技能信息
    @Lob
    @Column(name = "skills")
    private String skills;

    // 创建时间
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Resume() {
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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