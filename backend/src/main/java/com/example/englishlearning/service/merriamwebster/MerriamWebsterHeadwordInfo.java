package com.example.englishlearning.service.merriamwebster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MerriamWebsterHeadwordInfo {

    private String hw;
    private List<MerriamWebsterPronunciation> prs;

    public String getHw() {
        return hw;
    }

    public void setHw(String hw) {
        this.hw = hw;
    }

    public List<MerriamWebsterPronunciation> getPrs() {
        return prs;
    }

    public void setPrs(List<MerriamWebsterPronunciation> prs) {
        this.prs = prs;
    }
}
