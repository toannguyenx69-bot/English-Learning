package com.example.englishlearning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vocabulary_images",
        indexes = {
                @Index(name = "idx_vocabulary_images_vocabulary_id", columnList = "vocabulary_id"),
                @Index(name = "idx_vocabulary_images_primary", columnList = "vocabulary_id, is_primary"),
                @Index(name = "idx_vocabulary_images_provider_photo", columnList = "provider, provider_photo_id")
        }
)
public class VocabularyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vocabulary_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_vocabulary_images_vocabulary"))
    private Vocabulary vocabulary;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_photo_id", length = 200)
    private String providerPhotoId;

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "author_name", length = 200)
    private String authorName;

    @Column(name = "author_url", length = 500)
    private String authorUrl;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public VocabularyImage() {
    }

    public VocabularyImage(Vocabulary vocabulary, String provider, String providerPhotoId,
                           String imageUrl, String authorName, String authorUrl,
                           String sourceUrl, boolean isPrimary) {
        this.vocabulary = vocabulary;
        this.provider = provider;
        this.providerPhotoId = providerPhotoId;
        this.imageUrl = imageUrl;
        this.authorName = authorName;
        this.authorUrl = authorUrl;
        this.sourceUrl = sourceUrl;
        this.isPrimary = isPrimary;
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderPhotoId() {
        return providerPhotoId;
    }

    public void setProviderPhotoId(String providerPhotoId) {
        this.providerPhotoId = providerPhotoId;
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

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
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
