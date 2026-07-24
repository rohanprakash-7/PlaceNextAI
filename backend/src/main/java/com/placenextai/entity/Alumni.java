package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alumni", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alumni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 150)
    private String currentCompany;

    @Column(length = 100)
    private String designation;

    private Integer graduationYear;

    @Column(length = 500)
    private String expertise;

    @Column(length = 1000)
    private String bio;

    @Column(length = 300)
    private String linkedinUrl;

    @Column(length = 500)
    private String profileImageUrl;

    private Integer yearsOfExperience;

    @Column(nullable = false, length = 30)
    private String role;

    @PrePersist
    public void onCreate() {
        if (this.role == null) {
            this.role = "ROLE_ALUMNI";
        }
    }
}
