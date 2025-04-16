package com.jackyfan.handsonspringai.service;

import com.jackyfan.handsonspringai.domain.Answer;
import com.jackyfan.handsonspringai.domain.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SpringAiBoardGameService implements BoardGameService {
    private final ChatClient chatClient;
    @Value("classpath:/templates/prompts/systemPromptTemplate.st")
    private Resource questionPromptTemplate;
    private final GameRulesService gameRulesService;

    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder, GameRulesService gameRulesService) {
        this.chatClient = chatClientBuilder.build();
        this.gameRulesService = gameRulesService;
    }

    @Override
    public Answer askQuestion(Question question) {
        String gameRules = gameRulesService.getRulesFor(question.gameTitle());
        return  chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(questionPromptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("rules", gameRules))
                .user(question.question())
                .call()
                .entity(Answer.class);
    }
}
