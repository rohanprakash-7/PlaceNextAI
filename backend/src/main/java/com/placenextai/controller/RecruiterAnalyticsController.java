package com.placenextai.controller;

import com.placenextai.dto.DepartmentBreakdownResponse;
import com.placenextai.dto.FunnelResponse;
import com.placenextai.dto.SkillDistributionResponse;
import com.placenextai.service.RecruiterAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/analytics")
@RequiredArgsConstructor
public class RecruiterAnalyticsController {

    private final RecruiterAnalyticsService recruiterAnalyticsService;

    @GetMapping("/funnel")
    public ResponseEntity<List<FunnelResponse>> funnel(Authentication authentication) {
        return ResponseEntity.ok(recruiterAnalyticsService.funnel(authentication.getName()));
    }

    @GetMapping("/skills")
    public ResponseEntity<List<SkillDistributionResponse>> skills(Authentication authentication) {
        return ResponseEntity.ok(recruiterAnalyticsService.skillDistribution(authentication.getName()));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentBreakdownResponse>> departments(Authentication authentication) {
        return ResponseEntity.ok(recruiterAnalyticsService.departmentBreakdown(authentication.getName()));
    }
}
