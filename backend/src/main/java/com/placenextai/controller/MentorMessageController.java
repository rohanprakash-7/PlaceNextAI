package com.placenextai.controller;

import com.placenextai.dto.MentorMessageRequest;
import com.placenextai.dto.MentorMessageResponse;
import com.placenextai.service.MentorMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentor-requests/{requestId}/messages")
@RequiredArgsConstructor
public class MentorMessageController {

    private final MentorMessageService mentorMessageService;

    @PreAuthorize("hasAnyRole('STUDENT', 'ALUMNI')")
    @GetMapping
    public ResponseEntity<List<MentorMessageResponse>> getMessages(
            Authentication authentication, @PathVariable Long requestId) {
        return ResponseEntity.ok(mentorMessageService.getMessages(authentication.getName(), requestId));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ALUMNI')")
    @PostMapping
    public ResponseEntity<MentorMessageResponse> sendMessage(
            Authentication authentication,
            @PathVariable Long requestId,
            @Valid @RequestBody MentorMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorMessageService.sendMessage(authentication.getName(), requestId, request));
    }
}
