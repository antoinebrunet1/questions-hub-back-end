package com.example.questionshub.question.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class QuestionDto {
    private UUID id;

    @NotNull(message = "Body is required")
    private String body;

    @NotNull(message = "Answer is required")
    private String answer;
}
