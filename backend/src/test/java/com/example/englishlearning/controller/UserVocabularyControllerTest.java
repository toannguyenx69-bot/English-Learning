package com.example.englishlearning.controller;

import com.example.englishlearning.dto.UserVocabularyProgressResponse;
import com.example.englishlearning.dto.UserVocabularyResponse;
import com.example.englishlearning.service.UserVocabularyService;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserVocabularyController.class)
class UserVocabularyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserVocabularyService userVocabularyService;

    @Test
    @WithMockUser(username = "john@example.com")
    void markVocabularyAsLearnedReturnsCreated() throws Exception {
        doNothing().when(userVocabularyService).markVocabularyAsLearned("john@example.com", 1L);

        mockMvc.perform(post("/api/v1/vocabularies/1/learn")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void removeVocabularyFromLearnedReturnsNoContent() throws Exception {
        doNothing().when(userVocabularyService).removeVocabularyFromLearned("john@example.com", 1L);

        mockMvc.perform(delete("/api/v1/vocabularies/1/learn")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getCurrentUserVocabulariesReturnsList() throws Exception {
        UserVocabularyResponse response = new UserVocabularyResponse(
                5L,
                1L,
                1L,
                "run",
                "to move quickly on foot",
                "/rʌn/",
                "verb",
                "I run every morning.",
                "A1",
                LocalDateTime.now()
        );
        when(userVocabularyService.getLearnedVocabularies("john@example.com")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/users/me/vocabularies"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(response))));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getCurrentUserVocabularyProgressReturnsProgress() throws Exception {
        UserVocabularyProgressResponse response = new UserVocabularyProgressResponse(12, 40, 30.0);
        when(userVocabularyService.getProgress("john@example.com")).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me/vocabularies/progress"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
