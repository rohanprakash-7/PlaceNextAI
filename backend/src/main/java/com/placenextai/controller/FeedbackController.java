package com.placenextai.controller;

import com.placenextai.dto.RecruiterFeedbackRequest;
import com.placenextai.dto.RecruiterFeedbackResponse;
import com.placenextai.dto.StudentFeedbackSummaryResponse;
import com.placenextai.service.RecruiterFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedbackController {

    private final RecruiterFeedbackService recruiterFeedbackService;

    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/api/application/{id}/feedback")
    public ResponseEntity<RecruiterFeedbackResponse> submitFeedback(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody RecruiterFeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recruiterFeedbackService.submitFeedback(authentication.getName(), id, request));
    }

    @GetMapping("/api/application/{id}/feedback")
    public ResponseEntity<List<RecruiterFeedbackResponse>> getFeedback(
            Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(recruiterFeedbackService.getFeedbackForApplication(authentication.getName(), id));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/api/student/feedback-summary")
    public ResponseEntity<StudentFeedbackSummaryResponse> getFeedbackSummary(Authentication authentication) {
        return ResponseEntity.ok(recruiterFeedbackService.getStudentSummary(authentication.getName()));
    }
}
