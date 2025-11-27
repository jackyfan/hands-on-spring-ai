package com.jackyfan.handsonspringai.boardgamebuddy;

import io.qdrant.client.grpc.Points;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;


import java.util.stream.Collectors;

@Service
public class GameRulesService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GameRulesService.class);
    private final VectorStore vectorStore;

    public GameRulesService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String getRulesFor(String gameName, String question) {
        var searchRequest = SearchRequest.builder()
                .query(question)
                .topK(6)
                .similarityThreshold(0.5)
                .filterExpression(
                        new FilterExpressionBuilder()
                                .eq("gameTitle", normalizeGameTitle(gameName))
                                .build())
                .build();
        LOGGER.info("Search Request:{}", searchRequest);
        var similarDocs = vectorStore.similaritySearch(searchRequest);
        if (similarDocs.isEmpty()) {
            return "The rules for " + gameName + " are not available.";
        }
        return similarDocs.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
    }

    private String normalizeGameTitle(String gameTitle) {
        return gameTitle.toLowerCase().replace(" ", "_");
    }

}
