package com.example.englishlearning;

import com.example.englishlearning.config.PdfProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(PdfProperties.class)
public class EnglishLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnglishLearningApplication.class, args);
    }
}
