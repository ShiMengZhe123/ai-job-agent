
package com.aijob.agent.repository;

import com.aijob.agent.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository
        extends JpaRepository<Job, Long> {
}