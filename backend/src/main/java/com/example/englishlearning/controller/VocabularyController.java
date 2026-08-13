package com.example.englishlearning.controller;

import com.example.englishlearning.dto.VocabularyCreateRequest;
import com.example.englishlearning.dto.VocabularyResponse;
import com.example.englishlearning.dto.VocabularyUpdateRequest;
import com.example.englishlearning.service.VocabularyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vocabularies")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    public VocabularyController(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    @GetMapping
    public ResponseEntity<Page<VocabularyResponse>> getAllVocabularies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "word") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String q
    ) {
        Page<VocabularyResponse> response = vocabularyService.getAllVocabularies(page, size, sortBy, sortDir, q);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VocabularyResponse> getVocabularyById(@PathVariable Long id) {
        VocabularyResponse response = vocabularyService.getVocabularyById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<VocabularyResponse> createVocabulary(@Valid @RequestBody VocabularyCreateRequest request) {
        VocabularyResponse response = vocabularyService.createVocabulary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VocabularyResponse> updateVocabulary(@PathVariable Long id,
                                                             @Valid @RequestBody VocabularyUpdateRequest request) {
        VocabularyResponse response = vocabularyService.updateVocabulary(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVocabulary(@PathVariable Long id) {
        vocabularyService.deleteVocabulary(id);
        return ResponseEntity.noContent().build();
    }
}
