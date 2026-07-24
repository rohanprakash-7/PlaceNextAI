package com.placenextai.service;

import com.placenextai.dto.GamificationSummaryResponse;
import com.placenextai.dto.LeaderboardResponse;
import com.placenextai.entity.EventType;

public interface GamificationService {

    /**
     * Awards event-based XP and updates the daily activity streak.
     * @return the student's currentStreak after this update
     */
    int recordActivity(Long studentId, EventType type);

    void addXp(Long studentId, int amount);

    GamificationSummaryResponse getSummary(String studentEmail);

    LeaderboardResponse getLeaderboard(String studentEmail);
}
