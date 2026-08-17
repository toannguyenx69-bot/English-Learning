package com.example.englishlearning.service.dictionary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DictionaryApiEntry {

    private String word;
    private List<DictionaryApiPhonetic> phonetics;
    private List<DictionaryApiMeaning> meanings;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<DictionaryApiPhonetic> getPhonetics() {
        return phonetics;
    }

    public void setPhonetics(List<DictionaryApiPhonetic> phonetics) {
        this.phonetics = phonetics;
    }

    public List<DictionaryApiMeaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<DictionaryApiMeaning> meanings) {
        this.meanings = meanings;
    }
}
