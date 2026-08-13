package com.example.englishlearning.controller;

import com.example.englishlearning.dto.VocabularyCreateRequest;
import com.example.englishlearning.dto.VocabularyResponse;
import com.example.englishlearning.dto.VocabularyUpdateRequest;
import com.example.englishlearning.service.VocabularyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VocabularyController.class)
class VocabularyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VocabularyService vocabularyService;

    @Test
    @WithMockUser(username = "john@example.com")
    void createVocabularyReturnsCreated() throws Exception {
        VocabularyCreateRequest request = new VocabularyCreateRequest();
        request.setWord("run");
        request.setMeaning("to move quickly on foot");
        request.setPronunciation("/rʌn/");
        request.setPartOfSpeech("verb");
        request.setExample("I run every morning.");
        request.setDifficulty("A1");

        VocabularyResponse response = new VocabularyResponse(1L, "run", "to move quickly on foot", "/rʌn/", "verb", "I run every morning.", "A1", LocalDateTime.now(), LocalDateTime.now());
        when(vocabularyService.createVocabulary(any(VocabularyCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/vocabularies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getVocabularyByIdReturnsOk() throws Exception {
        VocabularyResponse response = new VocabularyResponse(1L, "run", "to move quickly on foot", "/rʌn/", "verb", "I run every morning.", "A1", LocalDateTime.now(), LocalDateTime.now());
        when(vocabularyService.getVocabularyById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/vocabularies/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getAllVocabulariesReturnsPage() throws Exception {
        VocabularyResponse response = new VocabularyResponse(1L, "run", "to move quickly on foot", "/rʌn/", "verb", "I run every morning.", "A1", LocalDateTime.now(), LocalDateTime.now());
        Page<VocabularyResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20, Sort.by("word")), 1);
        when(vocabularyService.getAllVocabularies(eq(0), eq(20), eq("word"), eq("asc"), eq(null))).thenReturn(page);

        mockMvc.perform(get("/api/v1/vocabularies")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "word")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void updateVocabularyReturnsOk() throws Exception {
        VocabularyUpdateRequest request = new VocabularyUpdateRequest();
        request.setWord("run");
        request.setMeaning("to move quickly on foot");
        request.setPronunciation("/rʌn/");
        request.setPartOfSpeech("verb");
        request.setExample("I run every morning.");
        request.setDifficulty("A2");

        VocabularyResponse response = new VocabularyResponse(1L, "run", "to move quickly on foot", "/rʌn/", "verb", "I run every morning.", "A2", LocalDateTime.now(), LocalDateTime.now());
        when(vocabularyService.updateVocabulary(eq(1L), any(VocabularyUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/vocabularies/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void deleteVocabularyReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/vocabularies/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
