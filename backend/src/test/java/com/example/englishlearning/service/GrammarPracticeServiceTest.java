package com.example.englishlearning.service;

import com.example.englishlearning.dto.GrammarAnswerFeedbackResponse;
import com.example.englishlearning.dto.GrammarQuestionResponse;
import com.example.englishlearning.entity.GrammarAnswer;
import com.example.englishlearning.entity.GrammarQuestion;
import com.example.englishlearning.repository.GrammarAnswerRepository;
import com.example.englishlearning.repository.GrammarQuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GrammarPracticeServiceTest {

    private GrammarQuestionRepository grammarQuestionRepository;
    private GrammarAnswerRepository grammarAnswerRepository;
    private GrammarPracticeService grammarPracticeService;

    @BeforeEach
    void setUp() {
        grammarQuestionRepository = mock(GrammarQuestionRepository.class);
        grammarAnswerRepository = mock(GrammarAnswerRepository.class);
        grammarPracticeService = new GrammarPracticeService(grammarQuestionRepository, grammarAnswerRepository);
    }

    @Test
    void getQuestionReturnsAnswersWithoutCorrectFlag() {
        GrammarQuestion question = new GrammarQuestion(
                "Choose the correct past tense form.",
                "\"Yesterday\" indicates the past tense.",
                "A1",
                "Past tense"
        );
        question.setId(1L);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        GrammarAnswer answer1 = new GrammarAnswer(question, "go", false);
        answer1.setId(1L);
        GrammarAnswer answer2 = new GrammarAnswer(question, "went", true);
        answer2.setId(2L);
        question.setAnswers(List.of(answer1, answer2));

        when(grammarQuestionRepository.findById(1L)).thenReturn(Optional.of(question));

        GrammarQuestionResponse response = grammarPracticeService.getQuestion(1L);

        assertEquals(1L, response.getId());
        assertEquals("Choose the correct past tense form.", response.getQuestion());
        assertEquals(2, response.getAnswers().size());
        assertEquals("go", response.getAnswers().get(0).getAnswer());
        assertEquals("went", response.getAnswers().get(1).getAnswer());
    }

    @Test
    void submitAnswerReturnsCorrectFeedbackAndExplanation() {
        GrammarQuestion question = new GrammarQuestion(
                "Choose the correct past tense form.",
                "\"Yesterday\" indicates the past tense.",
                "A1",
                "Past tense"
        );
        question.setId(1L);

        GrammarAnswer option1 = new GrammarAnswer(question, "go", false);
        option1.setId(1L);
        GrammarAnswer correctOption = new GrammarAnswer(question, "went", true);
        correctOption.setId(2L);
        question.setAnswers(List.of(option1, correctOption));

        when(grammarQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(grammarAnswerRepository.findByQuestionIdAndId(1L, 2L)).thenReturn(Optional.of(correctOption));

        GrammarAnswerFeedbackResponse response = grammarPracticeService.submitAnswer(1L, 2L);

        assertNotNull(response);
        assertEquals(true, response.isCorrect());
        assertEquals("went", response.getCorrectAnswer());
        assertEquals("\"Yesterday\" indicates the past tense.", response.getExplanation());
    }

    @Test
    void submitAnswerReturnsIncorrectWhenUserSelectsWrongOption() {
        GrammarQuestion question = new GrammarQuestion(
                "Choose the correct past tense form.",
                "\"Yesterday\" indicates the past tense.",
                "A1",
                "Past tense"
        );
        question.setId(1L);

        GrammarAnswer wrongOption = new GrammarAnswer(question, "go", false);
        wrongOption.setId(1L);
        GrammarAnswer correctOption = new GrammarAnswer(question, "went", true);
        correctOption.setId(2L);
        question.setAnswers(List.of(wrongOption, correctOption));

        when(grammarQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(grammarAnswerRepository.findByQuestionIdAndId(1L, 1L)).thenReturn(Optional.of(wrongOption));

        GrammarAnswerFeedbackResponse response = grammarPracticeService.submitAnswer(1L, 1L);

        assertFalse(response.isCorrect());
        assertEquals("went", response.getCorrectAnswer());
    }

    @Test
    void submitAnswerThrowsWhenAnswerDoesNotBelongToQuestion() {
        GrammarQuestion question = new GrammarQuestion(
                "Choose the correct past tense form.",
                "\"Yesterday\" indicates the past tense.",
                "A1",
                "Past tense"
        );
        question.setId(1L);

        when(grammarQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(grammarAnswerRepository.findByQuestionIdAndId(1L, 99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> grammarPracticeService.submitAnswer(1L, 99L));
    }
}
