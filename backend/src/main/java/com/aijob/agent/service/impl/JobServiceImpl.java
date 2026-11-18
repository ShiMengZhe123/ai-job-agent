
package com.aijob.agent.service.impl;

import com.aijob.agent.common.ErrorCode;
import com.aijob.agent.dto.JobRequest;
import com.aijob.agent.entity.Job;
import com.aijob.agent.exception.BusinessException;
import com.aijob.agent.repository.JobRepository;
import com.aijob.agent.service.JobService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 招聘岗位业务实现类
 *
 * 功能：
 * 1. 新增岗位
 * 2. 查询岗位列表
 * 3. 查询岗位详情
 * 4. 修改岗位
 * 5. 删除岗位
 */
@Service
public class JobServiceImpl implements JobService {

    private static final Logger log =
            LoggerFactory.getLogger(JobServiceImpl.class);

    private final JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * 新增岗位
     */
    @Override
    @Transactional
    public Job createJob(JobRequest request) {

        validateRequest(request);

        Job job = new Job();

        copyProperties(request, job);

        Job saved = jobRepository.saveAndFlush(job);

        log.info(
                "新增岗位成功，ID={}，岗位名称={}",
                saved.getId(),
                saved.getTitle()
        );

        return saved;
    }

    /**
     * 查询全部岗位
     */
    @Override
    @Transactional(readOnly = true)
    public List<Job> listJobs() {

        return jobRepository.findAll();
    }

    /**
     * 根据 ID 查询岗位
     */
    @Override
    @Transactional(readOnly = true)
    public Job getJobById(Long id) {

        if (id == null || id <= 0) {
            throw new BusinessException(
                    400,
                    "岗位 ID 必须大于 0"
            );
        }

        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.JOB_NOT_FOUND
                        )
                );
    }

    /**
     * 修改岗位信息
     */
    @Override
    @Transactional
    public Job updateJob(Long id, JobRequest request) {

        validateRequest(request);

        Job job = getJobById(id);

        copyProperties(request, job);

        Job updated = jobRepository.saveAndFlush(job);

        log.info(
                "更新岗位成功，ID={}，岗位名称={}",
                updated.getId(),
                updated.getTitle()
        );

        return updated;
    }

    /**
     * 删除岗位
     */
    @Override
    @Transactional
    public void deleteJob(Long id) {

        Job job = getJobById(id);

        jobRepository.delete(job);

        jobRepository.flush();

        log.info("删除岗位成功，ID={}", id);
    }

    /**
     * 校验请求参数
     */
    private void validateRequest(JobRequest request) {

        if (request == null) {
            throw new BusinessException(
                    400,
                    "岗位请求不能为空"
            );
        }

        if (request.getTitle() == null ||
                request.getTitle().isBlank()) {

            throw new BusinessException(
                    400,
                    "岗位名称不能为空"
            );
        }

        if (request.getTitle().length() > 150) {
            throw new BusinessException(
                    400,
                    "岗位名称不能超过150个字符"
            );
        }

        if (request.getCompany() != null &&
                request.getCompany().length() > 150) {

            throw new BusinessException(
                    400,
                    "公司名称不能超过150个字符"
            );
        }

        if (request.getLocation() != null &&
                request.getLocation().length() > 150) {

            throw new BusinessException(
                    400,
                    "工作地点不能超过150个字符"
            );
        }
    }

    /**
     * 将请求信息复制到数据库实体
     */
    private void copyProperties(
            JobRequest request,
            Job job) {

        job.setTitle(request.getTitle().trim());

        job.setCompany(
                trimToNull(request.getCompany())
        );

        job.setLocation(
                trimToNull(request.getLocation())
        );

        job.setDescription(
                trimToNull(request.getDescription())
        );

        job.setRequirements(
                trimToNull(request.getRequirements())
        );
    }

    /**
     * 去掉字符串前后空格，
     * 空字符串转为 null
     */
    private String trimToNull(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}