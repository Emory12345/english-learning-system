package com.englishlearning.repository;

import com.englishlearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRole(String role);
    List<User> findByRoleAndStatus(String role, String status);
    List<User> findByRoleIn(List<String> roles);
    long countByRole(String role);
    long countByRoleAndStatus(String role, String status);
    
    // 按日期统计每天的新用户数量
    @Query("SELECT DATE(u.createdAt) as date, COUNT(*) as count FROM User u WHERE u.createdAt >= :startDate GROUP BY DATE(u.createdAt) ORDER BY date")
    List<Object[]> countByCreatedAtDate(@Param("startDate") java.sql.Timestamp startDate);
    
    // 按角色和日期统计每天的新用户数量
    @Query("SELECT DATE(u.createdAt) as date, u.role as role, COUNT(*) as count FROM User u WHERE u.createdAt >= :startDate GROUP BY DATE(u.createdAt), u.role ORDER BY date, role")
    List<Object[]> countByRoleAndCreatedAtDate(@Param("startDate") java.sql.Timestamp startDate);
}