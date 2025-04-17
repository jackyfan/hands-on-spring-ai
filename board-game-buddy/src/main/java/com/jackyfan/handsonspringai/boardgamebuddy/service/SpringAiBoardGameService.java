package com.jackyfan.handsonspringai.boardgamebuddy.service;

import com.jackyfan.handsonspringai.boardgamebuddy.domain.Answer;
import com.jackyfan.handsonspringai.boardgamebuddy.domain.Question;
import com.jackyfan.handsonspringai.boardgamebuddy.tools.GameTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
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


    public SpringAiBoardGameService(ChatClient.Builder chatClientBuilder, GameRulesService gameRulesService, GameTools gameTools) {
        this.chatClient = chatClientBuilder.defaultTools(gameTools).build();
        this.gameRulesService = gameRulesService;
    }

    @Override
    public Answer askQuestion(Question question, String conversationId) {
        String gameRules = gameRulesService.getRulesFor(question.gameTitle());
        var responseEntity = chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(systemPromptTemplate)
                        .param("gameTitle", question.gameTitle())
                        .param("rules", gameRules))
                .advisors(advisorSpec -> advisorSpec.
                        param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
                .user(question.question())
                .call()
                .responseEntity(String.class);
        var chatResponse = responseEntity.getResponse();
        ChatResponseMetadata metadata = chatResponse.getMetadata();
        log(metadata.getUsage());
        return new Answer(question.gameTitle(), responseEntity.entity());
    }

    @Override
    public Answer askQuestion(Question question,
                              Resource image,            //
                              String imageContentType,   //
                              String conversationId) {
        String gameNameMatch = String.format(
                "gameTitle == '%s'",
                normalizeGameTitle(question.gameTitle()));

        MimeType mediaType =
                MimeTypeUtils.parseMimeType(imageContentType); //

        return chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(question.question())
                        .media(mediaType, image)) //
                .system(systemSpec -> systemSpec
                        .text(systemPromptTemplate)
                        .param("gameTitle", question.gameTitle()))
                .advisors(advisorSpec -> advisorSpec
                        .param(mediaType.getSubtype(), gameNameMatch)
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId))
                .call()
                .entity(Answer.class);
    }


    private void log(Usage usage) {
        log.info("Token usage: prompt={}, generation={}, total={}",
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens());
    }

    public static String normalizeGameTitle(String title) {
        if (title == null || title.isEmpty()) {
            return "";
        }
        // 去除前后的空格
        title = title.trim();
        // 将所有字符转换为小写
        title = title.toLowerCase();
        // 去除特殊字符，只保留字母、数字和空格
        title = title.replaceAll("[^a-z0-9\\s]", "");
        // 将多个空格替换为单个空格
        title = title.replaceAll("\\s+", " ");
        return title;
    }
}
