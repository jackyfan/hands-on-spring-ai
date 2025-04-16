package com.jackyfan.handsonspringai.service;

import com.jackyfan.handsonspringai.domain.Answer;
import com.jackyfan.handsonspringai.domain.Question;
import reactor.core.publisher.Flux;

public interface BoardGameService {
    Answer askQuestion(Question question);
    Flux<String> askQuestionWithFlux(Question question);
}
