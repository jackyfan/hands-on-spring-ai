package com.jackyfan.handsonspringai.chaining;

public class ActionFailedException extends RuntimeException{
    public ActionFailedException(String message) {
        super(message);
    }
}
