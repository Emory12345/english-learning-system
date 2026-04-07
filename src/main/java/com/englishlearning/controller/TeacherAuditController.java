package com.englishlearning.controller;

import com.englishlearning.entity.User;
import com.englishlearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/teachers")
public class TeacherAuditController {

    @Autowired
    private UserRepository userRepository;

    // 获取待审核教师列表
    @GetMapping("/pending")
    public List<User> getPendingTeachers() {
        return userRepository.findByRoleAndStatus("teacher", "pending");
    }

    // 批准教师
    @PostMapping("/approve/{id}")
    public Map<String, String> approveTeacher(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("教师不存在"));
        user.setStatus("active");
        userRepository.save(user);
        
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "教师批准成功");
        return response;
    }

    // 拒绝教师
    @PostMapping("/reject/{id}")
    public Map<String, String> rejectTeacher(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("教师不存在"));
        user.setStatus("rejected");
        userRepository.save(user);
        
        Map<String, String> response = new java.util.HashMap<>();
        response.put("message", "教师拒绝成功");
        return response;
    }

    // 获取教师审核统计
    @GetMapping("/statistics")
    public Map<String, Long> getTeacherStatistics() {
        long totalTeachers = userRepository.countByRole("teacher");
        long pendingTeachers = userRepository.countByRoleAndStatus("teacher", "pending");
        long approvedTeachers = userRepository.countByRoleAndStatus("teacher", "active");
        long rejectedTeachers = userRepository.countByRoleAndStatus("teacher", "rejected");
        
        Map<String, Long> statistics = new java.util.HashMap<>();
        statistics.put("totalTeachers", totalTeachers);
        statistics.put("pendingTeachers", pendingTeachers);
        statistics.put("approvedTeachers", approvedTeachers);
        statistics.put("rejectedTeachers", rejectedTeachers);
        
        return statistics;
    }
}
