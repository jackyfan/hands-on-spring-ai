package com.jackyfan.handsonspringai.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever.FILTER_EXPRESSION;


@Service
public class SpringAiBoardGameService implements BoardGameService {
    private static final Logger LOG =
            LoggerFactory.getLogger(SpringAiBoardGameService.class);
    private final ChatClient chatClient;
    @Value("classpath:/promptTemplates/questionPromptTemplate.st")
    Resource questionPromptTemplate;
    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    Resource systemPromptTemplate;
    private final VectorStore vectorStore;

    private final GameRulesService gameRulesService;

    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder, GameRulesService gameRulesService, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.gameRulesService = gameRulesService;
        this.vectorStore = vectorStore;
    }

    @Override
    public Answer askQuestion(Question question) {
        var gameTitleMatch = String.format("gameTitle=='%s'", question.gameTitle());
        var responseEntity = chatClient.prompt().system(promptSystemSpec -> {
            promptSystemSpec.text(systemPromptTemplate).
                    param("gameTitle", question.gameTitle());
        }).user(question.question()).advisors(advisorSpec -> advisorSpec.param(FILTER_EXPRESSION, gameTitleMatch)).call().responseEntity(Answer.class);
        //return new Answer(text, question.gameTitle());
        var response = responseEntity.getResponse();
        var metadata = response.getMetadata();
        logUsage(metadata.getUsage());
        return responseEntity.entity();
    }

    @Override
    public Flux<String> askQuestionWithStreaming(Question question) {
        var roles = gameRulesService.getRulesFor(question.gameTitle(), question.question());
        return chatClient.prompt().system(promptSystemSpec -> {
            promptSystemSpec.text(systemPromptTemplate).
                    param("gameTitle", question.gameTitle()).
                    param("rules", roles);
        }).user(question.question()).stream().content();
    }

    private void logUsage(Usage usage) {
        LOG.info("Token Usage: prompt:{},generation:{},total:{}", usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
    }
}
