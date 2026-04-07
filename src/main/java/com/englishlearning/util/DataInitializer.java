package com.englishlearning.util;

import com.englishlearning.entity.User;
import com.englishlearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void initializeData() {
        // 检查是否已有足够的用户数据
        long studentCount = userRepository.countByRole("student");
        long teacherCount = userRepository.countByRole("teacher");

        System.out.println("当前学生数量: " + studentCount);
        System.out.println("当前教师数量: " + teacherCount);

        // 增加学生用户
        if (studentCount < 10) {
            List<User> students = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                User student = new User();
                student.setName("学生" + i);
                student.setEmail("student" + i + "@example.com");
                student.setPassword(passwordEncoder.encode("123456"));
                student.setRole("student");
                student.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                student.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                student.setStatus("active");
                student.setUsername("student" + i);
                students.add(student);
            }
            userRepository.saveAll(students);
            System.out.println("已添加10个学生用户");
        }

        // 增加教师用户
        if (teacherCount < 10) {
            List<User> teachers = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                User teacher = new User();
                teacher.setName("教师" + i);
                teacher.setEmail("teacher" + i + "@example.com");
                teacher.setPassword(passwordEncoder.encode("123456"));
                teacher.setRole("teacher");
                teacher.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                teacher.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                teacher.setStatus("active");
                teacher.setUsername("teacher" + i);
                teachers.add(teacher);
            }
            userRepository.saveAll(teachers);
            System.out.println("已添加10个教师用户");
        }
    }
}
