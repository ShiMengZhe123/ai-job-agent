
package com.aijob.agent.service;

import com.aijob.agent.dto.JobRequest;
import com.aijob.agent.entity.Job;

import java.util.List;

/**
 * 招聘岗位业务接口
 */
public interface JobService {

    /**
     * 新增岗位
     */
    Job createJob(JobRequest request);

    /**
     * 查询所有岗位
     */
    List<Job> listJobs();

    /**
     * 根据 ID 查询岗位
     */
    Job getJobById(Long id);

    /**
     * 修改岗位
     */
    Job updateJob(Long id, JobRequest request);

    /**
     * 删除岗位
     */
    void deleteJob(Long id);
}