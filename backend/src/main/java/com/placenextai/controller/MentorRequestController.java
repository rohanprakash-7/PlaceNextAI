package com.placenextai.controller;

import com.placenextai.dto.MentorRequestCreateRequest;
import com.placenextai.dto.MentorRequestResponse;
import com.placenextai.service.MentorRequestService;
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
public class MentorRequestController {

    private final MentorRequestService mentorRequestService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/api/student/mentor-requests")
    public ResponseEntity<MentorRequestResponse> sendRequest(
            Authentication authentication, @Valid @RequestBody MentorRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorRequestService.sendRequest(authentication.getName(), request));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/api/student/mentor-requests")
    public ResponseEntity<List<MentorRequestResponse>> myRequests(Authentication authentication) {
        return ResponseEntity.ok(mentorRequestService.getRequestsForStudent(authentication.getName()));
    }

    @PreAuthorize("hasRole('ALUMNI')")
    @GetMapping("/api/alumni/mentor-requests")
    public ResponseEntity<List<MentorRequestResponse>> incomingRequests(Authentication authentication) {
        return ResponseEntity.ok(mentorRequestService.getRequestsForAlumni(authentication.getName()));
    }

    @PreAuthorize("hasRole('ALUMNI')")
    @PatchMapping("/api/alumni/mentor-requests/{id}/accept")
    public ResponseEntity<MentorRequestResponse> accept(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(mentorRequestService.accept(authentication.getName(), id));
    }

    @PreAuthorize("hasRole('ALUMNI')")
    @PatchMapping("/api/alumni/mentor-requests/{id}/reject")
    public ResponseEntity<MentorRequestResponse> reject(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(mentorRequestService.reject(authentication.getName(), id));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ALUMNI')")
    @GetMapping("/api/mentor-requests/{id}")
    public ResponseEntity<MentorRequestResponse> getRequest(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(mentorRequestService.getForParticipant(authentication.getName(), id));
    }
}
