package com.example.englishlearning.controller;

import com.example.englishlearning.dto.UserVocabularyProgressResponse;
import com.example.englishlearning.dto.UserVocabularyResponse;
import com.example.englishlearning.service.UserVocabularyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserVocabularyController {

    private final UserVocabularyService userVocabularyService;

    public UserVocabularyController(UserVocabularyService userVocabularyService) {
        this.userVocabularyService = userVocabularyService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vocabularies/{id}/learn")
    public ResponseEntity<Void> markVocabularyAsLearned(@PathVariable("id") Long vocabularyId,
                                                      Authentication authentication) {
        userVocabularyService.markVocabularyAsLearned(authentication.getName(), vocabularyId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/vocabularies/{id}/learn")
    public ResponseEntity<Void> removeVocabularyFromLearned(@PathVariable("id") Long vocabularyId,
                                                          Authentication authentication) {
        userVocabularyService.removeVocabularyFromLearned(authentication.getName(), vocabularyId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users/me/vocabularies")
    public ResponseEntity<List<UserVocabularyResponse>> getCurrentUserVocabularies(Authentication authentication) {
        List<UserVocabularyResponse> response = userVocabularyService.getLearnedVocabularies(authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users/me/vocabularies/progress")
    public ResponseEntity<UserVocabularyProgressResponse> getCurrentUserVocabularyProgress(Authentication authentication) {
        UserVocabularyProgressResponse response = userVocabularyService.getProgress(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
