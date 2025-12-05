package com.jackyfan.handsonspringai.chaining;

import java.util.List;

public class Chain implements Action{
    private final List<Action> tasks;

    public Chain(List<Action> tasks) {
        this.tasks = tasks;
    }
    @Override
    public String act(String input) {
        String response = input;
        for (Action task : tasks) {
                response = task.act(response);
        }
        return response;
    }
}
