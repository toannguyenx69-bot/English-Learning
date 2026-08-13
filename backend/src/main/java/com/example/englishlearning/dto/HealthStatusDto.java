package com.example.englishlearning.dto;

public class HealthStatusDto {
    private String status;

    public HealthStatusDto() {
    }

    public HealthStatusDto(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
