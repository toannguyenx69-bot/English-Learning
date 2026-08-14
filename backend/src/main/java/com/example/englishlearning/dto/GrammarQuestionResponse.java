package com.example.englishlearning.dto;

import java.time.LocalDateTime;
import java.util.List;

public class GrammarQuestionResponse {

    private Long id;
    private String question;
    private String explanation;
    private String difficulty;
    private String topic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GrammarAnswerOptionResponse> answers;

    public GrammarQuestionResponse() {
    }

    public GrammarQuestionResponse(Long id, String question, String explanation, String difficulty,
                                  String topic, LocalDateTime createdAt, LocalDateTime updatedAt,
                                  List<GrammarAnswerOptionResponse> answers) {
        this.id = id;
        this.question = question;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.topic = topic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.answers = answers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<GrammarAnswerOptionResponse> getAnswers() {
        return answers;
    }

    public void setAnswers(List<GrammarAnswerOptionResponse> answers) {
        this.answers = answers;
    }
}
