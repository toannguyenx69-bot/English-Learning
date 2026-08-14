package com.example.englishlearning.dto;

import java.time.LocalDateTime;

public class GrammarProgressResponse {

    private Long id;
    private Long questionId;
    private String questionText;
    private Long selectedAnswerId;
    private String selectedAnswerText;
    private boolean correct;
    private LocalDateTime attemptedAt;

    public GrammarProgressResponse() {
    }

    public GrammarProgressResponse(Long id, Long questionId, String questionText, Long selectedAnswerId,
                                  String selectedAnswerText, boolean correct, LocalDateTime attemptedAt) {
        this.id = id;
        this.questionId = questionId;
        this.questionText = questionText;
        this.selectedAnswerId = selectedAnswerId;
        this.selectedAnswerText = selectedAnswerText;
        this.correct = correct;
        this.attemptedAt = attemptedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Long getSelectedAnswerId() {
        return selectedAnswerId;
    }

    public void setSelectedAnswerId(Long selectedAnswerId) {
        this.selectedAnswerId = selectedAnswerId;
    }

    public String getSelectedAnswerText() {
        return selectedAnswerText;
    }

    public void setSelectedAnswerText(String selectedAnswerText) {
        this.selectedAnswerText = selectedAnswerText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
}
