package com.englishlearning.repository;

import com.englishlearning.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByCategory(String category);
    List<Video> findByCategoryAndType(String category, String type);
}
