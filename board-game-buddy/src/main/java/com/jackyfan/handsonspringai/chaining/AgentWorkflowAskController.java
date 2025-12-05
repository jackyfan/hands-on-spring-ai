package com.jackyfan.handsonspringai.chaining;

import com.jackyfan.handsonspringai.boardgamebuddy.Answer;
import com.jackyfan.handsonspringai.boardgamebuddy.Question;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentWorkflowAskController {
    private final Chain chain;

    public AgentWorkflowAskController(Chain chain) {
        this.chain = chain;
    }

    @PostMapping(value = "/agent/ask", produces = "application/json")
    public Answer ask(@RequestBody Question question) {
        var response = chain.act(question.question());
        return new Answer(response, question.gameTitle());
    }
}
