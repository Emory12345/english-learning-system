package com.englishlearning.repository;

import com.englishlearning.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByCategory(String category);
    List<Video> findByCategoryAndType(String category, String type);
    List<Video> findByTeacherId(Long teacherId);
    List<Video> findByStatus(String status);
    List<Video> findByCategoryAndStatus(String category, String status);
    List<Video> findByCategoryAndTypeAndStatus(String category, String type, String status);
}
