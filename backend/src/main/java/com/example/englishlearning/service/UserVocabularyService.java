package com.example.englishlearning.service;

import com.example.englishlearning.dto.UserVocabularyProgressResponse;
import com.example.englishlearning.dto.UserVocabularyResponse;
import com.example.englishlearning.entity.User;
import com.example.englishlearning.entity.UserVocabulary;
import com.example.englishlearning.entity.Vocabulary;
import com.example.englishlearning.repository.UserRepository;
import com.example.englishlearning.repository.UserVocabularyRepository;
import com.example.englishlearning.repository.VocabularyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserVocabularyService {

    private final UserRepository userRepository;
    private final VocabularyRepository vocabularyRepository;
    private final UserVocabularyRepository userVocabularyRepository;

    public UserVocabularyService(UserRepository userRepository,
                                VocabularyRepository vocabularyRepository,
                                UserVocabularyRepository userVocabularyRepository) {
        this.userRepository = userRepository;
        this.vocabularyRepository = vocabularyRepository;
        this.userVocabularyRepository = userVocabularyRepository;
    }

    @Transactional
    public void markVocabularyAsLearned(String email, Long vocabularyId) {
        User user = findUserByEmail(email);
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found"));

        if (userVocabularyRepository.findByUserAndVocabulary(user, vocabulary).isPresent()) {
            throw new IllegalArgumentException("Vocabulary is already marked as learned");
        }

        userVocabularyRepository.save(new UserVocabulary(user, vocabulary));
    }

    @Transactional
    public void removeVocabularyFromLearned(String email, Long vocabularyId) {
        User user = findUserByEmail(email);
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found"));

        UserVocabulary relation = userVocabularyRepository.findByUserAndVocabulary(user, vocabulary)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary is not in your learned list"));

        userVocabularyRepository.delete(relation);
    }

    @Transactional(readOnly = true)
    public List<UserVocabularyResponse> getLearnedVocabularies(String email) {
        User user = findUserByEmail(email);
        return userVocabularyRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserVocabularyProgressResponse getProgress(String email) {
        User user = findUserByEmail(email);
        long totalLearned = userVocabularyRepository.countByUser(user);
        long totalVocabularies = vocabularyRepository.count();

        double progressPercent = totalVocabularies == 0 ? 0.0 : (totalLearned * 100.0) / totalVocabularies;
        return new UserVocabularyProgressResponse(totalLearned, totalVocabularies, progressPercent);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private UserVocabularyResponse toResponse(UserVocabulary relation) {
        Vocabulary vocabulary = relation.getVocabulary();
        return new UserVocabularyResponse(
                relation.getId(),
                relation.getUser().getId(),
                vocabulary.getId(),
                vocabulary.getWord(),
                vocabulary.getMeaning(),
                vocabulary.getPronunciation(),
                vocabulary.getPartOfSpeech(),
                vocabulary.getExample(),
                vocabulary.getDifficulty(),
                relation.getCreatedAt()
        );
    }
}
