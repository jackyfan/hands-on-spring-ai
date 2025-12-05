package com.jackyfan.handsonspringai.chaining;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class MechanicsDeterminerAction implements Action {
    private final ChatClient chatClient;

    public MechanicsDeterminerAction(ChatClient.Builder chatClientBuilder,
                                     @Value("classpath:/promptTemplates/mechanicsDeterminer.st")
                                     Resource systemMessageTemplate) {
        this.chatClient = chatClientBuilder.defaultSystem(systemMessageTemplate).build();
    }

    @Override
    public String act(String rules) {
        return chatClient.prompt()
                .user(text ->
                        text.text("Analyze the following rules:\\n\\nRULES:\\n\\n{rules}")
                                .param("rules", rules))
                .call().content();
    }
}
