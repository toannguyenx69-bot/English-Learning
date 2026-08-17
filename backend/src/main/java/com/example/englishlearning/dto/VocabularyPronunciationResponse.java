package com.example.englishlearning.dto;

import java.util.List;

public class VocabularyPronunciationResponse {

    private Long vocabularyId;
    private String word;
    private List<PronunciationEntryResponse> pronunciations;

    public VocabularyPronunciationResponse() {
    }

    public VocabularyPronunciationResponse(Long vocabularyId, String word,
                                          List<PronunciationEntryResponse> pronunciations) {
        this.vocabularyId = vocabularyId;
        this.word = word;
        this.pronunciations = pronunciations;
    }

    public Long getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(Long vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<PronunciationEntryResponse> getPronunciations() {
        return pronunciations;
    }

    public void setPronunciations(List<PronunciationEntryResponse> pronunciations) {
        this.pronunciations = pronunciations;
    }
}
