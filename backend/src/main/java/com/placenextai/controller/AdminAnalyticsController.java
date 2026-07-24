package com.placenextai.controller;

import com.placenextai.dto.AdminAnalyticsOverviewResponse;
import com.placenextai.dto.AiPredictionAnalyticsResponse;
import com.placenextai.dto.CollegeAnalyticsResponse;
import com.placenextai.dto.DayCountResponse;
import com.placenextai.dto.DepartmentAnalyticsResponse;
import com.placenextai.dto.HiringTrendResponse;
import com.placenextai.dto.InterviewStatsResponse;
import com.placenextai.dto.RecruiterActivityResponse;
import com.placenextai.dto.ResumeStatsResponse;
import com.placenextai.dto.RiskDistributionResponse;
import com.placenextai.dto.SkillAnalyticsResponse;
import com.placenextai.dto.StudentAnalyticsResponse;
import com.placenextai.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/overview")
    public ResponseEntity<AdminAnalyticsOverviewResponse> overview() {
        return ResponseEntity.ok(adminAnalyticsService.overview());
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentAnalyticsResponse>> departments() {
        return ResponseEntity.ok(adminAnalyticsService.departments());
    }

    @GetMapping("/recruiters")
    public ResponseEntity<List<RecruiterActivityResponse>> recruiters() {
        return ResponseEntity.ok(adminAnalyticsService.recruiterActivity());
    }

    @GetMapping("/risk-distribution")
    public ResponseEntity<RiskDistributionResponse> riskDistribution() {
        return ResponseEntity.ok(adminAnalyticsService.riskDistribution());
    }

    @GetMapping("/colleges")
    public ResponseEntity<List<CollegeAnalyticsResponse>> colleges() {
        return ResponseEntity.ok(adminAnalyticsService.colleges());
    }

    @GetMapping("/students")
    public ResponseEntity<StudentAnalyticsResponse> students() {
        return ResponseEntity.ok(adminAnalyticsService.studentAnalytics());
    }

    @GetMapping("/hiring-trends")
    public ResponseEntity<List<HiringTrendResponse>> hiringTrends() {
        return ResponseEntity.ok(adminAnalyticsService.hiringTrends());
    }

    @GetMapping("/resume-stats")
    public ResponseEntity<ResumeStatsResponse> resumeStats() {
        return ResponseEntity.ok(adminAnalyticsService.resumeStatistics());
    }

    @GetMapping("/interview-stats")
    public ResponseEntity<InterviewStatsResponse> interviewStats() {
        return ResponseEntity.ok(adminAnalyticsService.interviewStatistics());
    }

    @GetMapping("/skills")
    public ResponseEntity<SkillAnalyticsResponse> skills() {
        return ResponseEntity.ok(adminAnalyticsService.skillAnalytics());
    }

    @GetMapping("/ai-predictions")
    public ResponseEntity<AiPredictionAnalyticsResponse> aiPredictions() {
        return ResponseEntity.ok(adminAnalyticsService.aiPredictionAnalytics());
    }

    @GetMapping("/heatmap")
    public ResponseEntity<List<DayCountResponse>> heatmap(@RequestParam(defaultValue = "90") int days) {
        return ResponseEntity.ok(adminAnalyticsService.platformHeatmap(days));
    }
}
