package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadinessResponse {

    private int totalScore;
    private int academicScore;
    private int resumeScore;
    private int skillScore;
    private int interviewScore;
    private int activityScore;
    private int feedbackAdjustment;
    private String weakestDimension;
    private String improvementTip;
    private LocalDateTime computedAt;
    private List<ScoreHistoryPoint> history;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScoreHistoryPoint {
        private int totalScore;
        private LocalDateTime computedAt;
    }
}
