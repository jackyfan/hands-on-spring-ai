package com.jackyfan.handsonspringai.domain;

public enum GameComplexity {
    Easy(1.0f), Moderately_Easy(2.0f),
    Moderate(3.0f), Moderately_Difficult(4.0f),
    Difficult(5.0f), UNKNOWN(99.99f);

    GameComplexity(float complexity) {
        this.complexity = complexity;
    }
    private final float complexity;
    public float getValue() {
        return complexity;
    }
}
