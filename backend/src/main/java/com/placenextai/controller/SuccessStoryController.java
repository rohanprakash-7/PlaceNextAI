package com.placenextai.controller;

import com.placenextai.dto.InterviewExperienceResponse;
import com.placenextai.service.InterviewExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/success-stories")
@RequiredArgsConstructor
public class SuccessStoryController {

    private final InterviewExperienceService interviewExperienceService;

    @PreAuthorize("hasAnyRole('STUDENT', 'ALUMNI')")
    @GetMapping
    public ResponseEntity<List<InterviewExperienceResponse>> browse(
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(interviewExperienceService.browse(company, search));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ALUMNI')")
    @GetMapping("/companies")
    public ResponseEntity<List<String>> companies() {
        return ResponseEntity.ok(interviewExperienceService.listCompanies());
    }
}
