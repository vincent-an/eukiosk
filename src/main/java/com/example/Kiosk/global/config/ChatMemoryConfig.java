package com.example.Kiosk.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Bean
    public ChatMemoryRepository chatMemoryRepository() {
        // In-memory 레포지토리 (앱 재시작 시 초기화됨)
        return new InMemoryChatMemoryRepository();
    }

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository repo) {
        // MessageWindowChatMemory를 사용 (슬라이딩 윈도우)
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repo)
                .maxMessages(10)   // 최근 메시지 10개만 유지 (원하면 변경)
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        // Builder 패턴으로 Advisor 생성 — 생성자 직접 호출하지 마라
        return MessageChatMemoryAdvisor.builder(chatMemory)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, MessageChatMemoryAdvisor advisor) {
        // ChatClient.Builder를 받아 default advisors로 메모리 어드바이저를 붙임
        return builder
                .defaultAdvisors(advisor)
                .build();
    }
}