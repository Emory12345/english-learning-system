package com.englishlearning.controller;

import com.englishlearning.entity.Course;
import com.englishlearning.entity.CourseEnrollment;
import com.englishlearning.entity.User;
import com.englishlearning.repository.CourseEnrollmentRepository;
import com.englishlearning.repository.CourseRepository;
import com.englishlearning.service.CustomUserDetailsService;
import com.englishlearning.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    public List<Course> getCourseList() {
        return courseRepository.findByStatus("approved");
    }

    @GetMapping("/{id}")
    public Course getCourseDetail(@PathVariable Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userDetailsService.findUserByEmail(email);

        course.setTeacher(user);
        course.setStatus("pending");
        return courseRepository.save(course);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course course) {
        Course existingCourse = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        existingCourse.setName(course.getName());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setCategory(course.getCategory());
        existingCourse.setCoverImage(course.getCoverImage());
        existingCourse.setDuration(course.getDuration());
        existingCourse.setLevel(course.getLevel());
        return courseRepository.save(existingCourse);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Course deleted successfully");
        return response;
    }

    @PostMapping("/enroll/{id}")
    public CourseEnrollment enrollCourse(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User student = userDetailsService.findUserByEmail(email);

        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));

        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());

        return courseEnrollmentRepository.save(enrollment);
    }

    @PostMapping("/unenroll/{id}")
    public Map<String, String> unenrollCourse(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User student = userDetailsService.findUserByEmail(email);

        CourseEnrollment enrollment = courseEnrollmentRepository.findByStudentIdAndCourseId(student.getId(), id);
        if (enrollment != null) {
            courseEnrollmentRepository.delete(enrollment);
        }

        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "Course unenrolled successfully");
        return response;
    }

    @GetMapping("/enrolled")
    public List<CourseEnrollment> getEnrolledCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User student = userDetailsService.findUserByEmail(email);

        return courseEnrollmentRepository.findByStudentId(student.getId());
    }

    @GetMapping("/teacher")
    public List<Course> getTeacherCourses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User teacher = userDetailsService.findUserByEmail(email);

        return courseRepository.findByTeacherId(teacher.getId());
    }
}