package com.englishlearning.repository;

import com.englishlearning.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findByCourseId(Long courseId);
}