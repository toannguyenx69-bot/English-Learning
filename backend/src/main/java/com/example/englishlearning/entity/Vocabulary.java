package com.example.englishlearning.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "vocabularies",
        indexes = {
                @Index(name = "idx_vocabulary_word", columnList = "word"),
                @Index(name = "idx_vocabulary_difficulty", columnList = "difficulty"),
                @Index(name = "idx_vocabulary_part_of_speech", columnList = "part_of_speech")
        }
)
public class Vocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String word;

    @Column(nullable = false, length = 500)
    private String meaning;

    @Column(length = 200)
    private String pronunciation;

    @Column(name = "part_of_speech", nullable = false, length = 30)
    private String partOfSpeech;

    @Column(columnDefinition = "TEXT")
    private String example;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VocabularyImage> images = new ArrayList<>();

    @OneToOne(mappedBy = "vocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    private VocabularyPronunciation vocabularyPronunciation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Vocabulary() {
    }

    public Vocabulary(String word, String meaning, String pronunciation, String partOfSpeech,
                      String example, String difficulty) {
        this.word = word;
        this.meaning = meaning;
        this.pronunciation = pronunciation;
        this.partOfSpeech = partOfSpeech;
        this.example = example;
        this.difficulty = difficulty;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public List<VocabularyImage> getImages() {
        return images;
    }

    public void setImages(List<VocabularyImage> images) {
        this.images = images;
    }

    public VocabularyPronunciation getVocabularyPronunciation() {
        return vocabularyPronunciation;
    }

    public void setVocabularyPronunciation(VocabularyPronunciation vocabularyPronunciation) {
        this.vocabularyPronunciation = vocabularyPronunciation;
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
