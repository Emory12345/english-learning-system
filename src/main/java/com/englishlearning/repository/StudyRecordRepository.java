package com.englishlearning.repository;

import com.englishlearning.entity.StudyRecord;
import com.englishlearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
    List<StudyRecord> findByUserAndStudyDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    List<StudyRecord> findByUserIdAndStudyDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
