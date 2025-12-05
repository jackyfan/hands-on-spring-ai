package com.jackyfan.handsonspringai.boardgamebuddy;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import reactor.core.publisher.Flux;

public class SelfEvaluatingBoardGameService implements BoardGameService {
    private final ChatClient chatClient;
    private final RelevancyEvaluator relevancyEvaluator;

    public SelfEvaluatingBoardGameService(ChatClient.Builder chatClientBuilder) {
        chatClient = chatClientBuilder.build();
        relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
    }

    @Override
    @Retryable(retryFor = AnswerNotRelevantException.class)
    public Answer askQuestion(Question question) {
        var answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();
        evaluateRelevancy(question, answerText);
        return new Answer(answerText,question.gameTitle());
    }

    @Override
    public Flux<String> askQuestionWithStreaming(Question question) {
        return null;
    }

    @Override
    public Answer askQuestionWithMemory(Question question, String conversationId) {
        return null;
    }

    @Recover
    public Answer recover(AnswerNotRelevantException e) {
        return new Answer("I'm sorry, I wasn't able to answer the question.","");
    }

    private void evaluateRelevancy(Question question, String answerText) {
        var evaluationRequest = new EvaluationRequest(
                question.question(), answerText);
        var response = relevancyEvaluator.evaluate(evaluationRequest);
        if (!response.isPass()) {
            throw new AnswerNotRelevantException(question.question(), answerText);
        }
    }
}
