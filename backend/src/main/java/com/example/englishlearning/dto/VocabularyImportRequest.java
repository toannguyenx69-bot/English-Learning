package com.example.englishlearning.dto;

import java.util.ArrayList;
import java.util.List;

public class VocabularyImportRequest {

    private List<VocabularyImportRowDto> selectedItems = new ArrayList<>();

    public VocabularyImportRequest() {
    }

    public List<VocabularyImportRowDto> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<VocabularyImportRowDto> selectedItems) {
        this.selectedItems = selectedItems;
    }
}
