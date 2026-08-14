package com.example.englishlearning.controller;

import com.example.englishlearning.dto.GrammarProgressResponse;
import com.example.englishlearning.dto.GrammarStatisticsResponse;
import com.example.englishlearning.service.GrammarProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class GrammarProgressController {

    private final GrammarProgressService grammarProgressService;

    public GrammarProgressController(GrammarProgressService grammarProgressService) {
        this.grammarProgressService = grammarProgressService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/grammar/progress")
    public ResponseEntity<List<GrammarProgressResponse>> getUserGrammarProgress(Authentication authentication) {
        return ResponseEntity.ok(grammarProgressService.getUserProgress(authentication.getName()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/grammar/statistics")
    public ResponseEntity<GrammarStatisticsResponse> getUserGrammarStatistics(Authentication authentication) {
        return ResponseEntity.ok(grammarProgressService.getUserStatistics(authentication.getName()));
    }
}
