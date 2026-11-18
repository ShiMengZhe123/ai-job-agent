
package com.aijob.agent.repository;

import com.aijob.agent.entity.InterviewMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewMessageRepository
        extends JpaRepository<InterviewMessage, Long> {

    List<InterviewMessage> findBySessionIdOrderByCreatedAtAsc(
            Long sessionId
    );
}