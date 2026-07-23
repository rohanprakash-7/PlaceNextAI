package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAnalyticsOverviewResponse {

    private long totalStudents;
    private long placedStudents;
    private double placementPercent;
    private double averageReadiness;
}
