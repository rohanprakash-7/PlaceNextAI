package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String username;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String role;

    @PrePersist
    public void onCreate() {
        if (this.role == null) {
            this.role = "ROLE_ADMIN";
        }
    }
}
