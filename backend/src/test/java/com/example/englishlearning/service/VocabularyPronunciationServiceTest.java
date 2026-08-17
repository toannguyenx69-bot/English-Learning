package com.example.englishlearning.service;

import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyPronunciation;
import com.example.englishlearning.repository.VocabularyPronunciationRepository;
import com.example.englishlearning.repository.VocabularyRepository;
import com.example.englishlearning.service.dictionary.DictionaryWordResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VocabularyPronunciationServiceTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private VocabularyPronunciationRepository vocabularyPronunciationRepository;

    @Mock
    private DictionaryApiService dictionaryApiService;

    @InjectMocks
    private VocabularyPronunciationService service;

    @Test
    void shouldReturnCachedPronunciationWithoutCallingDictionaryApi() {
        Vocabulary vocabulary = new Vocabulary("river", "a natural stream of water", "/ˈrɪv.ər/", "noun",
                "The river flows.", "A2");
        VocabularyPronunciation cached = new VocabularyPronunciation(
                vocabulary,
                "/ˈrɪv.ər/",
                "/ˈrɪv.ɚ/",
                "/ˈrɪv.ər/",
                "https://example.com/us.mp3",
                "https://example.com/uk.mp3",
                "db_cache"
        );

        when(vocabularyPronunciationRepository.findByVocabulary(vocabulary)).thenReturn(java.util.Optional.of(cached));

        VocabularyPronunciation result = service.getOrCreatePronunciation(vocabulary);

        assertNotNull(result);
        assertEquals("db_cache", result.getSource());
        verify(dictionaryApiService, never()).searchWord(anyString());
    }

    @Test
    void shouldLookUpAndSaveDictionaryPronunciationWhenMissing() {
        Vocabulary vocabulary = new Vocabulary("river", "a natural stream of water", "/ˈrɪv.ər/", "noun",
                "The river flows.", "A2");
        DictionaryWordResult dictionaryResult = new DictionaryWordResult(
                "river",
                "/ˈrɪv.ər/",
                "/ˈrɪv.ɚ/",
                "/ˈrɪv.ər/",
                "https://example.com/us.mp3",
                "https://example.com/uk.mp3",
                java.util.List.of("a natural stream of water"),
                java.util.List.of("The river flows."),
                "MERRIAM_WEBSTER"
        );

        when(vocabularyPronunciationRepository.findByVocabulary(vocabulary)).thenReturn(java.util.Optional.empty());
        when(dictionaryApiService.searchWord("river")).thenReturn(dictionaryResult);
        when(vocabularyPronunciationRepository.save(org.mockito.ArgumentMatchers.any(VocabularyPronunciation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        VocabularyPronunciation result = service.getOrCreatePronunciation(vocabulary);

        assertNotNull(result);
        assertEquals("/ˈrɪv.ər/", result.getUsPronunciation());
        assertEquals("https://example.com/us.mp3", result.getUsAudioUrl());
        assertEquals("MERRIAM_WEBSTER", result.getSource());
    }

    @Test
    void shouldHandleDictionaryApiFailureGracefully() {
        Vocabulary vocabulary = new Vocabulary("planet", "a large celestial body", null, "noun",
                "The planet is bright.", "A2");

        when(vocabularyPronunciationRepository.findByVocabulary(vocabulary)).thenReturn(java.util.Optional.empty());
        when(dictionaryApiService.searchWord("planet")).thenThrow(new RuntimeException("network failure"));
        when(vocabularyPronunciationRepository.save(org.mockito.ArgumentMatchers.any(VocabularyPronunciation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        VocabularyPronunciation result = service.getOrCreatePronunciation(vocabulary);

        assertNotNull(result);
        assertNull(result.getUsPronunciation());
        assertNull(result.getUsAudioUrl());
        assertEquals("MERRIAM_WEBSTER", result.getSource());
    }

    @Test
    void shouldHandleMissingAudioGracefully() {
        Vocabulary vocabulary = new Vocabulary("glimmer", "shine faintly", null, "verb",
                "The stars glimmer.", "B1");
        DictionaryWordResult dictionaryResult = new DictionaryWordResult(
                "glimmer",
                null,
                null,
                "/ˈɡlɪm.ər/",
                null,
                null,
                java.util.List.of("shine faintly"),
                java.util.List.of("The stars glimmer."),
                "MERRIAM_WEBSTER"
        );

        when(vocabularyPronunciationRepository.findByVocabulary(vocabulary)).thenReturn(java.util.Optional.empty());
        when(dictionaryApiService.searchWord("glimmer")).thenReturn(dictionaryResult);
        when(vocabularyPronunciationRepository.save(org.mockito.ArgumentMatchers.any(VocabularyPronunciation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        VocabularyPronunciation result = service.getOrCreatePronunciation(vocabulary);

        assertNotNull(result);
        assertEquals("/ˈɡlɪm.ər/", result.getIpa());
        assertNull(result.getUsAudioUrl());
        assertNull(result.getUkAudioUrl());
    }

    @Test
    void shouldHandleMissingIpaGracefully() {
        Vocabulary vocabulary = new Vocabulary("whisper", "speak very quietly", null, "verb",
                "She whispered softly.", "B1");
        DictionaryWordResult dictionaryResult = new DictionaryWordResult(
                "whisper",
                null,
                null,
                null,
                "https://example.com/us.mp3",
                "https://example.com/uk.mp3",
                java.util.List.of("speak very quietly"),
                java.util.List.of("She whispered softly."),
                "MERRIAM_WEBSTER"
        );

        when(vocabularyPronunciationRepository.findByVocabulary(vocabulary)).thenReturn(java.util.Optional.empty());
        when(dictionaryApiService.searchWord("whisper")).thenReturn(dictionaryResult);
        when(vocabularyPronunciationRepository.save(org.mockito.ArgumentMatchers.any(VocabularyPronunciation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        VocabularyPronunciation result = service.getOrCreatePronunciation(vocabulary);

        assertNotNull(result);
        assertNull(result.getIpa());
        assertEquals("https://example.com/us.mp3", result.getUsAudioUrl());
    }

    @Test
    void shouldPreventDuplicatePronunciationSave() {
        Vocabulary vocabulary = new Vocabulary("river", "a natural stream of water", "/ˈrɪv.ər/", "noun",
                "The river flows.", "A2");
        VocabularyPronunciation existing = new VocabularyPronunciation(
                vocabulary,
                "/ˈrɪv.ər/",
                "/ˈrɪv.ɚ/",
                "/ˈrɪv.ər/",
                "https://example.com/us.mp3",
                "https://example.com/uk.mp3",
                "db_cache"
        );

        when(vocabularyPronunciationRepository.findByVocabulary(vocabulary)).thenReturn(java.util.Optional.of(existing));

        VocabularyPronunciation result = service.getOrCreatePronunciation(vocabulary);

        assertEquals(existing, result);
        verify(vocabularyPronunciationRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
