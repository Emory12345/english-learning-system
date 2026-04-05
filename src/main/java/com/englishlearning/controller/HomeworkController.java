package com.englishlearning.controller;

import com.englishlearning.entity.Homework;
import com.englishlearning.entity.HomeworkSubmission;
import com.englishlearning.entity.User;
import com.englishlearning.repository.HomeworkRepository;
import com.englishlearning.repository.HomeworkSubmissionRepository;
import com.englishlearning.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/homework")
public class HomeworkController {

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private HomeworkSubmissionRepository homeworkSubmissionRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/submit")
    public HomeworkSubmission submitHomework(@RequestBody Map<String, Object> submissionData) {
        Long homeworkId = Long.valueOf(submissionData.get("homeworkId").toString());
        String content = submissionData.get("content").toString();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User student = userDetailsService.findUserByEmail(email);

        Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new RuntimeException("Homework not found"));

        HomeworkSubmission submission = new HomeworkSubmission();
        submission.setHomework(homework);
        submission.setStudent(student);
        submission.setContent(content);
        submission.setSubmissionDate(LocalDateTime.now());
        submission.setStatus("submitted");

        return homeworkSubmissionRepository.save(submission);
    }

    @PostMapping("/grade")
    public HomeworkSubmission gradeHomework(@RequestBody Map<String, Object> gradeData) {
        Long submissionId = Long.valueOf(gradeData.get("submissionId").toString());
        int score = Integer.parseInt(gradeData.get("score").toString());
        String feedback = gradeData.get("feedback").toString();

        HomeworkSubmission submission = homeworkSubmissionRepository.findById(submissionId).orElseThrow(() -> new RuntimeException("Submission not found"));
        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus("graded");

        return homeworkSubmissionRepository.save(submission);
    }

    @GetMapping("/submissions/{homeworkId}")
    public List<HomeworkSubmission> getSubmissions(@PathVariable Long homeworkId) {
        return homeworkSubmissionRepository.findByHomeworkId(homeworkId);
    }
}