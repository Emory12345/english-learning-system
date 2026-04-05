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
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");

        return response;
    }
}