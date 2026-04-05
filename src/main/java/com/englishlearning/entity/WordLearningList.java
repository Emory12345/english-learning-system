package com.englishlearning.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "word_learning_list")
public class WordLearningList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_learned")
    private Boolean isLearned;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word;
}