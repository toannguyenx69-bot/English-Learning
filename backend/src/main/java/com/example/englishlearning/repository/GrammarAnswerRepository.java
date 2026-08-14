package com.example.englishlearning.repository;

import com.example.englishlearning.entity.GrammarAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrammarAnswerRepository extends JpaRepository<GrammarAnswer, Long> {

    Optional<GrammarAnswer> findByQuestionIdAndId(Long questionId, Long id);
}
