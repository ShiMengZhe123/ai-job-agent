
package com.aijob.agent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 配置类。
 *
 * Spring AI DeepSeek Starter 自动创建 ChatModel。
 * 本类将它包装成 ChatClient。
 */
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {

        return ChatClient.builder(chatModel)
                .build();
    }
}