package com.example.englishlearning.service;

import com.example.englishlearning.dto.GrammarProgressResponse;
import com.example.englishlearning.dto.GrammarStatisticsResponse;
import com.example.englishlearning.entity.GrammarAnswer;
import com.example.englishlearning.entity.GrammarAttempt;
import com.example.englishlearning.entity.GrammarQuestion;
import com.example.englishlearning.entity.User;
import com.example.englishlearning.repository.GrammarAnswerRepository;
import com.example.englishlearning.repository.GrammarAttemptRepository;
import com.example.englishlearning.repository.GrammarQuestionRepository;
import com.example.englishlearning.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GrammarProgressServiceTest {

    private UserRepository userRepository;
    private GrammarQuestionRepository grammarQuestionRepository;
    private GrammarAnswerRepository grammarAnswerRepository;
    private GrammarAttemptRepository grammarAttemptRepository;
    private GrammarProgressService grammarProgressService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        grammarQuestionRepository = mock(GrammarQuestionRepository.class);
        grammarAnswerRepository = mock(GrammarAnswerRepository.class);
        grammarAttemptRepository = mock(GrammarAttemptRepository.class);
        grammarProgressService = new GrammarProgressService(
                userRepository,
                grammarQuestionRepository,
                grammarAnswerRepository,
                grammarAttemptRepository
        );
    }

    @Test
    void saveAttemptSavesCorrectUserAnswerTracking() {
        User user = new User("john", "john@example.com", "password");
        user.setId(1L);

        GrammarQuestion question = new GrammarQuestion(
                "Choose the correct past tense form.",
                "\"Yesterday\" indicates the past tense.",
                "A1",
                "Past tense"
        );
        question.setId(5L);

        GrammarAnswer selectedAnswer = new GrammarAnswer(question, "went", true);
        selectedAnswer.setId(2L);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(grammarQuestionRepository.findById(5L)).thenReturn(Optional.of(question));
        when(grammarAnswerRepository.findByQuestionIdAndId(5L, 2L)).thenReturn(Optional.of(selectedAnswer));

        grammarProgressService.saveAttempt("john@example.com", 5L, 2L);
    }

    @Test
    void getUserProgressReturnsAttemptHistory() {
        User user = new User("john", "john@example.com", "password");
        user.setId(1L);

        GrammarQuestion question = new GrammarQuestion(
                "Choose the correct past tense form.",
                "\"Yesterday\" indicates the past tense.",
                "A1",
                "Past tense"
        );
        question.setId(5L);

        GrammarAnswer selectedAnswer = new GrammarAnswer(question, "went", true);
        selectedAnswer.setId(2L);

        GrammarAttempt attempt = new GrammarAttempt(user, question, selectedAnswer, true);
        attempt.setId(1L);
        attempt.setAttemptedAt(LocalDateTime.now());

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(grammarAttemptRepository.findByUserOrderByAttemptedAtDesc(user)).thenReturn(List.of(attempt));

        List<GrammarProgressResponse> progress = grammarProgressService.getUserProgress("john@example.com");

        assertEquals(1, progress.size());
        assertEquals(5L, progress.get(0).getQuestionId());
        assertEquals("went", progress.get(0).getSelectedAnswerText());
    }

    @Test
    void getUserStatisticsCalculatesAccuracy() {
        User user = new User("john", "john@example.com", "password");
        user.setId(1L);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(grammarAttemptRepository.countByUser(user)).thenReturn(8L);
        when(grammarAttemptRepository.countCorrectByUser(user)).thenReturn(6L);

        GrammarStatisticsResponse statistics = grammarProgressService.getUserStatistics("john@example.com");

        assertEquals(8L, statistics.getTotalAttempts());
        assertEquals(6L, statistics.getTotalCorrect());
        assertEquals(75.0, statistics.getAccuracyRate());
    }

    @Test
    void saveAttemptThrowsWhenSelectedAnswerDoesNotBelongToQuestion() {
        User user = new User("john", "john@example.com", "password");
        user.setId(1L);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(grammarQuestionRepository.findById(5L)).thenReturn(Optional.of(new GrammarQuestion()));
        when(grammarAnswerRepository.findByQuestionIdAndId(5L, 99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> grammarProgressService.saveAttempt("john@example.com", 5L, 99L));
    }
}
