package com.englishlearning.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post post;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Comment parent;
}