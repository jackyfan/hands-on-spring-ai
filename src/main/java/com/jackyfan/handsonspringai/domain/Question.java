package com.jackyfan.handsonspringai.domain;

import jakarta.validation.constraints.NotBlank;

public record Question(@NotBlank(message = "gameTitle is required") String gameTitle,
                       @NotBlank(message = "Question is required") String question) {
}
