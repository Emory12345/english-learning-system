package com.englishlearning.repository;

import com.englishlearning.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByLevel(String level);
    List<Word> findByCategory(String category);
}