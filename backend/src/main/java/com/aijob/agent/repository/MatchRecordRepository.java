
package com.aijob.agent.repository;

import com.aijob.agent.entity.MatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRecordRepository
        extends JpaRepository<MatchRecord, Long> {

    List<MatchRecord> findByResumeIdOrderByCreatedAtDesc(
            Long resumeId
    );

    List<MatchRecord> findByJobIdOrderByCreatedAtDesc(
            Long jobId
    );
}