package com.jackyfan.handsonspringai.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SpringAiBoardGameService implements BoardGameService {
    private final ChatClient chatClient;
    @Value("classpath:/promptTemplates/questionPromptTemplate.st")
    Resource questionPromptTemplate;
    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    Resource systemPromptTemplate;

    private final GameRulesService gameRulesService;

    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder, GameRulesService gameRulesService) {
        this.chatClient = chatClientBuilder.build();
        this.gameRulesService = gameRulesService;
    }

    @Override
    public Answer askQuestion(Question question) {
        var roles = gameRulesService.getRulesFor(question.gameTitle());
        var text = chatClient.prompt().system(promptSystemSpec -> {
            promptSystemSpec.text(systemPromptTemplate).
                    param("gameTitle", question.gameTitle()).
                    param("rules", roles);
        }).user(question.question()).call().content();
        return new Answer(text, question.gameTitle());
    }
}
