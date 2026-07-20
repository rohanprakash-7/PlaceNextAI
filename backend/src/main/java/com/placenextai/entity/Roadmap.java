package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "roadmaps",
        indexes = @Index(name = "idx_roadmaps_student", columnList = "studentId, status")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Roadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(length = 150)
    private String targetCompany;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoadmapStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = RoadmapStatus.ACTIVE;
        }
    }
}
