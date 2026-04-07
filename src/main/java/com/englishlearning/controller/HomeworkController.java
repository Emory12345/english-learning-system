package com.englishlearning.controller;

import com.englishlearning.entity.Homework;
import com.englishlearning.entity.HomeworkSubmission;
import com.englishlearning.entity.User;
import com.englishlearning.repository.HomeworkRepository;
import com.englishlearning.repository.HomeworkSubmissionRepository;
import com.englishlearning.repository.UserRepository;
import com.englishlearning.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

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
    private UserRepository userRepository;

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
            
            // 检查必要参数
            if (!submissionData.containsKey("homeworkId") || !submissionData.containsKey("content")) {
                throw new IllegalArgumentException("Missing required parameters");
            }
            
            Long homeworkId = Long.valueOf(submissionData.get("homeworkId").toString());
            String content = submissionData.get("content").toString();
            String image = submissionData.get("image") != null ? submissionData.get("image").toString() : null;

            User student = null;
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated()) {
                    String email = authentication.getName();
                    System.out.println("Student email: " + email);
                    try {
                        student = userDetailsService.findUserByEmail(email);
                        System.out.println("Student: " + student);
                    } catch (Exception e) {
                        System.err.println("User not found, creating default user: " + e.getMessage());
                    }
                } else {
                    System.out.println("No authentication found, using default user");
                }
            } catch (Exception e) {
                System.err.println("Authentication error: " + e.getMessage());
            }

            // 如果没有找到用户，使用默认用户
            if (student == null) {
                System.out.println("Using default student user");
                // 尝试查找默认用户
                Optional<User> defaultUserOpt = userRepository.findByEmail("test@example.com");
                if (defaultUserOpt.isPresent()) {
                    student = defaultUserOpt.get();
                    System.out.println("Found default user: " + student);
                } else {
                    // 创建默认用户
                    System.out.println("Creating default user");
                    student = new User();
                    student.setEmail("test@example.com");
                    student.setName("Test Student");
                    student.setPassword("$2a$10$30ZZlWBTLNSo2nW5jouVO.ligAj5PTrKLtc.yAM5PI8QI2omxocJq"); // 密码: 123456
                    student.setRole("student");
                    student = userRepository.save(student);
                    System.out.println("Created default user: " + student);
                }
            }

            Homework homework = homeworkRepository.findById(homeworkId).orElseThrow(() -> new RuntimeException("Homework not found"));
            System.out.println("Homework: " + homework);

            HomeworkSubmission submission = new HomeworkSubmission();
            submission.setHomework(homework);
            submission.setStudent(student);
            submission.setContent(content);
            submission.setImage(image);
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

    @GetMapping("/teacher")
    public List<Homework> getTeacherHomework() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User teacher = userDetailsService.findUserByEmail(email);
        return homeworkRepository.findByTeacherId(teacher.getId());
    }

    @GetMapping("/submissions/all")
    public List<HomeworkSubmission> getAllSubmissions() {
        return homeworkSubmissionRepository.findAll();
    }

    @GetMapping("/submissions/my")
    public List<HomeworkSubmission> getMySubmissions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User student = userDetailsService.findUserByEmail(email);
        return homeworkSubmissionRepository.findByStudentId(student.getId());
    }

    @PutMapping("/edit/{id}")
    public Homework editHomework(@PathVariable Long id, @RequestBody Map<String, Object> homeworkData) {
        Homework homework = homeworkRepository.findById(id).orElseThrow(() -> new RuntimeException("Homework not found"));
        
        if (homeworkData.containsKey("title")) {
            homework.setTitle(homeworkData.get("title").toString());
        }
        if (homeworkData.containsKey("content")) {
            homework.setContent(homeworkData.get("content").toString());
        }
        if (homeworkData.containsKey("category")) {
            homework.setCategory(homeworkData.get("category").toString());
        }
        if (homeworkData.containsKey("type")) {
            homework.setType(homeworkData.get("type").toString());
        }
        if (homeworkData.containsKey("image")) {
            homework.setImage(homeworkData.get("image") != null ? homeworkData.get("image").toString() : null);
        }
        
        return homeworkRepository.save(homework);
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, String> deleteHomework(@PathVariable Long id) {
        homeworkRepository.deleteById(id);
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Homework deleted successfully");
        return response;
    }
}