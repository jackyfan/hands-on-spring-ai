package com.jackyfan.handsonspringai.service;

import com.jackyfan.handsonspringai.domain.Answer;
import com.jackyfan.handsonspringai.domain.Question;

public interface BoardGameService {
    Answer askQuestion(Question question);
}
