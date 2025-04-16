package com.jackyfan.handsonspringai;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.Retryable;

@Retryable
@SpringBootApplication
public class HandsonspringaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HandsonspringaiApplication.class, args);
    }

    @Bean
    ChatMemory chatMemory() {      //
        return new InMemoryChatMemory();
    }


}
