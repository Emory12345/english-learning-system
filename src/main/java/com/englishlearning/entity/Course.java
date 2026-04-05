package com.englishlearning.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String category;

    private String coverImage;

    private String status; // pending, approved, rejected

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    private int duration; // 课程时长（分钟）

    private String level; // 难度级别
}