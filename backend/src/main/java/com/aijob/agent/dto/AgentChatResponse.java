
package com.aijob.agent.dto;

/**
 * Agent 聊天响应对象。
 */
public class AgentChatResponse {

    private String message;
    private String model;
    private String agent;

    public AgentChatResponse() {
    }

    public AgentChatResponse(
            String message,
            String model,
            String agent) {

        this.message = message;
        this.model = model;
        this.agent = agent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}