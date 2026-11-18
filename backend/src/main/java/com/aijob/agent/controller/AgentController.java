
package com.aijob.agent.controller;

import com.aijob.agent.common.Result;
import com.aijob.agent.dto.AgentChatRequest;
import com.aijob.agent.dto.AgentChatResponse;
import com.aijob.agent.service.AgentService;

import org.springframework.web.bind.annotation.*;

/**
 * AI Agent 对话接口。
 */
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * POST /api/agent/chat
     */
    @PostMapping("/chat")
    public Result<AgentChatResponse> chat(
            @RequestBody AgentChatRequest request) {

        return Result.success(
                agentService.chat(request)
        );
    }
}