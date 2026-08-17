package com.example.englishlearning.dto;

import java.time.LocalDateTime;

public class VocabularyResponse {

    private Long id;
    private String word;
    private String meaning;
    private String pronunciation;
    private String partOfSpeech;
    private String example;
    private String difficulty;
    private String imageUrl;
    private String authorName;
    private String authorUrl;
    private String sourceUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VocabularyResponse() {
    }

    public VocabularyResponse(Long id, String word, String meaning, String pronunciation,
                              String partOfSpeech, String example, String difficulty,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, word, meaning, pronunciation, partOfSpeech, example, difficulty,
                null, null, null, null, createdAt, updatedAt);
    }

    public VocabularyResponse(Long id, String word, String meaning, String pronunciation,
                              String partOfSpeech, String example, String difficulty,
                              String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, word, meaning, pronunciation, partOfSpeech, example, difficulty,
                imageUrl, null, null, null, createdAt, updatedAt);
    }

    public VocabularyResponse(Long id, String word, String meaning, String pronunciation,
                              String partOfSpeech, String example, String difficulty,
                              String imageUrl, String authorName, String authorUrl, String sourceUrl,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
        this.pronunciation = pronunciation;
        this.partOfSpeech = partOfSpeech;
        this.example = example;
        this.difficulty = difficulty;
        this.imageUrl = imageUrl;
        this.authorName = authorName;
        this.authorUrl = authorUrl;
        this.sourceUrl = sourceUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
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
}
