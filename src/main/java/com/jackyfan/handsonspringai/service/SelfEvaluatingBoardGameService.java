package com.jackyfan.handsonspringai.service;

import com.jackyfan.handsonspringai.domain.Answer;
import com.jackyfan.handsonspringai.domain.Question;
import com.jackyfan.handsonspringai.exception.AnswerNotRelevantException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

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
    public Answer askQuestion(Question question) {
        String answerText = chatClient.prompt().user(question.question()).call().content();
        evaluateRelevancy(question, answerText);
        return new Answer(question.gameTitle(),answerText);
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
