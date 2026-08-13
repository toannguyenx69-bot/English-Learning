package com.example.englishlearning.service;

import com.example.englishlearning.dto.VocabularyCreateRequest;
import com.example.englishlearning.dto.VocabularyResponse;
import com.example.englishlearning.dto.VocabularyUpdateRequest;
import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.repository.VocabularyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    public VocabularyService(VocabularyRepository vocabularyRepository) {
        this.vocabularyRepository = vocabularyRepository;
    }

    public VocabularyResponse createVocabulary(VocabularyCreateRequest request) {
        String normalizedWord = normalizeWord(request.getWord());
        if (vocabularyRepository.findByWordIgnoreCase(normalizedWord).isPresent()) {
            throw new IllegalArgumentException("Vocabulary already exists");
        }

        Vocabulary vocabulary = new Vocabulary(
                normalizedWord,
                request.getMeaning().trim(),
                request.getPronunciation() == null ? null : request.getPronunciation().trim(),
                normalizePartOfSpeech(request.getPartOfSpeech()),
                request.getExample() == null ? null : request.getExample().trim(),
                normalizeDifficulty(request.getDifficulty())
        );

        Vocabulary saved = vocabularyRepository.save(vocabulary);
        return toResponse(saved);
    }

    public VocabularyResponse getVocabularyById(Long id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found"));
        return toResponse(vocabulary);
    }

    public VocabularyResponse updateVocabulary(Long id, VocabularyUpdateRequest request) {
        Vocabulary existing = vocabularyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found"));

        String normalizedWord = normalizeWord(request.getWord());
        Optional<Vocabulary> duplicateByWord = vocabularyRepository.findByWordIgnoreCase(normalizedWord);
        if (duplicateByWord.isPresent() && !duplicateByWord.get().getId().equals(id)) {
            throw new IllegalArgumentException("Vocabulary already exists");
        }

        existing.setWord(normalizedWord);
        existing.setMeaning(request.getMeaning().trim());
        existing.setPronunciation(request.getPronunciation() == null ? null : request.getPronunciation().trim());
        existing.setPartOfSpeech(normalizePartOfSpeech(request.getPartOfSpeech()));
        existing.setExample(request.getExample() == null ? null : request.getExample().trim());
        existing.setDifficulty(normalizeDifficulty(request.getDifficulty()));

        Vocabulary updated = vocabularyRepository.save(existing);
        return toResponse(updated);
    }

    public void deleteVocabulary(Long id) {
        Vocabulary vocabulary = vocabularyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found"));
        vocabularyRepository.delete(vocabulary);
    }

    public Page<VocabularyResponse> getAllVocabularies(int page, int size, String sortBy, String sortDir, String query) {
        Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy == null || sortBy.isBlank() ? "word" : sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Vocabulary> vocabularies;
        if (query != null && !query.isBlank()) {
            vocabularies = vocabularyRepository.searchByKeyword(query.trim(), pageable);
        } else {
            vocabularies = vocabularyRepository.findAll(pageable);
        }

        return vocabularies.map(this::toResponse);
    }

    private VocabularyResponse toResponse(Vocabulary vocabulary) {
        return new VocabularyResponse(
                vocabulary.getId(),
                vocabulary.getWord(),
                vocabulary.getMeaning(),
                vocabulary.getPronunciation(),
                vocabulary.getPartOfSpeech(),
                vocabulary.getExample(),
                vocabulary.getDifficulty(),
                vocabulary.getCreatedAt(),
                vocabulary.getUpdatedAt()
        );
    }

    private String normalizeWord(String word) {
        return word == null ? null : word.trim();
    }

    private String normalizePartOfSpeech(String partOfSpeech) {
        return partOfSpeech == null ? null : partOfSpeech.trim().toLowerCase();
    }

    private String normalizeDifficulty(String difficulty) {
        return difficulty == null ? null : difficulty.trim();
    }
}
