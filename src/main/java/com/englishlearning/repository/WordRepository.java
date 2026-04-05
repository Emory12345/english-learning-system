package com.englishlearning.repository;

import com.englishlearning.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByLevel(String level);
    List<Word> findByCategory(String category);
    List<Word> findByAgeGroup(Integer ageGroup);
    Page<Word> findByAgeGroup(Integer ageGroup, Pageable pageable);
    Page<Word> findByAgeGroupAndMeaningNotNull(Integer ageGroup, Pageable pageable);
    Page<Word> findByAgeGroupIn(List<Integer> ageGroups, Pageable pageable);
}