package com.jackyfan.handsonspringai.service;

import com.jackyfan.handsonspringai.domain.Answer;
import com.jackyfan.handsonspringai.domain.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Primary
public class SpringAiBoardGameService implements BoardGameService {
    private static final Logger log =
            LoggerFactory.getLogger(SpringAiBoardGameService.class); //

    private final ChatClient chatClient;
    @Value("classpath:/templates/prompts/questionPromptTemplate.st")
    private Resource questionPromptTemplate;
    @Value("classpath:/templates/prompts/systemPromptTemplate.st")
    private Resource systemPromptTemplate;
    private final GameRulesService gameRulesService;

    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder, GameRulesService gameRulesService) {
        this.chatClient = chatClientBuilder.build();
        this.gameRulesService = gameRulesService;
    }

    @Override
    public Answer askQuestion(Question question) {
        String gameRules = gameRulesService.getRulesFor(question.gameTitle());
        var responseEntity = chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(systemPromptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("rules", gameRules))
                .user(question.question())
                .call()
                .responseEntity(String.class);
        var chatResponse = responseEntity.getResponse();
        ChatResponseMetadata metadata = chatResponse.getMetadata();
        log(metadata.getUsage());
        return new Answer(question.gameTitle(), responseEntity.entity());
    }

    @Override
    public Flux<String> askQuestionWithFlux(Question question) {
        String gameRules = gameRulesService.getRulesFor(question.gameTitle());
        return chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(systemPromptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("rules", gameRules))
                .user(question.question())
                .stream()
                .content();
    }

    private void log(Usage usage) {
        log.info("Token usage: prompt={}, generation={}, total={}",
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens());
    }
}
