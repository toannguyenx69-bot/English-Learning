package com.example.englishlearning.dto;

public class GrammarAnswerOptionResponse {

    private Long id;
    private String answer;

    public GrammarAnswerOptionResponse() {
    }

    public GrammarAnswerOptionResponse(Long id, String answer) {
        this.id = id;
        this.answer = answer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
