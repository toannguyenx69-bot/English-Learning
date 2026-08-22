package com.example.englishlearning.service;

import com.example.englishlearning.dto.VocabularyCreateRequest;
import com.example.englishlearning.dto.VocabularyImportPreviewResponse;
import com.example.englishlearning.dto.VocabularyImportRequest;
import com.example.englishlearning.dto.VocabularyImportResult;
import com.example.englishlearning.dto.VocabularyImportRowDto;
import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.repository.VocabularyRepository;
import com.example.englishlearning.service.dictionary.DictionaryWordResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VocabularyImportService {

    private final VocabularyRepository vocabularyRepository;
    private final DictionaryApiService dictionaryApiService;
    private final VocabularyService vocabularyService;

    public VocabularyImportService(VocabularyRepository vocabularyRepository,
                                  DictionaryApiService dictionaryApiService,
                                  VocabularyService vocabularyService) {
        this.vocabularyRepository = vocabularyRepository;
        this.dictionaryApiService = dictionaryApiService;
        this.vocabularyService = vocabularyService;
    }

    public VocabularyImportPreviewResponse previewExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an Excel file.");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || (!originalName.toLowerCase().endsWith(".xlsx") && !originalName.toLowerCase().endsWith(".csv"))) {
            throw new IllegalArgumentException("Only .xlsx or .csv files are allowed.");
        }

        List<String> values = extractValues(file);
        VocabularyImportPreviewResponse response = new VocabularyImportPreviewResponse();
        response.setTotalRows(values.size());

        List<VocabularyImportRowDto> newItems = new ArrayList<>();
        List<VocabularyImportRowDto> existingItems = new ArrayList<>();
        List<VocabularyImportRowDto> invalidItems = new ArrayList<>();

        int rowIndex = 1;
        Set<String> seen = new HashSet<>();
        for (String rawValue : values) {
            String value = normalizeWord(rawValue);
            if (value.isBlank()) {
                invalidItems.add(new VocabularyImportRowDto(rowIndex, rawValue, "INVALID", null, null, "Empty value"));
                rowIndex++;
                continue;
            }

            if (!isVocabularyCandidate(value)) {
                invalidItems.add(new VocabularyImportRowDto(rowIndex, value, "INVALID", null, null, "Not a valid vocabulary entry"));
                rowIndex++;
                continue;
            }

            if (!seen.add(value.toLowerCase())) {
                invalidItems.add(new VocabularyImportRowDto(rowIndex, value, "INVALID", null, null, "Duplicate in uploaded file"));
                rowIndex++;
                continue;
            }

            Optional<Vocabulary> existing = vocabularyRepository.findByWordIgnoreCase(value);
            if (existing.isPresent()) {
                existingItems.add(new VocabularyImportRowDto(
                        rowIndex,
                        value,
                        "EXISTS",
                        existing.get().getMeaning(),
                        "DATABASE",
                        "Already exists in the vocabulary database"
                ));
                rowIndex++;
                continue;
            }

            DictionaryWordResult dictionaryResult = dictionaryApiService.searchWord(value);
            String meaning = dictionaryResult != null && dictionaryResult.getDefinitions() != null && !dictionaryResult.getDefinitions().isEmpty()
                    ? String.join("; ", dictionaryResult.getDefinitions())
                    : "";

            newItems.add(new VocabularyImportRowDto(
                    rowIndex,
                    value,
                    "NEW",
                    meaning,
                    dictionaryResult == null ? "UNKNOWN" : dictionaryResult.getSource(),
                    meaning.isBlank() ? "No meaning returned from Merriam-Webster" : "Will be imported as new vocabulary"
            ));
            rowIndex++;
        }

        response.setNewItems(newItems);
        response.setExistingItems(existingItems);
        response.setInvalidItems(invalidItems);
        return response;
    }

    public VocabularyImportResult importSelectedItems(VocabularyImportRequest request) {
        if (request == null || request.getSelectedItems() == null || request.getSelectedItems().isEmpty()) {
            throw new IllegalArgumentException("No vocabulary items selected for import.");
        }

        int importedCount = 0;
        int existingCount = 0;
        int skippedCount = 0;

        for (VocabularyImportRowDto row : request.getSelectedItems()) {
            if (row == null || row.getWord() == null || row.getWord().isBlank()) {
                skippedCount++;
                continue;
            }

            String word = normalizeWord(row.getWord());
            if (!isVocabularyCandidate(word)) {
                skippedCount++;
                continue;
            }

            if (vocabularyRepository.findByWordIgnoreCase(word).isPresent()) {
                existingCount++;
                continue;
            }

            DictionaryWordResult dictionaryResult = dictionaryApiService.searchWord(word);
            String meaning = dictionaryResult != null && dictionaryResult.getDefinitions() != null && !dictionaryResult.getDefinitions().isEmpty()
                    ? String.join("; ", dictionaryResult.getDefinitions())
                    : (row.getMeaning() == null || row.getMeaning().isBlank() ? "Imported from Excel" : row.getMeaning());

            VocabularyCreateRequest createRequest = new VocabularyCreateRequest();
            createRequest.setWord(word);
            createRequest.setMeaning(meaning);
            createRequest.setPronunciation(null);
            createRequest.setPartOfSpeech("noun");
            createRequest.setExample(null);
            createRequest.setDifficulty("B1");

            vocabularyService.createVocabulary(createRequest);
            importedCount++;
        }

        return new VocabularyImportResult(
                importedCount,
                existingCount,
                skippedCount,
                "Imported " + importedCount + " new vocabulary items."
        );
    }

    private List<String> extractValues(MultipartFile file) {
        try {
            if (file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                String content = new String(file.getBytes());
                List<String> values = new ArrayList<>();
                String[] lines = content.split("\\r?\\n");
                for (String line : lines) {
                    if (line == null || line.isBlank()) {
                        continue;
                    }
                    String[] columns = line.split(",");
                    if (columns.length == 0) {
                        continue;
                    }
                    values.add(columns[0].trim());
                }
                return values;
            }

            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                List<String> values = new ArrayList<>();
                for (Row row : sheet) {
                    if (row == null) {
                        continue;
                    }
                    Cell firstCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (firstCell == null) {
                        continue;
                    }
                    String value = switch (firstCell.getCellType()) {
                        case STRING -> firstCell.getStringCellValue();
                        case NUMERIC -> String.valueOf(firstCell.getNumericCellValue());
                        case BOOLEAN -> String.valueOf(firstCell.getBooleanCellValue());
                        default -> firstCell.toString();
                    };
                    values.add(value);
                }
                return values;
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to read the uploaded Excel file.", ex);
        }
    }

    private String normalizeWord(String value) {
        if (value == null) {
            return "";
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        normalized = normalized.replaceAll("^[^\\p{L}]+", "");
        normalized = normalized.replaceAll("[^\\p{L}]+$", "");
        return normalized;
    }

    private boolean isVocabularyCandidate(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String normalized = normalizeWord(value);
        if (normalized.isBlank()) {
            return false;
        }

        if (normalized.matches(".*\\d.*")) {
            return false;
        }

        if (normalized.length() < 2) {
            return false;
        }

        return normalized.matches("[\\p{L}][\\p{L}\\p{M}' -]*");
    }
}
