
package com.aijob.agent.dto;

/**
 * Agent 聊天请求对象。
 */
public class AgentChatRequest {

    private String message;

    public AgentChatRequest() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}