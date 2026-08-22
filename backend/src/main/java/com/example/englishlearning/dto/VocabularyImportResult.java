package com.example.englishlearning.dto;

public class VocabularyImportResult {

    private int importedCount;
    private int existingCount;
    private int skippedCount;
    private String message;

    public VocabularyImportResult() {
    }

    public VocabularyImportResult(int importedCount, int existingCount, int skippedCount, String message) {
        this.importedCount = importedCount;
        this.existingCount = existingCount;
        this.skippedCount = skippedCount;
        this.message = message;
    }

    public int getImportedCount() {
        return importedCount;
    }

    public void setImportedCount(int importedCount) {
        this.importedCount = importedCount;
    }

    public int getExistingCount() {
        return existingCount;
    }

    public void setExistingCount(int existingCount) {
        this.existingCount = existingCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
