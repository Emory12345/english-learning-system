package com.englishlearning.repository;

import com.englishlearning.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    List<CourseEnrollment> findByStudentId(Long studentId);
    List<CourseEnrollment> findByCourseId(Long courseId);
    CourseEnrollment findByStudentIdAndCourseId(Long studentId, Long courseId);
}