
package com.aijob.agent.dto;

/**
 * 岗位新增与修改请求参数
 */
public class JobRequest {

    // 岗位名称
    private String title;

    // 公司名称
    private String company;

    // 工作地点
    private String location;

    // 岗位描述
    private String description;

    // 岗位技能要求
    private String requirements;

    public JobRequest() {
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
}