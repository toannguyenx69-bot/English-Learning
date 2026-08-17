package com.example.englishlearning.repository;

import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyPronunciation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class VocabularyPronunciationRepositoryTest {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private VocabularyPronunciationRepository vocabularyPronunciationRepository;

    @Test
    void shouldFindPronunciationForVocabulary() {
        Vocabulary vocabulary = new Vocabulary(
                "river",
                "a natural stream of water",
                "/ˈrɪv.ər/",
                "noun",
                "The river flows through the valley.",
                "A2"
        );
        vocabulary = vocabularyRepository.save(vocabulary);

        VocabularyPronunciation pronunciation = new VocabularyPronunciation(
                vocabulary,
                "/ˈrɪv.ər/",
                "/ˈrɪv.ɚ/",
                "/ˈrɪv.ər/",
                "https://example.com/us.mp3",
                "https://example.com/uk.mp3",
                "free_dictionary"
        );
        vocabularyPronunciationRepository.save(pronunciation);

        Optional<VocabularyPronunciation> savedPronunciation =
                vocabularyPronunciationRepository.findByVocabularyId(vocabulary.getId());

        assertTrue(savedPronunciation.isPresent());
        assertEquals("free_dictionary", savedPronunciation.get().getSource());
        assertEquals("https://example.com/us.mp3", savedPronunciation.get().getUsAudioUrl());
    }
}
