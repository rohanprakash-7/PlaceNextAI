package com.placenextai.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnalyticsResponse {

    private long totalStudents;
    private double averageResumeScore;
    private double averageMockInterviewScore;
    private double averageCgpa;
    private Map<String, Long> placementStatusBreakdown;
}
