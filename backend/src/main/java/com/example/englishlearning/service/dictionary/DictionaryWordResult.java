package com.example.englishlearning.service.dictionary;

import java.util.ArrayList;
import java.util.List;

public class DictionaryWordResult {

    private final String word;
    private final String usPronunciation;
    private final String ukPronunciation;
    private final String ipa;
    private final String usAudioUrl;
    private final String ukAudioUrl;
    private final List<String> definitions;
    private final List<String> examples;
    private final String source;

    public DictionaryWordResult(String word, String usPronunciation, String ukPronunciation,
                               String ipa, String usAudioUrl, String ukAudioUrl,
                               List<String> definitions, List<String> examples, String source) {
        this.word = word;
        this.usPronunciation = usPronunciation;
        this.ukPronunciation = ukPronunciation;
        this.ipa = ipa;
        this.usAudioUrl = usAudioUrl;
        this.ukAudioUrl = ukAudioUrl;
        this.definitions = definitions == null ? new ArrayList<>() : new ArrayList<>(definitions);
        this.examples = examples == null ? new ArrayList<>() : new ArrayList<>(examples);
        this.source = source;
    }

    public static DictionaryWordResult empty(String word) {
        return new DictionaryWordResult(word, null, null, null, null, null, List.of(), List.of(), "MERRIAM_WEBSTER");
    }

    public static DictionaryWordResult empty(String word, String source) {
        return new DictionaryWordResult(word, null, null, null, null, null, List.of(), List.of(), source == null ? "MERRIAM_WEBSTER" : source);
    }

    public String getWord() {
        return word;
    }

    public String getUsPronunciation() {
        return usPronunciation;
    }

    public String getUkPronunciation() {
        return ukPronunciation;
    }

    public String getIpa() {
        return ipa;
    }

    public String getUsAudioUrl() {
        return usAudioUrl;
    }

    public String getUkAudioUrl() {
        return ukAudioUrl;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public List<String> getExamples() {
        return examples;
    }

    public String getSource() {
        return source;
    }
}
