package com.englishlearning.controller;

import com.englishlearning.entity.User;
import com.englishlearning.entity.Video;
import com.englishlearning.entity.VideoWatchRecord;
import com.englishlearning.repository.UserRepository;
import com.englishlearning.repository.VideoRepository;
import com.englishlearning.repository.VideoWatchRecordRepository;
import com.englishlearning.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/video-watch")
public class VideoWatchRecordController {

    @Autowired
    private VideoWatchRecordRepository videoWatchRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentWatchRecords(
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(response);
            }

            List<VideoWatchRecord> records = videoWatchRecordRepository.findByUserIdOrderByLastWatchTimeDesc(user.getId());
            List<Map<String, Object>> recentStudies = new ArrayList<>();

            for (VideoWatchRecord record : records) {
                Map<String, Object> studyRecord = new HashMap<>();
                Video video = record.getVideo();
                if (video != null) {
                    studyRecord.put("courseId", video.getId());
                    studyRecord.put("courseTitle", video.getTitle());
                    studyRecord.put("lastStudyTime", record.getLastWatchTime());
                    studyRecord.put("progress", record.getProgress() + "%");
                    recentStudies.add(studyRecord);
                }
            }

            response.put("success", true);
            response.put("data", recentStudies);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取最近学习记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateWatchRecord(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("videoId") Long videoId,
            @RequestParam("watchMinutes") int watchMinutes,
            @RequestParam("totalMinutes") int totalMinutes) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmailFromToken(token);
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.badRequest().body(response);
            }

            Video video = videoRepository.findById(videoId).orElse(null);
            if (video == null) {
                response.put("success", false);
                response.put("message", "视频不存在");
                return ResponseEntity.badRequest().body(response);
            }

            VideoWatchRecord record = videoWatchRecordRepository.findByUserIdAndVideoId(user.getId(), videoId);
            if (record == null) {
                record = new VideoWatchRecord();
                record.setUser(user);
                record.setVideo(video);
                record.setCreatedAt(LocalDateTime.now());
            }

            record.setWatchMinutes(watchMinutes);
            record.setTotalMinutes(totalMinutes);
            int progress = totalMinutes > 0 ? (watchMinutes * 100) / totalMinutes : 0;
            record.setProgress(progress);
            record.setLastWatchTime(LocalDateTime.now());

            videoWatchRecordRepository.save(record);

            response.put("success", true);
            response.put("message", "观看记录更新成功");
            response.put("progress", progress + "%");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新观看记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
