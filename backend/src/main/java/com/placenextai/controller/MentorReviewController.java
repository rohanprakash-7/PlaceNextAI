package com.placenextai.controller;

import com.placenextai.dto.MentorReviewRequest;
import com.placenextai.dto.MentorReviewResponse;
import com.placenextai.service.MentorReviewService;
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
public class MentorReviewController {

    private final MentorReviewService mentorReviewService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/api/student/mentor-reviews")
    public ResponseEntity<MentorReviewResponse> submitReview(
            Authentication authentication, @Valid @RequestBody MentorReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorReviewService.submitReview(authentication.getName(), request));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ALUMNI')")
    @GetMapping("/api/mentors/{alumniId}/reviews")
    public ResponseEntity<List<MentorReviewResponse>> reviewsForAlumni(@PathVariable Long alumniId) {
        return ResponseEntity.ok(mentorReviewService.getReviewsForAlumni(alumniId));
    }
}
