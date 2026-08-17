package com.example.englishlearning.repository;

import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyPronunciation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VocabularyPronunciationRepository extends JpaRepository<VocabularyPronunciation, Long> {

    Optional<VocabularyPronunciation> findByVocabulary(Vocabulary vocabulary);

    Optional<VocabularyPronunciation> findByVocabularyId(Long vocabularyId);
}
