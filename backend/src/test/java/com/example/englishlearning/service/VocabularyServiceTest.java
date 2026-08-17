package com.example.englishlearning.service;

import com.example.englishlearning.dto.VocabularyCreateRequest;
import com.example.englishlearning.dto.VocabularyResponse;
import com.example.englishlearning.dto.VocabularyUpdateRequest;
import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyImage;
import com.example.englishlearning.repository.VocabularyImageRepository;
import com.example.englishlearning.repository.VocabularyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class VocabularyServiceTest {

    private VocabularyRepository vocabularyRepository;
    private VocabularyImageRepository vocabularyImageRepository;
    private UnsplashService unsplashService;
    private VocabularyService vocabularyService;

    @BeforeEach
    void setUp() {
        vocabularyRepository = Mockito.mock(VocabularyRepository.class);
        vocabularyImageRepository = Mockito.mock(VocabularyImageRepository.class);
        unsplashService = Mockito.mock(UnsplashService.class);
        vocabularyService = new VocabularyService(vocabularyRepository, vocabularyImageRepository, unsplashService);
    }

    @Test
    void createVocabularyReturnsResponse() {
        VocabularyCreateRequest request = new VocabularyCreateRequest();
        request.setWord("run");
        request.setMeaning("to move quickly on foot");
        request.setPronunciation("/rʌn/");
        request.setPartOfSpeech("verb");
        request.setExample("I run every morning.");
        request.setDifficulty("A1");

        Vocabulary saved = new Vocabulary("run", "to move quickly on foot", "/rʌn/", "verb", "I run every morning.", "A1");
        saved.setId(1L);
        saved.setCreatedAt(LocalDateTime.now());
        saved.setUpdatedAt(LocalDateTime.now());

        when(vocabularyRepository.findByWordIgnoreCase("run")).thenReturn(Optional.empty());
        when(vocabularyRepository.save(any(Vocabulary.class))).thenReturn(saved);

        VocabularyResponse response = vocabularyService.createVocabulary(request);

        assertEquals(1L, response.getId());
        assertEquals("run", response.getWord());
        assertEquals("verb", response.getPartOfSpeech());
    }

    @Test
    void getVocabularyByIdThrowsWhenNotFound() {
        when(vocabularyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> vocabularyService.getVocabularyById(99L));
    }

    @Test
    void updateVocabularyUpdatesExistingRecord() {
        Vocabulary existing = new Vocabulary("run", "old meaning", "/rʌn/", "verb", "old example", "A1");
        existing.setId(1L);
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        VocabularyUpdateRequest request = new VocabularyUpdateRequest();
        request.setWord("run");
        request.setMeaning("to move on foot quickly");
        request.setPronunciation("/rʌn/");
        request.setPartOfSpeech("verb");
        request.setExample("I run fast.");
        request.setDifficulty("A2");

        when(vocabularyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vocabularyRepository.findByWordIgnoreCase("run")).thenReturn(Optional.of(existing));
        when(vocabularyRepository.save(existing)).thenReturn(existing);

        VocabularyResponse response = vocabularyService.updateVocabulary(1L, request);

        assertEquals("to move on foot quickly", response.getMeaning());
        assertEquals("A2", response.getDifficulty());
    }

    @Test
    void getAllVocabulariesReturnsPagedResponse() {
        Vocabulary vocabulary = new Vocabulary("run", "to move quickly on foot", "/rʌn/", "verb", "I run.", "A1");
        vocabulary.setId(1L);
        vocabulary.setCreatedAt(LocalDateTime.now());
        vocabulary.setUpdatedAt(LocalDateTime.now());

        Page<Vocabulary> page = new PageImpl<>(List.of(vocabulary), PageRequest.of(0, 20, Sort.by("word")), 1);
        when(vocabularyRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<VocabularyResponse> result = vocabularyService.getAllVocabularies(0, 20, "word", "asc", null);

        assertEquals(1, result.getTotalElements());
        assertEquals("run", result.getContent().get(0).getWord());
    }

    @Test
    void searchByKeywordReturnsRelevantResults() {
        Vocabulary vocabulary = new Vocabulary("run", "to move quickly on foot", "/rʌn/", "verb", "I run.", "A1");
        vocabulary.setId(1L);
        vocabulary.setCreatedAt(LocalDateTime.now());
        vocabulary.setUpdatedAt(LocalDateTime.now());

        Page<Vocabulary> page = new PageImpl<>(List.of(vocabulary), PageRequest.of(0, 20, Sort.by("word")), 1);
        when(vocabularyRepository.searchByKeyword(eq("run"), any(Pageable.class))).thenReturn(page);

        Page<VocabularyResponse> result = vocabularyService.getAllVocabularies(0, 20, "word", "asc", "run");

        assertEquals(1, result.getTotalElements());
        assertEquals("run", result.getContent().get(0).getWord());
    }

    @Test
    void getVocabularyByIdReturnsExistingImageWithoutCallingUnsplash() {
        Vocabulary vocabulary = new Vocabulary("river", "a natural stream of water", "/ˈrɪv.ər/", "noun", "The river flows.", "A2");
        vocabulary.setId(1L);
        vocabulary.setCreatedAt(LocalDateTime.now());
        vocabulary.setUpdatedAt(LocalDateTime.now());

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

        when(vocabularyRepository.findById(1L)).thenReturn(Optional.of(vocabulary));
        when(vocabularyImageRepository.findByVocabularyAndIsPrimaryTrue(vocabulary)).thenReturn(Optional.of(image));

        VocabularyResponse response = vocabularyService.getVocabularyById(1L);

        assertEquals("https://images.unsplash.com/photo-123", response.getImageUrl());
        verifyNoInteractions(unsplashService);
    }

    @Test
    void getVocabularyByIdCallsUnsplashOnceWhenImageMissingAndSavesIt() {
        Vocabulary vocabulary = new Vocabulary("river", "a natural stream of water", "/ˈrɪv.ər/", "noun", "The river flows.", "A2");
        vocabulary.setId(1L);
        vocabulary.setCreatedAt(LocalDateTime.now());
        vocabulary.setUpdatedAt(LocalDateTime.now());

        UnsplashService.UnsplashSearchResult unsplashResult = new UnsplashService.UnsplashSearchResult(
                "photo_456",
                "https://unsplash.com/photos/photo_456",
                "Jane Doe",
                "https://unsplash.com/@jane",
                "https://images.unsplash.com/photo-456"
        );

        VocabularyImage savedImage = new VocabularyImage(
                vocabulary,
                "unsplash",
                "photo_456",
                "https://images.unsplash.com/photo-456",
                "Jane Doe",
                "https://unsplash.com/@jane",
                "https://unsplash.com/photos/photo_456",
                true
        );

        when(vocabularyRepository.findById(1L)).thenReturn(Optional.of(vocabulary));
        when(vocabularyImageRepository.findByVocabularyAndIsPrimaryTrue(vocabulary)).thenReturn(Optional.empty(), Optional.of(savedImage));
        when(unsplashService.searchByWord("river")).thenReturn(unsplashResult);
        when(vocabularyImageRepository.save(any(VocabularyImage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VocabularyResponse response = vocabularyService.getVocabularyById(1L);

        assertEquals("https://images.unsplash.com/photo-456", response.getImageUrl());
        verify(unsplashService).searchByWord("river");
    }

    @Test
    void getVocabularyByIdDoesNotFailWhenUnsplashLookupFails() {
        Vocabulary vocabulary = new Vocabulary("river", "a natural stream of water", "/ˈrɪv.ər/", "noun", "The river flows.", "A2");
        vocabulary.setId(1L);
        vocabulary.setCreatedAt(LocalDateTime.now());
        vocabulary.setUpdatedAt(LocalDateTime.now());

        when(vocabularyRepository.findById(1L)).thenReturn(Optional.of(vocabulary));
        when(vocabularyImageRepository.findByVocabularyAndIsPrimaryTrue(vocabulary)).thenReturn(Optional.empty());
        when(unsplashService.searchByWord("river")).thenThrow(new UnsplashService.UnsplashApiException("down"));

        VocabularyResponse response = vocabularyService.getVocabularyById(1L);

        assertEquals("river", response.getWord());
        assertNull(response.getImageUrl());
    }
}
