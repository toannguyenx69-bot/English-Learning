package com.example.englishlearning.controller;

import com.example.englishlearning.dto.GrammarProgressResponse;
import com.example.englishlearning.dto.GrammarStatisticsResponse;
import com.example.englishlearning.service.GrammarProgressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GrammarProgressController.class)
class GrammarProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GrammarProgressService grammarProgressService;

    @Test
    @WithMockUser(username = "john@example.com")
    void getUserGrammarProgressReturnsList() throws Exception {
        GrammarProgressResponse response = new GrammarProgressResponse(
                1L,
                5L,
                "Choose the correct past tense form.",
                2L,
                "went",
                true,
                LocalDateTime.now()
        );

        when(grammarProgressService.getUserProgress("john@example.com")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/users/me/grammar/progress"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getUserGrammarStatisticsReturnsSummary() throws Exception {
        GrammarStatisticsResponse response = new GrammarStatisticsResponse(8, 6, 75.0);
        when(grammarProgressService.getUserStatistics("john@example.com")).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me/grammar/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
