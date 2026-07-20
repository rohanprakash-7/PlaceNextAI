package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recruiters", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String companyName;

    @Column(nullable = false, length = 100)
    private String recruiterName;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 100)
    private String designation;

    @Column(nullable = false, length = 30)
    private String role;

    @PrePersist
    public void onCreate() {
        if (this.role == null) {
            this.role = "ROLE_RECRUITER";
        }
    }
}
