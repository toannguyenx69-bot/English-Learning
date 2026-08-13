package com.example.englishlearning.service;

import com.example.englishlearning.dto.HealthStatusDto;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public HealthStatusDto getHealth() {
        return new HealthStatusDto("UP");
    }
}
