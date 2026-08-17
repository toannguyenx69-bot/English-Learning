package com.example.englishlearning.repository;

import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VocabularyImageRepository extends JpaRepository<VocabularyImage, Long> {

    List<VocabularyImage> findByVocabularyOrderByCreatedAtDesc(Vocabulary vocabulary);

    Optional<VocabularyImage> findByVocabularyAndIsPrimaryTrue(Vocabulary vocabulary);

    Optional<VocabularyImage> findByProviderAndProviderPhotoId(String provider, String providerPhotoId);
}
