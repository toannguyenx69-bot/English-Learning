package com.example.englishlearning.dto;

public class PronunciationEntryResponse {

    private String accent;
    private String ipa;
    private String audioUrl;

    public PronunciationEntryResponse() {
    }

    public PronunciationEntryResponse(String accent, String ipa, String audioUrl) {
        this.accent = accent;
        this.ipa = ipa;
        this.audioUrl = audioUrl;
    }

    public String getAccent() {
        return accent;
    }

    public void setAccent(String accent) {
        this.accent = accent;
    }

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
