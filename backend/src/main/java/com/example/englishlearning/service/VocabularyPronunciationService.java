package com.example.englishlearning.service;

import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyPronunciation;
import com.example.englishlearning.repository.VocabularyPronunciationRepository;
import com.example.englishlearning.repository.VocabularyRepository;
import com.example.englishlearning.service.dictionary.DictionaryWordResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VocabularyPronunciationService {

    private static final Logger log = LoggerFactory.getLogger(VocabularyPronunciationService.class);

    private final VocabularyRepository vocabularyRepository;
    private final VocabularyPronunciationRepository vocabularyPronunciationRepository;
    private final DictionaryApiService dictionaryApiService;

    public VocabularyPronunciationService(VocabularyRepository vocabularyRepository,
                                          VocabularyPronunciationRepository vocabularyPronunciationRepository,
                                          DictionaryApiService dictionaryApiService) {
        this.vocabularyRepository = vocabularyRepository;
        this.vocabularyPronunciationRepository = vocabularyPronunciationRepository;
        this.dictionaryApiService = dictionaryApiService;
    }

    public VocabularyPronunciation getOrCreatePronunciation(Vocabulary vocabulary) {
        if (vocabulary == null) {
            throw new IllegalArgumentException("Vocabulary is required");
        }

        Optional<VocabularyPronunciation> existing = vocabularyPronunciationRepository.findByVocabulary(vocabulary);
        if (existing.isPresent()) {
            return existing.get();
        }

        DictionaryWordResult result = fetchFromDictionary(vocabulary.getWord());
        VocabularyPronunciation pronunciation = toEntity(vocabulary, result);
        return vocabularyPronunciationRepository.save(pronunciation);
    }

    public VocabularyPronunciation getOrCreatePronunciation(String word) {
        String normalizedWord = normalizeWord(word);
        Vocabulary vocabulary = vocabularyRepository.findByWordIgnoreCase(normalizedWord)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found for word: " + normalizedWord));

        return getOrCreatePronunciation(vocabulary);
    }

    private DictionaryWordResult fetchFromDictionary(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("Word is required");
        }

        try {
            return dictionaryApiService.searchWord(word.trim());
        } catch (Exception e) {
            log.warn("Dictionary lookup failed for word '{}'; saving empty pronunciation record.", word, e);
            return DictionaryWordResult.empty(word.trim());
        }
    }

    private VocabularyPronunciation toEntity(Vocabulary vocabulary, DictionaryWordResult result) {
        if (result == null) {
            result = DictionaryWordResult.empty(vocabulary.getWord());
        }

        return new VocabularyPronunciation(
                vocabulary,
                result.getUsPronunciation(),
                result.getUkPronunciation(),
                result.getIpa(),
                result.getUsAudioUrl(),
                result.getUkAudioUrl(),
                result.getSource()
        );
    }

    private String normalizeWord(String word) {
        return word == null ? null : word.trim();
    }
}
