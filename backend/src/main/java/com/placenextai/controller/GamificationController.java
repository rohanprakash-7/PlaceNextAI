package com.placenextai.controller;

import com.placenextai.dto.GamificationSummaryResponse;
import com.placenextai.dto.LeaderboardResponse;
import com.placenextai.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;

    @GetMapping("/api/student/gamification")
    public ResponseEntity<GamificationSummaryResponse> mySummary(Authentication authentication) {
        return ResponseEntity.ok(gamificationService.getSummary(authentication.getName()));
    }

    @GetMapping("/api/student/leaderboard")
    public ResponseEntity<LeaderboardResponse> leaderboard(Authentication authentication) {
        return ResponseEntity.ok(gamificationService.getLeaderboard(authentication.getName()));
    }
}
