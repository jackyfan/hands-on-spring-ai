package com.jackyfan.handsonspringai.boardgamebuddy;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {
    private final BoardGameService boardGameService;

    public AskController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @PostMapping(value = "/ask", produces = "application/json")
    public Answer ask(@RequestBody Question question) {
        return boardGameService.askQuestion(question);
    }
}
