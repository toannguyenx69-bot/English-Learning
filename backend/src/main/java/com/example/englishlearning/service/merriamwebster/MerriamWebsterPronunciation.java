package com.example.englishlearning.service.merriamwebster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MerriamWebsterPronunciation {

    private String ipa;
    private String geo;
    private MerriamWebsterSound sound;

    public String getIpa() {
        return ipa;
    }

    public void setIpa(String ipa) {
        this.ipa = ipa;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public MerriamWebsterSound getSound() {
        return sound;
    }

    public void setSound(MerriamWebsterSound sound) {
        this.sound = sound;
    }
}
