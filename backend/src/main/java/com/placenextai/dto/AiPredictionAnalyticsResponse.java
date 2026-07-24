package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiPredictionAnalyticsResponse {

    private long totalPredictions;
    private double averageProbability;
    private long lowRisk;
    private long mediumRisk;
    private long highRisk;
}
