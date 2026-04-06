package com.englishlearning.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "video_watch_records")
public class VideoWatchRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    private Integer watchMinutes;

    private Integer totalMinutes;

    private Integer progress;

    private LocalDateTime lastWatchTime;

    private LocalDateTime createdAt;
}
