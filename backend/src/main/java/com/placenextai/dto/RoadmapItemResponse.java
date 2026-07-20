package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapItemResponse {

    private Long id;
    private int weekNumber;
    private String title;
    private String skillTag;
    private boolean completed;
    private LocalDateTime completedAt;
}
