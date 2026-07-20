package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "readiness_scores",
        indexes = @Index(name = "idx_readiness_student", columnList = "studentId, computedAt")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadinessScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private int totalScore;

    @Column(nullable = false)
    private int academicScore;

    @Column(nullable = false)
    private int resumeScore;

    @Column(nullable = false)
    private int skillScore;

    @Column(nullable = false)
    private int interviewScore;

    @Column(nullable = false)
    private int activityScore;

    @Column(nullable = false)
    private int feedbackAdjustment;

    @Column(nullable = false, length = 80)
    private String triggeredBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime computedAt;

    @PrePersist
    public void onCreate() {
        this.computedAt = LocalDateTime.now();
    }
}
