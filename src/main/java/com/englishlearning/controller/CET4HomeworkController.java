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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/cet4-homework")
public class CET4HomeworkController {

    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private HomeworkSubmissionRepository homeworkSubmissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/submit")
    public Map<String, Object> submitCET4Homework(@RequestBody Map<String, Object> submissionData) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("Submitting CET4 homework: " + submissionData);
            
            Long homeworkId = Long.valueOf(submissionData.get("homeworkId").toString());
            String studentAnswer = submissionData.get("studentAnswer").toString();
            String writingImage = submissionData.get("writingImage") != null ? submissionData.get("writingImage").toString() : null;
            String translationImage = submissionData.get("translationImage") != null ? submissionData.get("translationImage").toString() : null;
            String paperContent = submissionData.get("paperContent") != null ? submissionData.get("paperContent").toString() : null;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // 检查用户是否已登录
            if ("anonymousUser".equals(email)) {
                response.put("success", false);
                response.put("message", "请先登录系统");
                return response;
            }
            
            User student = userDetailsService.findUserByEmail(email);

            // 尝试查找作业，如果找不到则创建一个新的作业
            Homework homework = homeworkRepository.findById(homeworkId).orElse(null);
            if (homework == null) {
                // 创建一个新的作业
                homework = new Homework();
                homework.setId(homeworkId);
                homework.setTitle("2022年9月四级真题");
                homework.setContent("2022年9月四级真题内容");
                homework.setCategory("cet4");
                homework.setType("real_test");
                homework.setCreatedAt(LocalDateTime.now());
                homework.setFullScore(710);
                // 保存到数据库
                homework = homeworkRepository.save(homework);
            }

            // 构建图片数据JSON
            Map<String, String> imageData = new HashMap<>();
            if (writingImage != null) {
                imageData.put("writingImage", writingImage);
            }
            if (translationImage != null) {
                imageData.put("translationImage", translationImage);
            }
            String imageJson = imageData.isEmpty() ? null : new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(imageData);

            HomeworkSubmission submission = new HomeworkSubmission();
            submission.setHomework(homework);
            submission.setStudent(student);
            submission.setContent(studentAnswer);
            submission.setImage(imageJson);
            submission.setPaperContent(paperContent);
            submission.setSubmissionDate(LocalDateTime.now());
            submission.setStatus("submitted");

            submission = homeworkSubmissionRepository.save(submission);

            response.put("success", true);
            response.put("message", "作业提交成功");
            response.put("submissionId", submission.getId());
            response.put("status", "submitted");

