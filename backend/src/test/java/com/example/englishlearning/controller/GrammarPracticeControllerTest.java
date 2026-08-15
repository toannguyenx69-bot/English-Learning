package com.example.englishlearning.controller;

import com.example.englishlearning.dto.GrammarAnswerFeedbackResponse;
import com.example.englishlearning.dto.GrammarAnswerOptionResponse;
import com.example.englishlearning.dto.GrammarAnswerSubmissionRequest;
import com.example.englishlearning.dto.GrammarQuestionResponse;
import com.example.englishlearning.service.GrammarPracticeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GrammarPracticeController.class)
class GrammarPracticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GrammarPracticeService grammarPracticeService;

    @Test
    @WithMockUser(username = "john@example.com")
    void getGrammarQuestionReturnsQuestionWithoutCorrectAnswer() throws Exception {
        GrammarQuestionResponse response = new GrammarQuestionResponse(
                1L,
                "Choose the correct past tense form.",
                "Yesterday indicates past action.",
                "A1",
                "Past tense",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of(
                        new GrammarAnswerOptionResponse(1L, "go"),
                        new GrammarAnswerOptionResponse(2L, "went"),
                        new GrammarAnswerOptionResponse(3L, "goed")
                )
        );

        when(grammarPracticeService.getQuestion(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/grammar/questions/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getGrammarQuestionIdsReturnsAvailableIds() throws Exception {
        when(grammarPracticeService.getQuestions()).thenReturn(List.of(3L, 5L, 8L));

        mockMvc.perform(get("/api/v1/grammar/questions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[3,5,8]"));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void submitAnswerReturnsFeedback() throws Exception {
        GrammarAnswerSubmissionRequest request = new GrammarAnswerSubmissionRequest();
        request.setAnswerId(2L);

        GrammarAnswerFeedbackResponse response = new GrammarAnswerFeedbackResponse(
                true,
                "went",
                "\"Yesterday\" indicates the past tense."
        );

        when(grammarPracticeService.submitAnswer(1L, 2L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/grammar/questions/1/answer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void submitAnswerWithInvalidAnswerIdReturnsNotFound() throws Exception {
        GrammarAnswerSubmissionRequest request = new GrammarAnswerSubmissionRequest();
        request.setAnswerId(99L);

        when(grammarPracticeService.submitAnswer(1L, 99L))
                .thenThrow(new IllegalArgumentException("Answer not found"));

        mockMvc.perform(post("/api/v1/grammar/questions/1/answer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
