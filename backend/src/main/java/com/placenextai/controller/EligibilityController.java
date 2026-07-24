package com.placenextai.controller;

import com.placenextai.dto.EligibilityResponse;
import com.placenextai.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/eligibility")
@RequiredArgsConstructor
public class EligibilityController {

    private final EligibilityService eligibilityService;

    @GetMapping("/companies")
    public ResponseEntity<List<String>> companies() {
        return ResponseEntity.ok(eligibilityService.listCompanies());
    }

    @GetMapping("/company/{company}")
    public ResponseEntity<List<EligibilityResponse>> byCompany(
            Authentication authentication, @PathVariable String company) {
        return ResponseEntity.ok(eligibilityService.checkForCompany(authentication.getName(), company));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<EligibilityResponse> byJob(Authentication authentication, @PathVariable Long jobId) {
        return ResponseEntity.ok(eligibilityService.checkForJob(authentication.getName(), jobId));
    }
}
