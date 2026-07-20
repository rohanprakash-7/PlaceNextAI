package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 150)
    private String company;

    @Column(length = 120)
    private String location;

    @Column(length = 3000)
    private String description;

    @Column(length = 60)
    private String salary;

    @Column(length = 500)
    private String skillsRequired;

    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @PrePersist
    public void onCreate() {
        this.createdDate = LocalDate.now();
    }
}
