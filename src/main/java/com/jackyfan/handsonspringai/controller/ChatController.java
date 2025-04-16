package com.jackyfan.handsonspringai.controller;

import com.jackyfan.handsonspringai.domain.Answer;
import com.jackyfan.handsonspringai.domain.Question;
import com.jackyfan.handsonspringai.service.BoardGameService;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class ChatController {
    @Qualifier("springAiBoardGameService")
    private final BoardGameService boardGameService;

    public ChatController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @PostMapping("/ask")
    public Answer chat(@RequestBody @Valid Question question) {
        return boardGameService.askQuestion(question);
    }
}
