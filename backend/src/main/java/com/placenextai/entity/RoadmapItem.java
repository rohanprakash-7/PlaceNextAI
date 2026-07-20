package com.placenextai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "roadmap_items",
        indexes = @Index(name = "idx_roadmap_items_roadmap", columnList = "roadmapId, weekNumber")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roadmapId;

    @Column(nullable = false)
    private int weekNumber;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 400)
    private String skillTag;

    @Column(nullable = false)
    private boolean completed;

    private LocalDateTime completedAt;
}
