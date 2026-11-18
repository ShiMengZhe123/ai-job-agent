
package com.aijob.agent.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 招聘岗位实体类
 * 对应MySQL数据库中的job表
 */
@Entity
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 岗位名称
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    // 公司名称
    @Column(name = "company", length = 150)
    private String company;

    // 工作地点
    @Column(name = "location", length = 150)
    private String location;

    // 岗位描述
    @Lob
    @Column(name = "description")
    private String description;

    // 岗位技能要求
    @Lob
    @Column(name = "requirements")
    private String requirements;

    // 创建时间
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Job() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}