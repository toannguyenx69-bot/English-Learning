package com.example.englishlearning.dto;

import java.time.LocalDateTime;

public class UserVocabularyResponse {

    private Long id;
    private Long userId;
    private Long vocabularyId;
    private String word;
    private String meaning;
    private String pronunciation;
    private String partOfSpeech;
    private String example;
    private String difficulty;
    private LocalDateTime createdAt;

    public UserVocabularyResponse() {
    }

    public UserVocabularyResponse(Long id, Long userId, Long vocabularyId, String word, String meaning,
                                 String pronunciation, String partOfSpeech, String example,
                                 String difficulty, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.vocabularyId = vocabularyId;
        this.word = word;
        this.meaning = meaning;
        this.pronunciation = pronunciation;
        this.partOfSpeech = partOfSpeech;
        this.example = example;
        this.difficulty = difficulty;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(Long vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
