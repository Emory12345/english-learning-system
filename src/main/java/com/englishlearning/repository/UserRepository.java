package com.englishlearning.repository;

import com.englishlearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRole(String role);
    List<User> findByRoleAndStatus(String role, String status);
    long countByRole(String role);
    long countByRoleAndStatus(String role, String status);
}