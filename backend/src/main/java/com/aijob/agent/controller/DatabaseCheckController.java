
package com.aijob.agent.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class DatabaseCheckController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCheckController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/database-check")
    public Map<String, Object> checkDatabase() {

        Map<String, Object> result = new LinkedHashMap<>();

        // 1. 测试数据库是否能够执行 SQL
        Integer test = jdbcTemplate.queryForObject(
                "SELECT 1", Integer.class
        );

        // 2. 查询简历表中的记录数量
        Long resumeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM resume", Long.class
        );

        // 3. 查询岗位表中的记录数量
        Long jobCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM job", Long.class
        );

        result.put("project", "AI智能求职助手");
        result.put("database", "ai_job_agent");
        result.put("connected", test != null && test == 1);
        result.put("resumeCount", resumeCount);
        result.put("jobCount", jobCount);
        result.put("message", "数据库查询成功");

        return result;
    }
}