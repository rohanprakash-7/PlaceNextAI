package com.placenextai.controller;

import com.placenextai.dto.InterviewQuestionResponse;
import com.placenextai.dto.InterviewSessionResponse;
import com.placenextai.dto.StartInterviewRequest;
import com.placenextai.dto.SubmitAnswerRequest;
import com.placenextai.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/start")
    public ResponseEntity<InterviewSessionResponse> start(
            Authentication authentication, @Valid @RequestBody StartInterviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewService.startInterview(authentication.getName(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewSessionResponse> getSession(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(interviewService.getSession(authentication.getName(), id));
    }

    @PostMapping("/{id}/questions/{questionId}/answer")
    public ResponseEntity<InterviewQuestionResponse> submitAnswer(
            Authentication authentication,
            @PathVariable Long id,
            @PathVariable Long questionId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        return ResponseEntity.ok(interviewService.submitAnswer(authentication.getName(), id, questionId, request));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<InterviewSessionResponse> complete(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(interviewService.completeInterview(authentication.getName(), id));
    }

    @GetMapping("/history")
    public ResponseEntity<List<InterviewSessionResponse>> history(Authentication authentication) {
        return ResponseEntity.ok(interviewService.getHistory(authentication.getName()));
    }
}
