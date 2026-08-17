package com.example.englishlearning.controller;

import com.example.englishlearning.dto.PronunciationEntryResponse;
import com.example.englishlearning.dto.VocabularyPronunciationResponse;
import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.entity.VocabularyPronunciation;
import com.example.englishlearning.service.VocabularyPronunciationService;
import com.example.englishlearning.service.VocabularyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VocabularyPronunciationController.class)
class VocabularyPronunciationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VocabularyService vocabularyService;

    @MockBean
    private VocabularyPronunciationService vocabularyPronunciationService;

    @Test
    @WithMockUser(username = "john@example.com")
    void getPronunciationReturnsUsAndUkEntries() throws Exception {
        Vocabulary vocabulary = new Vocabulary("apple", "a fruit", "/ˈæp.əl/", "noun", "An apple a day.", "A1");
        vocabulary.setId(123L);

        VocabularyPronunciation pronunciation = new VocabularyPronunciation(
                vocabulary,
                "/ˈæp.əl/",
                "/ˈæp.əl/",
                "/ˈæp.əl/",
                "https://example.com/us.mp3",
                "https://example.com/uk.mp3",
                "MERRIAM_WEBSTER"
        );

        VocabularyPronunciationResponse response = new VocabularyPronunciationResponse(
                123L,
                "apple",
                List.of(
                        new PronunciationEntryResponse("US", "/ˈæp.əl/", "https://example.com/us.mp3"),
                        new PronunciationEntryResponse("UK", "/ˈæp.əl/", "https://example.com/uk.mp3")
                )
        );

        when(vocabularyService.getVocabularyEntity(123L)).thenReturn(vocabulary);
        when(vocabularyPronunciationService.getOrCreatePronunciation(vocabulary)).thenReturn(pronunciation);

        mockMvc.perform(get("/api/v1/vocabularies/123/pronunciation"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getPronunciationReturnsNotFoundWhenVocabularyMissing() throws Exception {
        when(vocabularyService.getVocabularyEntity(999L))
                .thenThrow(new IllegalArgumentException("Vocabulary not found"));

        mockMvc.perform(get("/api/v1/vocabularies/999/pronunciation"))
                .andExpect(status().isNotFound());
    }
}
