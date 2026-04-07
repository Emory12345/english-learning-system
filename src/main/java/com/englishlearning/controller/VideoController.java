package com.englishlearning.controller;

import com.englishlearning.entity.Video;
import com.englishlearning.repository.VideoRepository;
import com.englishlearning.service.CustomUserDetailsService;
import com.englishlearning.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/videos")
    public ResponseEntity<List<Video>> getVideos() {
        List<Video> videos = videoRepository.findAll();
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/videos/category/{category}")
    public ResponseEntity<List<Video>> getVideosByCategory(@PathVariable String category) {
        List<Video> videos = videoRepository.findByCategory(category);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/videos/category/{category}/type/{type}")
    public ResponseEntity<List<Video>> getVideosByCategoryAndType(@PathVariable String category, @PathVariable String type) {
        List<Video> videos = videoRepository.findByCategoryAndType(category, type);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/videos/teacher")
    public ResponseEntity<List<Video>> getTeacherVideos() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User teacher = userDetailsService.findUserByEmail(email);
        List<Video> videos = videoRepository.findByTeacherId(teacher.getId());
        return ResponseEntity.ok(videos);
    }

    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Map<String, Object>> deleteVideo(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Video video = videoRepository.findById(id).orElse(null);
            if (video == null) {
                response.put("success", false);
                response.put("message", "视频不存在");
                return ResponseEntity.badRequest().body(response);
            }

            // 删除视频文件
            String videoUrl = video.getVideoUrl();
            if (videoUrl != null && !videoUrl.isEmpty()) {
                String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(uploadDir, fileName);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }

            // 删除视频记录
            videoRepository.delete(video);

            response.put("success", true);
            response.put("message", "视频删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "视频删除失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
