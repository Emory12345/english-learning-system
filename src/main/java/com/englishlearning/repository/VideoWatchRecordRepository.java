package com.englishlearning.repository;

import com.englishlearning.entity.VideoWatchRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoWatchRecordRepository extends JpaRepository<VideoWatchRecord, Long> {
    List<VideoWatchRecord> findByUserIdOrderByLastWatchTimeDesc(Long userId);
    
    VideoWatchRecord findByUserIdAndVideoId(Long userId, Long videoId);
}
