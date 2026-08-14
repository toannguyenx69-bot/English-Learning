package com.example.englishlearning.dto;

import jakarta.validation.constraints.NotNull;

public class GrammarAnswerSubmissionRequest {

    @NotNull(message = "Answer ID is required")
    private Long answerId;

    public GrammarAnswerSubmissionRequest() {
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }
}
