package com.example.englishlearning.repository;

import com.example.englishlearning.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    Optional<Vocabulary> findByWordIgnoreCase(String word);

    @Query("SELECT v FROM Vocabulary v WHERE " +
            "LOWER(v.word) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.meaning) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Vocabulary> searchByKeyword(@Param("query") String query, Pageable pageable);
}
