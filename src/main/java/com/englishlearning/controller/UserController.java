package com.englishlearning.controller;

import com.englishlearning.entity.User;
import com.englishlearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@RestController
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getUsers() {
        // 获取所有学生和教师用户，排除管理员
        return userRepository.findByRoleIn(Arrays.asList("student", "teacher"));
    }

    @DeleteMapping("/users/{id}")
    public Map<String, String> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return response;
    }
    
    @PostMapping("/users/{id}")
    public Map<String, String> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        // 更新用户信息
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        
        // 只有当密码字段不为空时才更新密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        
        existingUser.setRole(user.getRole());
        existingUser.setStatus(user.getStatus());
        
        userRepository.save(existingUser);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User updated successfully");
        return response;
    }

    // 用户统计相关
    @GetMapping("/users/stats")
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总用户数
        long totalUsers = userRepository.count();
        
        // 按角色分布
        long studentCount = userRepository.countByRole("student");
        long teacherCount = userRepository.countByRole("teacher");
        long adminCount = userRepository.countByRole("admin");
        
        stats.put("totalUsers", totalUsers);
        stats.put("studentCount", studentCount);
        stats.put("teacherCount", teacherCount);
        stats.put("adminCount", adminCount);
        
        return stats;
    }

    // 用户增长趋势（从数据库获取真实数据）
    @GetMapping("/users/growth")
    public Map<String, Object> getUserGrowth() {
        Map<String, Object> growth = new HashMap<>();
        
        // 计算一周前的日期
        java.sql.Timestamp oneWeekAgo = new java.sql.Timestamp(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
        
        // 从数据库获取近一周的用户增长数据（按角色分类）
        List<Object[]> result = userRepository.countByRoleAndCreatedAtDate(oneWeekAgo);
        
        // 处理结果
        List<String> dates = new ArrayList<>();
        List<Long> newStudents = new ArrayList<>();
        List<Long> newTeachers = new ArrayList<>();
        
        // 初始化近7天的日期列表，确保每天都有数据
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
            dates.add(date.toString());
            newStudents.add(0L); // 默认为0
            newTeachers.add(0L); // 默认为0
        }
        
        // 填充实际数据
        for (Object[] row : result) {
            java.sql.Date date = (java.sql.Date) row[0];
            String role = (String) row[1];
            Long count = (Long) row[2];
            int index = dates.indexOf(date.toString());
            if (index != -1) {
                if ("student".equals(role)) {
                    newStudents.set(index, count);
                } else if ("teacher".equals(role)) {
                    newTeachers.set(index, count);
                }
            }
        }
        
        growth.put("dates", dates);
        growth.put("newStudents", newStudents);
        growth.put("newTeachers", newTeachers);
        
        return growth;
    }
}
