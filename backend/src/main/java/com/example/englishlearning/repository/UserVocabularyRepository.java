package com.example.englishlearning.repository;

import com.example.englishlearning.entity.User;
import com.example.englishlearning.entity.UserVocabulary;
import com.example.englishlearning.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Long> {

    Optional<UserVocabulary> findByUserAndVocabulary(User user, Vocabulary vocabulary);

    List<UserVocabulary> findByUserOrderByCreatedAtDesc(User user);

    long countByUser(User user);
}
