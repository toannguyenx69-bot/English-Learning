package com.example.englishlearning.dto;

public class GrammarStatisticsResponse {

    private long totalAttempts;
    private long totalCorrect;
    private double accuracyRate;

    public GrammarStatisticsResponse() {
    }

    public GrammarStatisticsResponse(long totalAttempts, long totalCorrect, double accuracyRate) {
        this.totalAttempts = totalAttempts;
        this.totalCorrect = totalCorrect;
        this.accuracyRate = accuracyRate;
    }

    public long getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(long totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public long getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(long totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public double getAccuracyRate() {
        return accuracyRate;
    }

    public void setAccuracyRate(double accuracyRate) {
        this.accuracyRate = accuracyRate;
    }
}
