package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "recruiter_feedback",
        indexes = @Index(name = "idx_feedback_student", columnList = "studentId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long applicationId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long recruiterId;

    @Column(nullable = false)
    private int communicationRating;

    @Column(nullable = false)
    private int technicalRating;

    @Column(nullable = false)
    private int problemSolvingRating;

    @Column(nullable = false)
    private int cultureFitRating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeedbackOutcome outcome;

    @Column(length = 500)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
