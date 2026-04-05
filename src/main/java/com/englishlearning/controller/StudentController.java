package com.englishlearning.controller;

import com.englishlearning.entity.CourseEnrollment;
import com.englishlearning.entity.User;
import com.englishlearning.repository.CourseEnrollmentRepository;
import com.englishlearning.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @GetMapping("/overview")
    public Map<String, Object> getStudentOverview() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User student = userDetailsService.findUserByEmail(email);

        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByStudentId(student.getId());
        int totalCourses = enrollments.size();
        int completedCourses = (int) enrollments.stream().filter(CourseEnrollment::isCompleted).count();

        Map<String, Object> overview = new java.util.HashMap<>();
        overview.put("totalCourses", totalCourses);
        overview.put("completedCourses", completedCourses);
        overview.put("inProgressCourses", totalCourses - completedCourses);

        return overview;
    }

    @GetMapping("/recent-studies")
    public List<CourseEnrollment> getRecentStudies() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User student = userDetailsService.findUserByEmail(email);

        return courseEnrollmentRepository.findByStudentId(student.getId());
    }

    @PostMapping("/learning-time")
    public Map<String, String> recordLearningTime(@RequestBody Map<String, Object> learningData) {
        String chapterId = learningData.get("chapterId").toString();
        int duration = Integer.parseInt(learningData.get("duration").toString());

        // 这里可以实现学习时间的记录逻辑
        // 暂时返回成功消息
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Learning time recorded successfully");
        return response;
    }
}