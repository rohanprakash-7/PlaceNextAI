package com.placenextai.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewStatsResponse {

    private long totalMockInterviews;
    private double averageMockInterviewScore;
    private long totalSuccessStories;
    private Map<String, Long> topCompaniesByStories;
}
