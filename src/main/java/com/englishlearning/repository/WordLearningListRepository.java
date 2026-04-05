package com.englishlearning.repository;

import com.englishlearning.entity.WordLearningList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WordLearningListRepository extends JpaRepository<WordLearningList, Long> {
    Optional<WordLearningList> findByUserIdAndWordId(Long userId, Long wordId);
    List<WordLearningList> findByUserId(Long userId);
    void deleteByUserIdAndWordId(Long userId, Long wordId);
}
