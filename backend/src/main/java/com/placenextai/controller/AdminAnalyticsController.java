package com.placenextai.controller;

import com.placenextai.dto.AdminAnalyticsOverviewResponse;
import com.placenextai.dto.DepartmentAnalyticsResponse;
import com.placenextai.dto.RecruiterActivityResponse;
import com.placenextai.dto.RiskDistributionResponse;
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
}
