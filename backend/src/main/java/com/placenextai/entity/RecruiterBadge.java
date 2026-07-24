package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "recruiter_badges",
        uniqueConstraints = @UniqueConstraint(columnNames = {"recruiterId", "badgeId"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recruiterId;

    @Column(nullable = false)
    private Long badgeId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime awardedAt;

    @PrePersist
    public void onCreate() {
        this.awardedAt = LocalDateTime.now();
    }
}
