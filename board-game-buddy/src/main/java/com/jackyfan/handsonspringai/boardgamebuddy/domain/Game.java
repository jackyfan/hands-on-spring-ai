package com.jackyfan.handsonspringai.boardgamebuddy.domain;

import org.springframework.data.annotation.Id;

public record Game(
        @Id Long id,
        String slug,
        String title,
        float complexity) {

    public GameComplexity complexityEnum() {
        int rounded = Math.round(complexity);
        return GameComplexity.values()[rounded];
    }

}
