package com.englishlearning.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    @GetMapping("/overview")
    public Map<String, Object> getStudentOverview() {
        Map<String, Object> overview = new HashMap<>();
        overview.put("learningTime", 0);
        overview.put("completedCourses", 0);
        overview.put("completedExercises", 0);
        overview.put("accuracy", "0%");
        return overview;
    }

    @GetMapping("/recent-studies")
    public List<Map<String, Object>> getRecentStudies() {
        List<Map<String, Object>> recentStudies = new ArrayList<>();
        return recentStudies;
    }
}
