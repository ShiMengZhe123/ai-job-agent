
package com.aijob.agent.service;

import com.aijob.agent.dto.AgentChatRequest;
import com.aijob.agent.dto.AgentChatResponse;

/**
 * Agent 业务服务接口。
 */
public interface AgentService {

    AgentChatResponse chat(AgentChatRequest request);
}