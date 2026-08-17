package com.example.englishlearning.service;

import com.example.englishlearning.dto.VocabularyCreateRequest;
import com.example.englishlearning.dto.VocabularyResponse;
import com.example.englishlearning.dto.VocabularyUpdateRequest;
import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyImage;
import com.example.englishlearning.repository.VocabularyImageRepository;
import com.example.englishlearning.repository.VocabularyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VocabularyService {

    private static final Logger log = LoggerFactory.getLogger(VocabularyService.class);

    private final VocabularyRepository vocabularyRepository;
    private final VocabularyImageRepository vocabularyImageRepository;
    private final UnsplashService unsplashService;

    // public VocabularyService(VocabularyRepository vocabularyRepository) {
    //     this(vocabularyRepository, null, null);
    // }

    public VocabularyService(VocabularyRepository vocabularyRepository,
                            VocabularyImageRepository vocabularyImageRepository,
                            UnsplashService unsplashService) {
        this.vocabularyRepository = vocabularyRepository;
        this.vocabularyImageRepository = vocabularyImageRepository;
        this.unsplashService = unsplashService;
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

    private void ensurePrimaryImage(Vocabulary vocabulary) {
        if (vocabulary == null || vocabularyImageRepository == null || unsplashService == null) {
            return;
        }

        if (vocabularyImageRepository.findByVocabularyAndIsPrimaryTrue(vocabulary).isPresent()) {
            return;
        }

        try {
            UnsplashService.UnsplashSearchResult result = unsplashService.searchByWord(vocabulary.getWord());
            if (result == null || !result.hasImage()) {
                return;
            }

            VocabularyImage image = new VocabularyImage(
                    vocabulary,
                    "unsplash",
                    result.getProviderPhotoId(),
                    result.getImageUrl(),
                    result.getAuthorName(),
                    result.getAuthorUrl(),
                    result.getSourceUrl(),
                    true
            );
            vocabularyImageRepository.save(image);
        } catch (Exception e) {
            log.error("Unable to load Unsplash image for vocabulary '{}'", vocabulary.getWord(), e);
        }
    }

    public Vocabulary getVocabularyEntity(Long id) {
        return vocabularyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found"));
    }

    public VocabularyResponse getVocabularyById(Long id) {
        Vocabulary vocabulary = getVocabularyEntity(id);
        ensurePrimaryImage(vocabulary);
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
        String imageUrl = null;
        String authorName = null;
        String authorUrl = null;
        String sourceUrl = null;

        if (vocabularyImageRepository != null) {
            Optional<VocabularyImage> primaryImage = vocabularyImageRepository.findByVocabularyAndIsPrimaryTrue(vocabulary);
            if (primaryImage.isPresent()) {
                VocabularyImage image = primaryImage.get();
                imageUrl = image.getImageUrl();
                authorName = image.getAuthorName();
                authorUrl = image.getAuthorUrl();
                sourceUrl = image.getSourceUrl();
            }
        }

        return new VocabularyResponse(
                vocabulary.getId(),
                vocabulary.getWord(),
                vocabulary.getMeaning(),
                vocabulary.getPronunciation(),
                vocabulary.getPartOfSpeech(),
                vocabulary.getExample(),
                vocabulary.getDifficulty(),
                imageUrl,
                authorName,
                authorUrl,
                sourceUrl,
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
