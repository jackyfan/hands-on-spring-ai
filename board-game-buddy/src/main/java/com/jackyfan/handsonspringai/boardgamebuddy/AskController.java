package com.jackyfan.handsonspringai.boardgamebuddy;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskController {
    private final BoardGameService boardGameService;

    public AskController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @PostMapping(value = "/ask", produces = "application/json")
    public Answer ask(@RequestBody @Valid Question question) {
        return boardGameService.askQuestion(question);
    }


    @PostMapping(value = "/askWithMemory", produces = "application/json")
    public Answer ask(
            @RequestHeader(name = "X_AI_CONVERSATION_ID",
                    defaultValue = "default") String conversationId,
            @RequestBody @Valid Question question) {
        return boardGameService.askQuestionWithMemory(question, conversationId);
    }


}
