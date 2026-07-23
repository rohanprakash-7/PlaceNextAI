package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacementPredictionResponse {

    private int probabilityScore;
    private String riskLevel;
    private List<Factor> positiveFactors;
    private List<Factor> negativeFactors;
    private List<String> recommendations;
    private LocalDateTime computedAt;
    private List<HistoryPoint> history;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Factor {
        private String label;
        private int impact;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryPoint {
        private int probabilityScore;
        private LocalDateTime computedAt;
    }
}
