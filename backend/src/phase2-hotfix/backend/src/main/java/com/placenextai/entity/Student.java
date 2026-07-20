package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "students", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 15)
    private String phone;

    @Column(length = 150)
    private String college;

    @Column(length = 80)
    private String branch;

    private Integer graduationYear;

    private Double cgpa;

    @Column(length = 500)
    private String skills;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlacementStatus placementStatus;

    private Integer placementScore;

    private Integer resumeScore;

    private Integer mockInterviewScore;

    @Column(length = 300)
    private String resumeUrl;

    @Column(length = 2000)
    private String projects;

    @Column(length = 2000)
    private String education;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.role == null) {
            this.role = "ROLE_STUDENT";
        }
        if (this.placementStatus == null) {
            this.placementStatus = PlacementStatus.NOT_PLACED;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
