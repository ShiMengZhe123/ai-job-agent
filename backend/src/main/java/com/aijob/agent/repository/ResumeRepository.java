
package com.aijob.agent.repository;

import com.aijob.agent.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {
}