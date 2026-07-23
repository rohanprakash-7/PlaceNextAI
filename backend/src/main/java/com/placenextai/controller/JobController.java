package com.placenextai.controller;

import com.placenextai.dto.InterviewExperienceResponse;
import com.placenextai.dto.JobRequest;
import com.placenextai.dto.JobResponse;
import com.placenextai.dto.MessageResponse;
import com.placenextai.service.InterviewExperienceService;
import com.placenextai.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final InterviewExperienceService interviewExperienceService;

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping("/{id}/interview-experiences")
    public ResponseEntity<List<InterviewExperienceResponse>> getInterviewExperiences(@PathVariable Long id) {
        String company = jobService.getJobById(id).getCompany();
        return ResponseEntity.ok(interviewExperienceService.getExperiencesForCompany(company));
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            Authentication authentication,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobService.createJob(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponse> updateJob(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(jobService.updateJob(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteJob(Authentication authentication, @PathVariable Long id) {
        jobService.deleteJob(id, authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Job deleted successfully"));
    }
}
