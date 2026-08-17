package com.example.englishlearning.service.dictionary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DictionaryApiMeaning {

    private String partOfSpeech;
    private List<DictionaryApiDefinition> definitions;

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public List<DictionaryApiDefinition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<DictionaryApiDefinition> definitions) {
        this.definitions = definitions;
    }
}