            return response;
        } catch (Exception e) {
            System.err.println("Error submitting CET4 homework: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "提交失败: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/my-submissions")
    public Map<String, Object> getMyCET4Submissions() {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // 检查用户是否已登录
            if ("anonymousUser".equals(email)) {
                response.put("success", false);
                response.put("message", "请先登录系统");
                return response;
            }
            
            User student = userDetailsService.findUserByEmail(email);
            
            List<HomeworkSubmission> submissions = homeworkSubmissionRepository.findByStudentId(student.getId());
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (HomeworkSubmission submission : submissions) {
                Homework homework = submission.getHomework();
                if (homework != null && "cet4".equals(homework.getCategory())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", submission.getId());
                    map.put("homeworkId", homework.getId());
                    map.put("title", homework.getTitle());
                    map.put("content", submission.getContent());
                    map.put("image", submission.getImage());
                    map.put("submissionDate", submission.getSubmissionDate());
                    map.put("status", submission.getStatus());
                    map.put("score", submission.getScore());
                    map.put("feedback", submission.getFeedback());
                    result.add(map);
                }
            }
            
            response.put("success", true);
            response.put("submissions", result);
            return response;
        } catch (Exception e) {
            System.err.println("Error getting CET4 submissions: " + e.getMessage());
            response.put("success", false);
            response.put("message", "获取提交记录失败: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/teacher/submissions")
    public Map<String, Object> getTeacherCET4Submissions() {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            System.out.println("Teacher email: " + email);
            
            // 检查用户是否已登录
            if ("anonymousUser".equals(email)) {
                response.put("success", false);
                response.put("message", "请先登录系统");
                System.out.println("User is anonymous");
                return response;
            }
            
            User teacher = userDetailsService.findUserByEmail(email);
            System.out.println("Teacher found: " + teacher.getName());
            
            // 获取所有四级作业
            List<Homework> allHomeworks = homeworkRepository.findByCategory("cet4");
            System.out.println("Number of CET4 homeworks: " + allHomeworks.size());
            
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Homework homework : allHomeworks) {
                System.out.println("Processing homework: " + homework.getId() + " - " + homework.getTitle());
                List<HomeworkSubmission> submissions = homeworkSubmissionRepository.findByHomeworkId(homework.getId());
                System.out.println("Number of submissions for homework " + homework.getId() + ": " + submissions.size());
                
                for (HomeworkSubmission submission : submissions) {
                    System.out.println("Processing submission: " + submission.getId() + " from student " + submission.getStudent().getName());
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", submission.getId());
                    map.put("homeworkId", homework.getId());
                    map.put("homeworkTitle", homework.getTitle());
                    map.put("studentName", submission.getStudent().getName());
                    map.put("studentEmail", submission.getStudent().getEmail());
                    map.put("studentAnswer", submission.getContent());
                    map.put("paperContent", submission.getPaperContent());
                    
                    // 处理图片数据
                    if (submission.getImage() != null) {
                        try {
                            Map<String, String> imageData = new com.fasterxml.jackson.databind.ObjectMapper().readValue(submission.getImage(), Map.class);
                            if (imageData.containsKey("writingImage")) {
                                map.put("writingImage", imageData.get("writingImage"));
                            }
                            if (imageData.containsKey("translationImage")) {
                                map.put("translationImage", imageData.get("translationImage"));
                            }
                            // 保留subjectiveImage字段以保持兼容性
                            map.put("subjectiveImage", submission.getImage());
                        } catch (Exception e) {
                            // 如果解析失败，直接返回原始image数据
                            map.put("subjectiveImage", submission.getImage());
                        }
                    }
                    
                    map.put("submissionDate", submission.getSubmissionDate());
                    map.put("status", submission.getStatus());
                    map.put("score", submission.getScore());
                    map.put("feedback", submission.getFeedback());
                    result.add(map);
                }
            }
            
            System.out.println("Total submissions found: " + result.size());
            response.put("success", true);
            response.put("submissions", result);
            return response;
        } catch (Exception e) {
            System.err.println("Error getting teacher CET4 submissions: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "获取作业提交列表失败: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/grade")
    public Map<String, Object> gradeCET4Homework(@RequestBody Map<String, Object> gradeData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long submissionId = Long.valueOf(gradeData.get("submissionId").toString());
            int score = Integer.parseInt(gradeData.get("score").toString());
            String feedback = gradeData.get("feedback") != null ? gradeData.get("feedback").toString() : "";

            HomeworkSubmission submission = homeworkSubmissionRepository.findById(submissionId)
                    .orElseThrow(() -> new RuntimeException("Submission not found"));
            
            submission.setScore(score);
            submission.setFeedback(feedback);
            submission.setStatus("graded");

            homeworkSubmissionRepository.save(submission);

            response.put("success", true);
            response.put("message", "评分成功");
            return response;
        } catch (Exception e) {
            System.err.println("Error grading CET4 homework: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "评分失败: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/check-submitted/{homeworkId}")
    public Map<String, Object> checkIfSubmitted(@PathVariable Long homeworkId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // 检查用户是否已登录
            if ("anonymousUser".equals(email)) {
                response.put("submitted", false);
                response.put("message", "请先登录系统");
                return response;
            }
            
            User student = userDetailsService.findUserByEmail(email);
            
            List<HomeworkSubmission> submissions = homeworkSubmissionRepository.findByStudentId(student.getId());
            boolean submitted = false;
            HomeworkSubmission existingSubmission = null;
            
            for (HomeworkSubmission submission : submissions) {
                if (submission.getHomework() != null && 
                    submission.getHomework().getId().equals(homeworkId)) {
                    submitted = true;
                    existingSubmission = submission;
                    break;
                }
            }
            
            response.put("submitted", submitted);
            if (existingSubmission != null) {
                response.put("status", existingSubmission.getStatus());
                response.put("score", existingSubmission.getScore());
                response.put("submissionId", existingSubmission.getId());
            }
            
            return response;
        } catch (Exception e) {
            System.err.println("Error checking submission: " + e.getMessage());
            response.put("submitted", false);
            return response;
        }
    }

    @GetMapping("/answer/{homeworkId}")
    public Map<String, Object> getHomeworkAnswer(@PathVariable Long homeworkId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Homework homework = homeworkRepository.findById(homeworkId)
                    .orElseThrow(() -> new RuntimeException("Homework not found"));
            
            response.put("success", true);
            response.put("answer", homework.getContent());
            response.put("fullScore", homework.getFullScore());
            
            return response;
        } catch (Exception e) {
            System.err.println("Error getting homework answer: " + e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
}