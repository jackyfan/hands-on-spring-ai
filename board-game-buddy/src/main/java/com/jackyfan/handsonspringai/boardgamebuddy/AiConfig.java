package com.jackyfan.handsonspringai.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AiConfig {


    @Bean
    ChatClient EnhancedChatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        /**
         * RetrievalAugmentationAdvisor的文档检索器是可插拔的，可以与您想要的任何DocumentRetriever实现配合使用;
         * 可以创建自定义实现DocumentRetriever
         */
        var advisor = RetrievalAugmentationAdvisor
                .builder()
                .documentRetriever(
                        VectorStoreDocumentRetriever//查询向量数据库
                                .builder()
                                .vectorStore(vectorStore)
                                .build()).queryExpander(
                        MultiQueryExpander//通过多种不同方式提出相同问题
                                .builder()
                                .chatClientBuilder(chatClientBuilder)
                                .numberOfQueries(5)
                                .includeOriginal(true).build())
                .queryTransformers(
                        TranslationQueryTransformer//翻译成英语
                                .builder()
                                .chatClientBuilder(chatClientBuilder)
                                .targetLanguage("english")
                                .build(),
                        RewriteQueryTransformer//重写查询
                                .builder()
                                .chatClientBuilder(chatClientBuilder)
                                .build())
                .build();
        return chatClientBuilder.defaultAdvisors(advisor).build();
    }

    @Bean("memoryChatClient")
    ChatClient MemoryChatClient(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            ChatMemory chatMemory) {
        return chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder().build()).build())
                .build();
    }

    @Bean
    ChatClient VectorStoreChatClient(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore) {
        return chatClientBuilder
                .defaultAdvisors(
                        VectorStoreChatMemoryAdvisor.builder(vectorStore).build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder().build()).build())
                .build();
    }

    @Bean
    ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(50)
                .build();
    }
}
