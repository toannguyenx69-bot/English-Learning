package com.example.englishlearning.controller;

import com.example.englishlearning.dto.HealthStatusDto;
import com.example.englishlearning.service.HealthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public ResponseEntity<HealthStatusDto> health() {
        HealthStatusDto status = healthService.getHealth();
        return ResponseEntity.ok(status);
    }
}
