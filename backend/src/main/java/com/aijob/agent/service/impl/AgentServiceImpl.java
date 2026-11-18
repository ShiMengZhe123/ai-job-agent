
package com.aijob.agent.service.impl;

import com.aijob.agent.agent.JobAssistantAgent;
import com.aijob.agent.dto.AgentChatRequest;
import com.aijob.agent.dto.AgentChatResponse;
import com.aijob.agent.exception.BusinessException;
import com.aijob.agent.service.AgentService;

import org.springframework.stereotype.Service;

/**
 * Agent 业务服务实现类。
 */
@Service
public class AgentServiceImpl implements AgentService {

    private final JobAssistantAgent jobAssistantAgent;

    public AgentServiceImpl(
            JobAssistantAgent jobAssistantAgent) {

        this.jobAssistantAgent = jobAssistantAgent;
    }

    @Override
    public AgentChatResponse chat(AgentChatRequest request) {

        if (request == null
                || request.getMessage() == null
                || request.getMessage().isBlank()) {

            throw new BusinessException(
                    400,
                    "聊天内容不能为空"
            );
        }

        if (request.getMessage().length() > 4000) {
            throw new BusinessException(
                    400,
                    "聊天内容不能超过4000个字符"
            );
        }

        String answer = jobAssistantAgent.chat(
                request.getMessage()
        );


        return new AgentChatResponse(
                answer,
                "qwen3:4b",
                "AI智能求职助手Agent"
        );
    }
}