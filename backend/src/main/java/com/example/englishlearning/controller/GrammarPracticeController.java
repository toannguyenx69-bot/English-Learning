package com.example.englishlearning.controller;

import com.example.englishlearning.dto.GrammarAnswerFeedbackResponse;
import com.example.englishlearning.dto.GrammarAnswerSubmissionRequest;
import com.example.englishlearning.dto.GrammarQuestionResponse;
import com.example.englishlearning.service.GrammarPracticeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/grammar")
public class GrammarPracticeController {

    private final GrammarPracticeService grammarPracticeService;

    public GrammarPracticeController(GrammarPracticeService grammarPracticeService) {
        this.grammarPracticeService = grammarPracticeService;
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<GrammarQuestionResponse> getQuestion(@PathVariable("id") Long questionId) {
        GrammarQuestionResponse response = grammarPracticeService.getQuestion(questionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/questions/{id}/answer")
    public ResponseEntity<GrammarAnswerFeedbackResponse> submitAnswer(
            @PathVariable("id") Long questionId,
            @Valid @RequestBody GrammarAnswerSubmissionRequest request) {
        GrammarAnswerFeedbackResponse response = grammarPracticeService.submitAnswer(questionId, request.getAnswerId());
        return ResponseEntity.ok(response);
    }
}
