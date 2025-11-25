package com.jackyfan.handsonspringai.boardgamebuddy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.content.Content;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.FactCheckingEvaluator;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ModelEvaluationTest {

    private ChatClient chatClient;
    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Autowired
    private ChatModel chatModel;


    @BeforeEach
    public void setup() {
        this.chatClient = chatClientBuilder.build();
    }
    @Test
    @Disabled("Run test manually when you add your API KEY to application.yml")
    void testEvaluation() {

        //dataController.delete();
        //dataController.load();

        String userText = "What is the purpose of Carina?";

        ChatResponse response = ChatClient.builder(chatModel)
                .build().prompt()
                .user(userText)
                .call()
                .chatResponse();
        String responseContent = response.getResult().getOutput().getText();

        var relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText,
                List.of(), responseContent);

        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        assertTrue(evaluationResponse.isPass(), "Response is not relevant to the question");

    }

    @Test
    void testFactChecking() {
        // Set up the Ollama API
       // OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");

        //ChatModel chatModel = new OllamaChatModel(ollamaApi,OllamaOptions.builder().model(BESPOKE_MINICHECK).numPredict(2).temperature(0.0d).build())


        // Create the FactCheckingEvaluator
        var factCheckingEvaluator = new FactCheckingEvaluator(chatClientBuilder);

        // Example context and claim
        String context = "The Earth is the third planet from the Sun and the only astronomical object known to harbor life.";
        String claim = "The Earth is the fourth planet from the Sun.";

        // Create an EvaluationRequest
        EvaluationRequest evaluationRequest = new EvaluationRequest(context, Collections.emptyList(), claim);

        // Perform the evaluation
        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);

        assertFalse(evaluationResponse.isPass(), "The claim should not be supported by the context");

    }
}
