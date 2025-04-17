package com.jackyfan.handsonspringai.boardgamebuddy.service;

import com.jackyfan.handsonspringai.boardgamebuddy.domain.Question;
import com.jackyfan.handsonspringai.boardgamebuddy.domain.Answer;

import org.springframework.core.io.Resource;

public interface BoardGameService {
    Answer askQuestion(Question question, String conversationId);

    Answer askQuestion(Question question,
                       Resource image,
                       String imageContentType,
                       String conversationId);
}
