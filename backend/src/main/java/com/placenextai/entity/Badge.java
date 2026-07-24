package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badges", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String code;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false, length = 40)
    private String icon;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false)
    private Integer xpReward;
}
