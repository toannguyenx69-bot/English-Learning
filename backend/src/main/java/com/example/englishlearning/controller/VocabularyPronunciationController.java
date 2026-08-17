package com.example.englishlearning.controller;

import com.example.englishlearning.dto.PronunciationEntryResponse;
import com.example.englishlearning.dto.VocabularyPronunciationResponse;
import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyPronunciation;
import com.example.englishlearning.service.VocabularyPronunciationService;
import com.example.englishlearning.service.VocabularyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vocabularies")
public class VocabularyPronunciationController {

    private final VocabularyService vocabularyService;
    private final VocabularyPronunciationService vocabularyPronunciationService;

    public VocabularyPronunciationController(VocabularyService vocabularyService,
                                            VocabularyPronunciationService vocabularyPronunciationService) {
        this.vocabularyService = vocabularyService;
        this.vocabularyPronunciationService = vocabularyPronunciationService;
    }

    @GetMapping("/{id}/pronunciation")
    public ResponseEntity<VocabularyPronunciationResponse> getPronunciation(@PathVariable Long id) {
        Vocabulary vocabulary = vocabularyService.getVocabularyEntity(id);
        VocabularyPronunciation pronunciation = vocabularyPronunciationService.getOrCreatePronunciation(vocabulary);

        List<PronunciationEntryResponse> pronunciations = new ArrayList<>();
        if (pronunciation.getUsPronunciation() != null || pronunciation.getUsAudioUrl() != null) {
            pronunciations.add(new PronunciationEntryResponse(
                    "US",
                    pronunciation.getUsPronunciation(),
                    pronunciation.getUsAudioUrl()
            ));
        }
        if (pronunciation.getUkPronunciation() != null || pronunciation.getUkAudioUrl() != null) {
            pronunciations.add(new PronunciationEntryResponse(
                    "UK",
                    pronunciation.getUkPronunciation(),
                    pronunciation.getUkAudioUrl()
            ));
        }

        if (pronunciations.isEmpty()) {
            pronunciations.add(new PronunciationEntryResponse("US", null, null));
        }

        VocabularyPronunciationResponse response = new VocabularyPronunciationResponse(
                vocabulary.getId(),
                vocabulary.getWord(),
                pronunciations
        );

        return ResponseEntity.ok(response);
    }
}
