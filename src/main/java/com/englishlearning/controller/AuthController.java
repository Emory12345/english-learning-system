package com.englishlearning.controller;

import com.englishlearning.entity.User;
import com.englishlearning.repository.UserRepository;
import com.englishlearning.service.CustomUserDetailsService;
import com.englishlearning.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        String role = credentials.get("role");

        System.out.println("Login attempt: email=" + email + ", password=" + password + ", role=" + role);

        try {
            User user = userDetailsService.findUserByEmail(email);

            System.out.println("User found: " + user);
            if (user != null) {
                System.out.println("User password: " + user.getPassword());
                System.out.println("User role: " + user.getRole());
                System.out.println("Password match: " + password.equals(user.getPassword()));
                System.out.println("Role match: " + user.getRole().equals(role));
            }

            // 直接使用数据库中的密码进行登录，不进行任何加密
            if (password.equals(user.getPassword()) && user.getRole().equals(role)) {
                // 检查用户状态，只有active状态的用户才能登录
                if (!"active".equals(user.getStatus())) {
                    if ("pending".equals(user.getStatus())) {
                        throw new RuntimeException("您的账号正在审核中，请等待管理员批准");
                    } else {
                        throw new RuntimeException("您的账号已被禁用");
                    }
                }
                
                System.out.println("Generating token...");
                String token = jwtUtil.generateToken(email, role);
                System.out.println("Token generated: " + token);
                
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("name", user.getName());
                userInfo.put("email", user.getEmail());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("role", role);
                response.put("userInfo", userInfo);

                return response;
            } else {
                System.out.println("Invalid credentials");
                throw new RuntimeException("Invalid credentials");
            }
        } catch (UsernameNotFoundException e) {
            System.out.println("User not found: " + e.getMessage());
            throw new RuntimeException("Invalid credentials");
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Internal server error");
        }
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, String> userData) {
        String name = userData.get("name");
        String email = userData.get("email");
        String password = userData.get("password");
        String role = userData.get("role");

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password); // 直接保存明文密码，不加密
        user.setRole(role);
        
        // 确保设置username字段，因为数据库表中username是NOT NULL
        if ("teacher".equals(role)) {
            user.setStatus("pending");
            // 保存教师专用字段
            user.setUsername(userData.get("username"));
            user.setGender(userData.get("gender"));
            user.setSubject(userData.get("subject"));
            user.setEducation(userData.get("education"));
            user.setExperience(userData.get("experience"));
            user.setIntroduction(userData.get("introduction"));
            user.setCertificate(userData.get("certificate"));
        } else {
            user.setStatus("active");
            // 为学生角色设置默认username（使用邮箱前缀）
            String defaultUsername = email.split("@")[0];
            user.setUsername(defaultUsername);
        }

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        if ("teacher".equals(role)) {
            response.put("message", "教师注册成功，等待管理员审核");
        } else {
            response.put("message", "注册成功，请登录");
        }

        return response;
    }
}