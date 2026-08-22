package com.example.englishlearning.dto;

public class VocabularyImportRowDto {

    private Integer rowNumber;
    private String word;
    private String status;
    private String meaning;
    private String source;
    private String message;

    public VocabularyImportRowDto() {
    }

    public VocabularyImportRowDto(Integer rowNumber, String word, String status, String meaning,
                                 String source, String message) {
        this.rowNumber = rowNumber;
        this.word = word;
        this.status = status;
        this.meaning = meaning;
        this.source = source;
        this.message = message;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
