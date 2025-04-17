package com.jackyfan.handsonspringai.boardgamebuddy.service;

import com.jackyfan.handsonspringai.boardgamebuddy.domain.Question;
import com.jackyfan.handsonspringai.boardgamebuddy.domain.Answer;
import reactor.core.publisher.Flux;

public interface BoardGameService {
    Answer askQuestion(Question question);
    Flux<String> askQuestionWithFlux(Question question);
}
