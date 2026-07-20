package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "score_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double academicWeight;

    @Column(nullable = false)
    private double resumeWeight;

    @Column(nullable = false)
    private double skillWeight;

    @Column(nullable = false)
    private double interviewWeight;

    @Column(nullable = false)
    private double activityWeight;

    @Column(nullable = false)
    private int feedbackAdjustmentCap;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void onWrite() {
        this.updatedAt = LocalDateTime.now();
    }
}
