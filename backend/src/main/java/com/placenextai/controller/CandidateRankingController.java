package com.placenextai.controller;

import com.placenextai.dto.RankedCandidateResponse;
import com.placenextai.service.CandidateRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class CandidateRankingController {

    private final CandidateRankingService candidateRankingService;

    @GetMapping("/jobs/{jobId}/ranking")
    public ResponseEntity<List<RankedCandidateResponse>> ranking(
            Authentication authentication, @PathVariable Long jobId) {
        return ResponseEntity.ok(candidateRankingService.rankCandidatesForJob(authentication.getName(), jobId));
    }

    @GetMapping("/candidates/compare")
    public ResponseEntity<List<RankedCandidateResponse>> compare(
            Authentication authentication,
            @RequestParam Long jobId,
            @RequestParam List<Long> ids) {
        return ResponseEntity.ok(candidateRankingService.compareCandidates(authentication.getName(), jobId, ids));
    }
}
