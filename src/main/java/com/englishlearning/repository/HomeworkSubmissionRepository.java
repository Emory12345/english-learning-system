package com.englishlearning.repository;

import com.englishlearning.entity.HomeworkSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HomeworkSubmissionRepository extends JpaRepository<HomeworkSubmission, Long> {
    List<HomeworkSubmission> findByHomeworkId(Long homeworkId);
    List<HomeworkSubmission> findByStudentId(Long studentId);
    HomeworkSubmission findByHomeworkIdAndStudentId(Long homeworkId, Long studentId);
    
    @Modifying
    @Transactional
    void deleteByHomeworkId(Long homeworkId);
}