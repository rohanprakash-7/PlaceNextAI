package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollegeAnalyticsResponse {

    private String college;
    private long studentCount;
    private double averageReadiness;
    private double placementPercent;
}
