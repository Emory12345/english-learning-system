package com.englishlearning.controller;

import com.englishlearning.entity.Course;
import com.englishlearning.entity.User;
import com.englishlearning.entity.Video;
import com.englishlearning.repository.CourseRepository;
import com.englishlearning.repository.UserRepository;
import com.englishlearning.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;



    @GetMapping("/courses/pending")
    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus("pending");
    }

    @PostMapping("/courses/audit")
    public Course auditCourse(@RequestBody Map<String, Object> auditData) {
        Long courseId = Long.valueOf(auditData.get("courseId").toString());
        String status = auditData.get("status").toString();

        Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        course.setStatus(status);
        return courseRepository.save(course);
    }

    @DeleteMapping("/courses/{id}")
    public Map<String, String> deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Course deleted successfully");
        return response;
    }

    // 视频审核相关
    @Autowired
    private VideoRepository videoRepository;

    @GetMapping("/videos/pending")
    public List<Video> getPendingVideos() {
        return videoRepository.findByStatus("pending");
    }

    @PostMapping("/videos/audit")
    public Video auditVideo(@RequestBody Map<String, Object> auditData) {
        Long videoId = Long.valueOf(auditData.get("videoId").toString());
        String status = auditData.get("status").toString();

        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        video.setStatus(status);
        return videoRepository.save(video);
    }

    @DeleteMapping("/videos/{id}")
    public Map<String, String> deleteVideo(@PathVariable Long id) {
        videoRepository.deleteById(id);
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Video deleted successfully");
        return response;
    }
}