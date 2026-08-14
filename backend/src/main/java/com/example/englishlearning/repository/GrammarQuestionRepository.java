package com.example.englishlearning.repository;

import com.example.englishlearning.entity.GrammarQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrammarQuestionRepository extends JpaRepository<GrammarQuestion, Long> {
}
