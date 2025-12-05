package com.jackyfan.handsonspringai.parallel;

import com.jackyfan.handsonspringai.chaining.Action;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class SummarizerAction implements Action {
    private static final Logger LOGGER =
            Logger.getLogger(SummarizerAction.class.getName());
    private final ChatClient chatClient;

    public SummarizerAction(ChatClient.Builder chatClientBuilder,
                            @Value("classpath:/promptTemplates/summarizer.st")
                            Resource systemMessageTemplate) {
        this.chatClient = chatClientBuilder.defaultSystem(systemMessageTemplate).build();
    }

    @Override
    public String act(String input) {
        LOGGER.info("Summarizing");
        return chatClient.prompt()
                .user(text ->
                        text.text("Summarize the following text:\\n\\n{input}")
                                .param("input", input))
                .call()
                .content();
    }
}
