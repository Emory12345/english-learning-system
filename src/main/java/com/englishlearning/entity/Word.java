package com.englishlearning.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;

    private String meaning;

    private String phonetic;

    private String example;

    private String level; // 难度级别

    private String category; // 单词类别
}