package com.example.englishlearning;

import com.example.englishlearning.repository.GrammarQuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GrammarDataInitializationTest {

    @Autowired
    private GrammarQuestionRepository grammarQuestionRepository;

    @Test
    void defaultGrammarQuestionShouldBeSeeded() {
        assertTrue(grammarQuestionRepository.findById(1L).isPresent());
    }
}
