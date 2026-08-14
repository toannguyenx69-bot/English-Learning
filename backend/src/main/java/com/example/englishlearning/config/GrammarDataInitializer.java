package com.example.englishlearning.config;

import com.example.englishlearning.entity.GrammarAnswer;
import com.example.englishlearning.entity.GrammarQuestion;
import com.example.englishlearning.repository.GrammarAnswerRepository;
import com.example.englishlearning.repository.GrammarQuestionRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class GrammarDataInitializer implements ApplicationRunner {

    private final GrammarQuestionRepository grammarQuestionRepository;
    private final GrammarAnswerRepository grammarAnswerRepository;

    public GrammarDataInitializer(GrammarQuestionRepository grammarQuestionRepository,
                                 GrammarAnswerRepository grammarAnswerRepository) {
        this.grammarQuestionRepository = grammarQuestionRepository;
        this.grammarAnswerRepository = grammarAnswerRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (grammarQuestionRepository.count() > 0) {
            return;
        }

        createQuestion(
                "Choose the correct past tense form.",
                "\"Yesterday\" indicates the past tense.",
                "A1",
                "Past tense",
                new String[]{"go", "went", "goed", "going"},
                2
        );

        createQuestion(
                "Choose the correct sentence.",
                "Use \"has\" for a singular subject in the present perfect tense.",
                "A2",
                "Present perfect",
                new String[]{"She have finished her work.", "She has finished her work.", "She is finished her work.", "She finished has her work."},
                2
        );

        createQuestion(
                "Choose the correct article.",
                "Use \"an\" before a vowel sound.",
                "A2",
                "Articles",
                new String[]{"a", "an", "the", "no article"},
                2
        );
    }

    private void createQuestion(String questionText, String explanation, String difficulty, String topic,
                                String[] answers, int correctIndex) {
        GrammarQuestion question = new GrammarQuestion(questionText, explanation, difficulty, topic);

        for (int i = 0; i < answers.length; i++) {
            GrammarAnswer answer = new GrammarAnswer(question, answers[i], i == correctIndex);
            question.getAnswers().add(answer);
        }

        grammarQuestionRepository.save(question);
    }
}
