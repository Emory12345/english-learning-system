package com.englishlearning.controller;

import com.englishlearning.entity.StudyRecord;
import com.englishlearning.entity.User;
import com.englishlearning.repository.StudyRecordRepository;
import com.englishlearning.repository.UserRepository;
import com.englishlearning.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study-record")
public class StudyRecordController {

    @Autowired
    private StudyRecordRepository studyRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/weekly")
    public ResponseEntity<Map<String, Object>> getWeeklyStudyRecords(
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

            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusDays(6);

            List<StudyRecord> records = studyRecordRepository.findByUserIdAndStudyDateBetween(
                    user.getId(), startDate, today);

            List<String> dates = new ArrayList<>();
            List<Integer> minutes = new ArrayList<>();

            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));

                int totalMinutes = 0;
                for (StudyRecord record : records) {
                    if (record.getStudyDate().equals(date)) {
                        totalMinutes += record.getStudyMinutes();
                    }
                }
                minutes.add(totalMinutes);
            }

            response.put("success", true);
            response.put("dates", dates);
            response.put("minutes", minutes);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学习记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addStudyRecord(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("minutes") int minutes) {
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

            LocalDate today = LocalDate.now();
            List<StudyRecord> existingRecords = studyRecordRepository.findByUserIdAndStudyDateBetween(
                    user.getId(), today, today);

            StudyRecord record;
            if (existingRecords.isEmpty()) {
                record = new StudyRecord();
                record.setUser(user);
                record.setStudyDate(today);
                record.setStudyMinutes(minutes);
            } else {
                record = existingRecords.get(0);
                record.setStudyMinutes(record.getStudyMinutes() + minutes);
            }

            studyRecordRepository.save(record);

            response.put("success", true);
            response.put("message", "学习记录添加成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "添加学习记录失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
