package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapResponse {

    private Long id;
    private String targetCompany;
    private int totalItems;
    private int completedItems;
    private int progressPercent;
    private LocalDateTime createdAt;
    private List<RoadmapItemResponse> items;
}
