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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GrammarProgressService {

    private final UserRepository userRepository;
    private final GrammarQuestionRepository grammarQuestionRepository;
    private final GrammarAnswerRepository grammarAnswerRepository;
    private final GrammarAttemptRepository grammarAttemptRepository;

    public GrammarProgressService(UserRepository userRepository,
                                 GrammarQuestionRepository grammarQuestionRepository,
                                 GrammarAnswerRepository grammarAnswerRepository,
                                 GrammarAttemptRepository grammarAttemptRepository) {
        this.userRepository = userRepository;
        this.grammarQuestionRepository = grammarQuestionRepository;
        this.grammarAnswerRepository = grammarAnswerRepository;
        this.grammarAttemptRepository = grammarAttemptRepository;
    }

    @Transactional
    public void saveAttempt(String email, Long questionId, Long selectedAnswerId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        GrammarQuestion question = grammarQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Grammar question not found"));

        GrammarAnswer selectedAnswer = grammarAnswerRepository.findByQuestionIdAndId(questionId, selectedAnswerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found"));

        boolean correct = selectedAnswer.isCorrect();
        grammarAttemptRepository.save(new GrammarAttempt(user, question, selectedAnswer, correct));
    }

    @Transactional(readOnly = true)
    public List<GrammarProgressResponse> getUserProgress(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return grammarAttemptRepository.findByUserOrderByAttemptedAtDesc(user).stream()
                .map(this::toProgressResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GrammarStatisticsResponse getUserStatistics(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        long totalAttempts = grammarAttemptRepository.countByUser(user);
        long totalCorrect = grammarAttemptRepository.countCorrectByUser(user);
        double accuracyRate = totalAttempts == 0 ? 0.0 : (totalCorrect * 100.0) / totalAttempts;

        return new GrammarStatisticsResponse(totalAttempts, totalCorrect, accuracyRate);
    }

    private GrammarProgressResponse toProgressResponse(GrammarAttempt attempt) {
        GrammarAnswer selectedAnswer = attempt.getSelectedAnswer();
        return new GrammarProgressResponse(
                attempt.getId(),
                attempt.getQuestion().getId(),
                attempt.getQuestion().getQuestion(),
                selectedAnswer.getId(),
                selectedAnswer.getAnswer(),
                attempt.isCorrect(),
                attempt.getAttemptedAt()
        );
    }
}
