package com.englishlearning.controller;

import com.englishlearning.entity.CourseEnrollment;
import com.englishlearning.entity.HomeworkSubmission;
import com.englishlearning.repository.CourseEnrollmentRepository;
import com.englishlearning.repository.HomeworkSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/score")
public class StatisticsController {

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private HomeworkSubmissionRepository homeworkSubmissionRepository;

    @GetMapping("/student")
    public Map<String, Object> getStudentScores() {
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findAll();
        Map<String, Object> statistics = new HashMap<>();

        // 计算平均分数
        double averageScore = enrollments.stream()
                .filter(enrollment -> enrollment.getScore() != null)
                .mapToInt(CourseEnrollment::getScore)
                .average()
                .orElse(0.0);

        statistics.put("averageScore", averageScore);
        statistics.put("totalCourses", enrollments.size());

        return statistics;
    }

    @GetMapping("/course/average/{courseId}")
    public Map<String, Object> getCourseAverageScore(@PathVariable Long courseId) {
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByCourseId(courseId);
        Map<String, Object> statistics = new HashMap<>();

        double averageScore = enrollments.stream()
                .filter(enrollment -> enrollment.getScore() != null)
                .mapToInt(CourseEnrollment::getScore)
                .average()
                .orElse(0.0);

        statistics.put("averageScore", averageScore);
        statistics.put("studentCount", enrollments.size());

        return statistics;
    }

    @GetMapping("/course/distribution/{courseId}")
    public Map<String, Object> getCourseScoreDistribution(@PathVariable Long courseId) {
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByCourseId(courseId);
        Map<String, Object> distribution = new HashMap<>();

        // 计算分数分布
        int[] scoreRanges = new int[5]; // 0-60, 60-70, 70-80, 80-90, 90-100
        for (CourseEnrollment enrollment : enrollments) {
            if (enrollment.getScore() != null) {
                int score = enrollment.getScore();
                if (score < 60) scoreRanges[0]++;
                else if (score < 70) scoreRanges[1]++;
                else if (score < 80) scoreRanges[2]++;
                else if (score < 90) scoreRanges[3]++;
                else scoreRanges[4]++;
            }
        }

        distribution.put("0-60", scoreRanges[0]);
        distribution.put("60-70", scoreRanges[1]);
        distribution.put("70-80", scoreRanges[2]);
        distribution.put("80-90", scoreRanges[3]);
        distribution.put("90-100", scoreRanges[4]);

        return distribution;
    }
}