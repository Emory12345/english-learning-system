package com.englishlearning.controller;

import com.englishlearning.entity.Course;
import com.englishlearning.entity.User;
import com.englishlearning.repository.CourseRepository;
import com.englishlearning.repository.UserRepository;
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

    @GetMapping("/users")
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @DeleteMapping("/users/{id}")
    public Map<String, String> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "User deleted successfully");
        return response;
    }

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
}