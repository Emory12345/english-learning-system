package com.englishlearning.repository;

import com.englishlearning.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStatus(String status);
    List<Course> findByTeacherId(Long teacherId);
    List<Course> findByCategory(String category);
    List<Course> findByLevel(String level);
}