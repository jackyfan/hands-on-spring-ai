package com.jackyfan.handsonspringai.boardgamebuddy.service;

import com.jackyfan.handsonspringai.boardgamebuddy.domain.Answer;
import com.jackyfan.handsonspringai.boardgamebuddy.domain.Question;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.FactCheckingEvaluator;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@Slf4j
@SpringBootTest
public class SpringAiBoardGameServiceTest {
    @Autowired
    private BoardGameService boardGameService;
    @Autowired
    private ChatClient.Builder chatClientBuilder;
    private RelevancyEvaluator relevancyEvaluator;
    //判断是否正确回答的问题
    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    public void setup() {
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        this.factCheckingEvaluator = new FactCheckingEvaluator(chatClientBuilder);
    }

    @Test
    public void evaluateFactualAccuracy() {
        String title = "relevancy Evaluator";
        String userText = "Why is the sky blue?";
        Question question = new Question(title, userText);
        Answer answer = boardGameService.askQuestion(question);
        String referenceAnswer =
                "The sky is blue because of that was the paint color that was on sale.";

        EvaluationRequest evaluationRequest =
                new EvaluationRequest(answer.answer(), Collections.emptyList(), referenceAnswer);

        EvaluationResponse response =
                factCheckingEvaluator.evaluate(evaluationRequest);

        Assertions.assertThat(response.isPass())
                .withFailMessage("""

                        ========================================
                        The answer "%s"
                        is not considered correct for the question
                        "%s".
                        ========================================
                        """, answer.answer(), userText)
                .isTrue();
    }

}
