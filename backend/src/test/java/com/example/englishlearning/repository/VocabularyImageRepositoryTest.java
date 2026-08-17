package com.example.englishlearning.repository;

import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyImage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class VocabularyImageRepositoryTest {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private VocabularyImageRepository vocabularyImageRepository;

    @Test
    void shouldFindPrimaryImageForVocabulary() {
        Vocabulary vocabulary = new Vocabulary(
                "river",
                "a natural stream of water",
                "/ˈrɪv.ər/",
                "noun",
                "The river flows through the valley.",
                "A2"
        );
        vocabulary = vocabularyRepository.save(vocabulary);

        VocabularyImage image = new VocabularyImage(
                vocabulary,
                "unsplash",
                "photo_123",
                "https://images.unsplash.com/photo-123",
                "Jane Doe",
                "https://unsplash.com/@jane",
                "https://unsplash.com/photos/photo_123",
                true
        );
        vocabularyImageRepository.save(image);

        Optional<VocabularyImage> primaryImage = vocabularyImageRepository.findByVocabularyAndIsPrimaryTrue(vocabulary);

        assertTrue(primaryImage.isPresent());
        assertEquals("https://images.unsplash.com/photo-123", primaryImage.get().getImageUrl());
    }
}
