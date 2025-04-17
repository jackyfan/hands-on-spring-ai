package com.jackyfan.handsonspringai.boardgamebuddy.service;

import com.jackyfan.handsonspringai.boardgamebuddy.domain.Answer;
import com.jackyfan.handsonspringai.boardgamebuddy.domain.Question;
import com.jackyfan.handsonspringai.boardgamebuddy.exception.AnswerNotRelevantException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class SelfEvaluatingBoardGameService implements BoardGameService {
    private final ChatClient chatClient;

    private final RelevancyEvaluator evaluator;

    public SelfEvaluatingBoardGameService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.evaluator = new RelevancyEvaluator(chatClientBuilder);
    }

    @Override
    @Retryable(retryFor = AnswerNotRelevantException.class,maxAttempts = 2)
    public Answer askQuestion(Question question,String conversationId) {
        String answerText = chatClient.prompt().user(question.question()).call().content();
        evaluateRelevancy(question, answerText);
        return new Answer(question.gameTitle(),answerText);
    }

    @Override
    public Answer askQuestion(Question question, Resource image, String imageContentType, String conversationId) {
        return null;
    }


    @Recover
    private Answer recover(AnswerNotRelevantException e) {
        return new Answer("Answer Not Relevant","对不起，我回答不了这个问题，我会继续学习。");
    }

    private void evaluateRelevancy(Question question, String answerText) {
        EvaluationRequest request = new EvaluationRequest(question.question(), List.of(), answerText);
        EvaluationResponse response = evaluator.evaluate(request);
        if (!response.isPass()) {
            throw new AnswerNotRelevantException(question.question(), answerText);
        }
    }
}
