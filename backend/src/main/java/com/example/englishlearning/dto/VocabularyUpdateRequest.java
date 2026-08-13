package com.example.englishlearning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VocabularyUpdateRequest {

    @NotBlank(message = "Word is required")
    @Size(max = 100, message = "Word must be at most 100 characters")
    private String word;

    @NotBlank(message = "Meaning is required")
    @Size(max = 500, message = "Meaning must be at most 500 characters")
    private String meaning;

    @Size(max = 200, message = "Pronunciation must be at most 200 characters")
    private String pronunciation;

    @NotBlank(message = "Part of speech is required")
    @Pattern(
            regexp = "^(noun|verb|adjective|adverb|pronoun|preposition|conjunction|interjection|phrase)$",
            message = "Part of speech must be a valid English part of speech"
    )
    private String partOfSpeech;

    @Size(max = 1000, message = "Example must be at most 1000 characters")
    private String example;

    @NotBlank(message = "Difficulty is required")
    @Pattern(
            regexp = "^(A1|A2|B1|B2|C1|C2|Beginner|Intermediate|Advanced)$",
            message = "Difficulty must be one of A1, A2, B1, B2, C1, C2, Beginner, Intermediate, or Advanced"
    )
    private String difficulty;

    public VocabularyUpdateRequest() {
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
}
