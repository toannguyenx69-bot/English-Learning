package com.example.englishlearning.service;

import com.example.englishlearning.dto.GrammarAnswerFeedbackResponse;
import com.example.englishlearning.dto.GrammarAnswerOptionResponse;
import com.example.englishlearning.dto.GrammarQuestionResponse;
import com.example.englishlearning.entity.GrammarAnswer;
import com.example.englishlearning.entity.GrammarQuestion;
import com.example.englishlearning.repository.GrammarAnswerRepository;
import com.example.englishlearning.repository.GrammarQuestionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GrammarPracticeService {

    @Transactional(readOnly = true)
    public List<Long> getQuestions() {
        return grammarQuestionRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(GrammarQuestion::getId)
                .toList();
    }

    private final GrammarQuestionRepository grammarQuestionRepository;
    private final GrammarAnswerRepository grammarAnswerRepository;

    public GrammarPracticeService(GrammarQuestionRepository grammarQuestionRepository,
                                 GrammarAnswerRepository grammarAnswerRepository) {
        this.grammarQuestionRepository = grammarQuestionRepository;
        this.grammarAnswerRepository = grammarAnswerRepository;
    }

    @Transactional(readOnly = true)
    public GrammarQuestionResponse getQuestion(Long questionId) {
        GrammarQuestion question = grammarQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Grammar question not found"));

        return toResponse(question);
    }

    @Transactional
    public GrammarAnswerFeedbackResponse submitAnswer(Long questionId, Long answerId) {
        GrammarQuestion question = grammarQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Grammar question not found"));

        GrammarAnswer selectedAnswer = grammarAnswerRepository.findByQuestionIdAndId(questionId, answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found"));

        GrammarAnswer correctAnswer = question.getAnswers().stream()
                .filter(GrammarAnswer::isCorrect)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Question has no correct answer configured"));

        boolean isCorrect = selectedAnswer.getId().equals(correctAnswer.getId());

        return new GrammarAnswerFeedbackResponse(
                isCorrect,
                correctAnswer.getAnswer(),
                question.getExplanation()
        );
    }

    private GrammarQuestionResponse toResponse(GrammarQuestion question) {
        List<GrammarAnswerOptionResponse> answers = question.getAnswers().stream()
                .map(answer -> new GrammarAnswerOptionResponse(answer.getId(), answer.getAnswer()))
                .toList();

        return new GrammarQuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getExplanation(),
                question.getDifficulty(),
                question.getTopic(),
                question.getCreatedAt(),
                question.getUpdatedAt(),
                answers
        );
    }
}
