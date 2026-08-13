package com.example.englishlearning.dto;

public class UserVocabularyProgressResponse {

    private long totalLearned;
    private long totalVocabularies;
    private double progressPercent;

    public UserVocabularyProgressResponse() {
    }

    public UserVocabularyProgressResponse(long totalLearned, long totalVocabularies, double progressPercent) {
        this.totalLearned = totalLearned;
        this.totalVocabularies = totalVocabularies;
        this.progressPercent = progressPercent;
    }

    public long getTotalLearned() {
        return totalLearned;
    }

    public void setTotalLearned(long totalLearned) {
        this.totalLearned = totalLearned;
    }

    public long getTotalVocabularies() {
        return totalVocabularies;
    }

    public void setTotalVocabularies(long totalVocabularies) {
        this.totalVocabularies = totalVocabularies;
    }

    public double getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(double progressPercent) {
        this.progressPercent = progressPercent;
    }
}
