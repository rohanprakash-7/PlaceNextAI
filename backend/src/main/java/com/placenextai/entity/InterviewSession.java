package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "interview_sessions",
        indexes = @Index(name = "idx_interview_session_student", columnList = "studentId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(length = 150)
    private String targetCompany;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewSessionStatus status;

    private Integer overallScore;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @PrePersist
    public void onCreate() {
        this.startedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = InterviewSessionStatus.ACTIVE;
        }
    }
}
