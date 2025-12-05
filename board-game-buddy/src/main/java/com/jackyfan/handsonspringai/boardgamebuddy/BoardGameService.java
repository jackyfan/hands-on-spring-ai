package com.jackyfan.handsonspringai.boardgamebuddy;

import reactor.core.publisher.Flux;

public interface BoardGameService {
    Answer askQuestion(Question question);
    Flux<String> askQuestionWithStreaming(Question question);
    Answer askQuestionWithMemory(Question question,String conversationId);
}
