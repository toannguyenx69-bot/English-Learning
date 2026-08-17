package com.example.englishlearning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vocabulary_pronunciations",
        indexes = {
                @Index(name = "idx_vocabulary_pronunciations_vocabulary_id", columnList = "vocabulary_id"),
                @Index(name = "idx_vocabulary_pronunciations_source", columnList = "source")
        }
)
public class VocabularyPronunciation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vocabulary_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vocabulary_pronunciations_vocabulary"))
    private Vocabulary vocabulary;

    @Column(name = "us_pronunciation", length = 200)
    private String usPronunciation;

    @Column(name = "uk_pronunciation", length = 200)
    private String ukPronunciation;

    @Column(name = "ipa", length = 200)
    private String ipa;

    @Column(name = "us_audio_url", length = 1000)
    private String usAudioUrl;

    @Column(name = "uk_audio_url", length = 1000)
    private String ukAudioUrl;

    @Column(name = "source", length = 50)
    private String source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public VocabularyPronunciation() {
    }

    public VocabularyPronunciation(Vocabulary vocabulary, String usPronunciation, String ukPronunciation,
                                  String ipa, String usAudioUrl, String ukAudioUrl, String source) {
        this.vocabulary = vocabulary;
        this.usPronunciation = usPronunciation;
        this.ukPronunciation = ukPronunciation;
        this.ipa = ipa;
        this.usAudioUrl = usAudioUrl;
        this.ukAudioUrl = ukAudioUrl;
        this.source = source;
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

    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public String getUsPronunciation() {
        return usPronunciation;
    }

    public void setUsPronunciation(String usPronunciation) {
        this.usPronunciation = usPronunciation;
    }

    public String getUkPronunciation() {
        return ukPronunciation;
    }

    public void setUkPronunciation(String ukPronunciation) {
        this.ukPronunciation = ukPronunciation;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getUsAudioUrl() {
        return usAudioUrl;
    }

    public void setUsAudioUrl(String usAudioUrl) {
        this.usAudioUrl = usAudioUrl;
    }

    public String getUkAudioUrl() {
        return ukAudioUrl;
    }

    public void setUkAudioUrl(String ukAudioUrl) {
        this.ukAudioUrl = ukAudioUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
