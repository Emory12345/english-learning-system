package com.englishlearning.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "word")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;

    private String meaning;

    private String chineseMeaning;

    private String phonetic;

    private String example;

    private String level;

    private String category;

    private Integer ageGroup;
}