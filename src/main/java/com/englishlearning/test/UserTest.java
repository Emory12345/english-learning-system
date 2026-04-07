package com.englishlearning.test;

import com.englishlearning.entity.User;
import com.englishlearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

@Component
public class UserTest implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已经有足够的用户数据
        long studentCount = userRepository.countByRole("student");
        long teacherCount = userRepository.countByRole("teacher");

        // 如果学生数量不足10个，添加学生用户
        if (studentCount < 10) {
            for (int i = 1; i <= 10 - (int) studentCount; i++) {
                User student = new User();
                student.setName("学生" + (int) studentCount + i);
                student.setEmail("student" + (int) studentCount + i + "@example.com");
                student.setPassword("$2a$10$30ZZlWBTLNSo2nW5jouVO.ligAj5PTrKLtc.yAM5PI8QI2omxocJq"); // 密码: 123456
                student.setRole("student");
                student.setCreatedAt(new Timestamp(new Date().getTime()));
                student.setUpdatedAt(new Timestamp(new Date().getTime()));
                student.setStatus("active");
                student.setUsername("student" + (int) studentCount + i);
                userRepository.save(student);
            }
        }

        // 如果教师数量不足10个，添加教师用户
        if (teacherCount < 10) {
            for (int i = 1; i <= 10 - (int) teacherCount; i++) {
                User teacher = new User();
                teacher.setName("教师" + (int) teacherCount + i);
                teacher.setEmail("teacher" + (int) teacherCount + i + "@example.com");
                teacher.setPassword("$2a$10$30ZZlWBTLNSo2nW5jouVO.ligAj5PTrKLtc.yAM5PI8QI2omxocJq"); // 密码: 123456
                teacher.setRole("teacher");
                teacher.setCreatedAt(new Timestamp(new Date().getTime()));
                teacher.setUpdatedAt(new Timestamp(new Date().getTime()));
                teacher.setStatus("active");
                teacher.setUsername("teacher" + (int) teacherCount + i);
                userRepository.save(teacher);
            }
        }
    }
}