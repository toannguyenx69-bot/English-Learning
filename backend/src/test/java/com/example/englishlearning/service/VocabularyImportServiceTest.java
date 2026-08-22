package com.example.englishlearning.service;

import com.example.englishlearning.dto.VocabularyCreateRequest;
import com.example.englishlearning.dto.VocabularyImportPreviewResponse;
import com.example.englishlearning.dto.VocabularyImportRequest;
import com.example.englishlearning.dto.VocabularyImportResult;
import com.example.englishlearning.dto.VocabularyImportRowDto;
import com.example.englishlearning.dto.VocabularyResponse;
import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.repository.VocabularyRepository;
import com.example.englishlearning.service.dictionary.DictionaryWordResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VocabularyImportServiceTest {

    private VocabularyRepository vocabularyRepository;
    private DictionaryApiService dictionaryApiService;
    private VocabularyService vocabularyService;
    private VocabularyImportService vocabularyImportService;

    @BeforeEach
    void setUp() {
        vocabularyRepository = Mockito.mock(VocabularyRepository.class);
        dictionaryApiService = Mockito.mock(DictionaryApiService.class);
        vocabularyService = Mockito.mock(VocabularyService.class);
        vocabularyImportService = new VocabularyImportService(vocabularyRepository, dictionaryApiService, vocabularyService);
    }

    @Test
    void previewExcelFileGroupsNewAndExistingRows() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "vocabulary.csv",
                "text/csv",
                "apple\norange\napple\n".getBytes()
        );

        when(vocabularyRepository.findByWordIgnoreCase("apple")).thenReturn(Optional.empty());
        when(vocabularyRepository.findByWordIgnoreCase("orange")).thenReturn(Optional.of(new Vocabulary("orange", "citrus fruit", null, "noun", null, "A1")));
        when(dictionaryApiService.searchWord("apple")).thenReturn(new DictionaryWordResult(
                "apple",
                null,
                null,
                null,
                null,
                null,
                List.of("a fruit with a round shape"),
                List.of(),
                "MERRIAM_WEBSTER"
        ));

        VocabularyImportPreviewResponse response = vocabularyImportService.previewExcelFile(file);

        assertEquals(3, response.getTotalRows());
        assertEquals(1, response.getNewItems().size());
        assertEquals(1, response.getExistingItems().size());
        assertEquals("apple", response.getNewItems().get(0).getWord());
        assertEquals("orange", response.getExistingItems().get(0).getWord());
    }

    @Test
    void importSelectedItemsImportsOnlyNewVocabulary() {
        VocabularyImportRequest request = new VocabularyImportRequest();
        request.setSelectedItems(List.of(
                new VocabularyImportRowDto(1, "apple", "NEW", "a fruit", "MERRIAM_WEBSTER", "Will be imported"),
                new VocabularyImportRowDto(2, "orange", "EXISTS", "citrus fruit", "DATABASE", "Already exists")
        ));

        when(vocabularyRepository.findByWordIgnoreCase("apple")).thenReturn(Optional.empty());
        when(vocabularyRepository.findByWordIgnoreCase("orange")).thenReturn(Optional.of(new Vocabulary("orange", "citrus fruit", null, "noun", null, "A1")));
        when(dictionaryApiService.searchWord(eq("apple"))).thenReturn(new DictionaryWordResult(
                "apple",
                null,
                null,
                null,
                null,
                null,
                List.of("a fruit"),
                List.of(),
                "MERRIAM_WEBSTER"
        ));
        when(vocabularyService.createVocabulary(any(VocabularyCreateRequest.class))).thenReturn(new VocabularyResponse(
                1L,
                "apple",
                "a fruit",
                null,
                "noun",
                null,
                "B1",
                null,
                null,
                null,
                null,
                null,
                null
        ));

        VocabularyImportResult result = vocabularyImportService.importSelectedItems(request);

        assertEquals(1, result.getImportedCount());
        assertEquals(1, result.getExistingCount());
        assertEquals(0, result.getSkippedCount());
        verify(vocabularyService).createVocabulary(any(VocabularyCreateRequest.class));
        assertNotNull(result.getMessage());
    }

    @Test
    void previewExcelFileSkipsCommonNonVocabularyLabelsBeforeDictionaryLookup() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "vocabulary.csv",
                "text/csv",
                "No.\napple\n".getBytes()
        );

        when(vocabularyRepository.findByWordIgnoreCase("apple")).thenReturn(Optional.empty());
        when(dictionaryApiService.searchWord("apple")).thenReturn(new DictionaryWordResult(
                "apple",
                null,
                null,
                null,
                null,
                null,
                List.of("a fruit"),
                List.of(),
                "MERRIAM_WEBSTER"
        ));

        VocabularyImportPreviewResponse response = vocabularyImportService.previewExcelFile(file);

        assertEquals(2, response.getTotalRows());
        assertEquals(1, response.getInvalidItems().size());
        assertEquals("No", response.getInvalidItems().get(0).getWord());
        verify(dictionaryApiService, never()).searchWord("No");
    }
}
