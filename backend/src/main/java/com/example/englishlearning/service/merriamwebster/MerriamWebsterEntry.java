package com.example.englishlearning.service.merriamwebster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MerriamWebsterEntry {

    private MerriamWebsterMeta meta;
    private MerriamWebsterHeadwordInfo hwi;
    private List<String> shortdef;

    public MerriamWebsterMeta getMeta() {
        return meta;
    }

    public void setMeta(MerriamWebsterMeta meta) {
        this.meta = meta;
    }

    public String getWord() {
        return hwi != null ? hwi.getHw() : (meta != null ? meta.getId() : null);
    }

    public MerriamWebsterHeadwordInfo getHwi() {
        return hwi;
    }

    public void setHwi(MerriamWebsterHeadwordInfo hwi) {
        this.hwi = hwi;
    }

    public List<String> getShortdef() {
        return shortdef;
    }

    public void setShortdef(List<String> shortdef) {
        this.shortdef = shortdef;
    }
}
