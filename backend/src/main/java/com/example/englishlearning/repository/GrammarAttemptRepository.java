package com.example.englishlearning.repository;

import com.example.englishlearning.entity.GrammarAttempt;
import com.example.englishlearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrammarAttemptRepository extends JpaRepository<GrammarAttempt, Long> {

    List<GrammarAttempt> findByUserOrderByAttemptedAtDesc(User user);

    @Query("SELECT COUNT(a) FROM GrammarAttempt a WHERE a.user = :user")
    long countByUser(@Param("user") User user);

    @Query("SELECT COUNT(a) FROM GrammarAttempt a WHERE a.user = :user AND a.correct = true")
    long countCorrectByUser(@Param("user") User user);
}
