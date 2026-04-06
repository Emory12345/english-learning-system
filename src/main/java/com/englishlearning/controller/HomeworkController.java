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

    @PostMapping("/publish")
    public Homework publishHomework(@RequestBody Map<String, Object> homeworkData) {
        String title = homeworkData.get("title").toString();
        String content = homeworkData.get("content").toString();
        String category = homeworkData.get("category").toString();
        String type = homeworkData.get("type").toString();
        String image = homeworkData.get("image") != null ? homeworkData.get("image").toString() : null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User teacher = userDetailsService.findUserByEmail(email);

        Homework homework = new Homework();
        homework.setTitle(title);
        homework.setContent(content);
        homework.setCategory(category);
        homework.setType(type);
        homework.setImage(image);
        homework.setTeacher(teacher);
        homework.setCreatedAt(LocalDateTime.now());

        return homeworkRepository.save(homework);
    }

    @GetMapping("/")
    public List<Homework> getAllHomework() {
        return homeworkRepository.findAll();
    }

    @GetMapping("/category/{category}")
    public List<Homework> getHomeworkByCategory(@PathVariable String category) {
        System.out.println("Getting homework by category: " + category);
        List<Homework> homeworks = homeworkRepository.findByCategory(category);
        System.out.println("Found " + homeworks.size() + " homeworks");
        return homeworks;
    }

    @GetMapping("/category/{category}/type/{type}")
    public List<Homework> getHomeworkByCategoryAndType(@PathVariable String category, @PathVariable String type) {
        return homeworkRepository.findByCategoryAndType(category, type);
    }

    @PostMapping("/submit")
    public HomeworkSubmission submitHomework(@RequestBody Map<String, Object> submissionData) {
        try {
            System.out.println("Submitting homework: " + submissionData);
            Long homeworkId = Long.valueOf(submissionData.get("homeworkId").toString());
            String content = submissionData.get("content").toString();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            System.out.println("Student email: " + email);
            User student = userDetailsService.findUserByEmail(email);
            System.out.println("Student: " + student);

            Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new RuntimeException("Homework not found"));
            System.out.println("Homework: " + homework);

            HomeworkSubmission submission = new HomeworkSubmission();
            submission.setHomework(homework);
            submission.setStudent(student);
            submission.setContent(content);
            submission.setSubmissionDate(LocalDateTime.now());
            submission.setStatus("submitted");

            System.out.println("Saving submission: " + submission);
            return homeworkSubmissionRepository.save(submission);
        } catch (Exception e) {
            System.err.println("Error submitting homework: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
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