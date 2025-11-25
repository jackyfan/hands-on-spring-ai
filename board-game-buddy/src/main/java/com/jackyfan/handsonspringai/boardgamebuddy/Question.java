package com.jackyfan.handsonspringai.boardgamebuddy;

import jakarta.validation.constraints.NotBlank;

public record Question(
        @NotBlank(message = "游戏标题必填") String gameTitle,
        @NotBlank(message = "问题必填")String question) {
}
