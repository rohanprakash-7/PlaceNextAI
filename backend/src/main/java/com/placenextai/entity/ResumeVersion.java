package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "resume_versions",
        indexes = @Index(name = "idx_resume_versions_student", columnList = "studentId, versionNumber")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private int versionNumber;

    @Column(nullable = false, length = 200)
    private String fileName;

    @Column(nullable = false)
    private int atsScore;

    @Column(length = 1500)
    private String extractedSkills;

    @Column(length = 1000)
    private String missingKeywords;

    @Column(length = 2000)
    private String suggestions;

    private Integer wordCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
