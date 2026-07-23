package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_badges",
        uniqueConstraints = @UniqueConstraint(columnNames = {"studentId", "badgeId"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long badgeId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime awardedAt;

    @PrePersist
    public void onCreate() {
        this.awardedAt = LocalDateTime.now();
    }
}
