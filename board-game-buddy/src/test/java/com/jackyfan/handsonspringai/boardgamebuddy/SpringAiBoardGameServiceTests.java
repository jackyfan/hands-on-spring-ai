package com.jackyfan.handsonspringai.boardgamebuddy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringAiBoardGameServiceTests {
    @Autowired
    private BoardGameService boardGameService;
    @Autowired
    private ChatClient.Builder chatClientBuilder;
    //用于评估模型生成的响应跟用户问题是否相关
    private RelevancyEvaluator relevancyEvaluator;
    //用于判断模型生成的答案是否真实
    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    public void setup() {
        relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        factCheckingEvaluator = FactCheckingEvaluator.builder(chatClientBuilder).build();
    }

    @Test
    public void evaluateRelevancy() {
        String userText = "苹果公司是种苹果的吗?";
        Question question = new Question(userText, "苹果");
        Answer answer = boardGameService.askQuestion(question);

        EvaluationRequest evaluationRequest = new EvaluationRequest(
                userText, answer.answer());

        EvaluationResponse response = relevancyEvaluator
                .evaluate(evaluationRequest);

        Assertions.assertThat(response.isPass())
                .withFailMessage("""
                        ========================================
                        The answer "%s"
                        is not considered relevant to the question
                        "%s".
                        ========================================
                        """, answer.answer(), userText)
                .isTrue();
    }

    @Test
    public void evaluateFactualAccuracy() {
        var userText = "天空为何是蓝色的？";
        var question = new Question(userText, "天空");
        var answer = boardGameService.askQuestion(question);
        System.out.println(answer.answer());
        var evaluationRequest =
                new EvaluationRequest(userText, answer.answer());
        var response =
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
