package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamificationSummaryResponse {

    private Integer xp;
    private Integer level;
    private Integer xpIntoLevel;
    private Integer xpForNextLevel;
    private Double progressPercent;
    private Integer currentStreak;
    private Integer longestStreak;
}
