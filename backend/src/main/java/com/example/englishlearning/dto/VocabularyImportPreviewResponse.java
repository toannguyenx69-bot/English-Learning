package com.example.englishlearning.dto;

import java.util.ArrayList;
import java.util.List;

public class VocabularyImportPreviewResponse {

    private int totalRows;
    private List<VocabularyImportRowDto> newItems = new ArrayList<>();
    private List<VocabularyImportRowDto> existingItems = new ArrayList<>();
    private List<VocabularyImportRowDto> invalidItems = new ArrayList<>();

    public VocabularyImportPreviewResponse() {
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public List<VocabularyImportRowDto> getNewItems() {
        return newItems;
    }

    public void setNewItems(List<VocabularyImportRowDto> newItems) {
        this.newItems = newItems;
    }

    public List<VocabularyImportRowDto> getExistingItems() {
        return existingItems;
    }

    public void setExistingItems(List<VocabularyImportRowDto> existingItems) {
        this.existingItems = existingItems;
    }

    public List<VocabularyImportRowDto> getInvalidItems() {
        return invalidItems;
    }

    public void setInvalidItems(List<VocabularyImportRowDto> invalidItems) {
        this.invalidItems = invalidItems;
    }
}
