package com.placenextai.controller;

import com.placenextai.dto.CreateMentorSlotRequest;
import com.placenextai.dto.InterviewExperienceRequest;
import com.placenextai.dto.InterviewExperienceResponse;
import com.placenextai.dto.MentorProfileResponse;
import com.placenextai.dto.MentorSlotResponse;
import com.placenextai.dto.UpdateAlumniProfileRequest;
import com.placenextai.service.AlumniProfileService;
import com.placenextai.service.InterviewExperienceService;
import com.placenextai.service.MentorSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
@RequiredArgsConstructor
public class AlumniController {

    private final MentorSlotService mentorSlotService;
    private final InterviewExperienceService interviewExperienceService;
    private final AlumniProfileService alumniProfileService;

    @PreAuthorize("hasRole('ALUMNI')")
    @PostMapping("/slots")
    public ResponseEntity<MentorSlotResponse> createSlot(
            Authentication authentication, @Valid @RequestBody CreateMentorSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorSlotService.createSlot(authentication.getName(), request));
    }

    @PreAuthorize("hasRole('ALUMNI')")
    @GetMapping("/slots")
    public ResponseEntity<List<MentorSlotResponse>> mySlots(Authentication authentication) {
        return ResponseEntity.ok(mentorSlotService.getSlotsForAlumni(authentication.getName()));
    }

    @PreAuthorize("hasRole('ALUMNI')")
    @DeleteMapping("/slots/{id}")
    public ResponseEntity<Void> deleteSlot(Authentication authentication, @PathVariable Long id) {
        mentorSlotService.deleteSlot(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ALUMNI')")
    @GetMapping("/profile")
    public ResponseEntity<MentorProfileResponse> myProfile(Authentication authentication) {
        return ResponseEntity.ok(alumniProfileService.getMyProfile(authentication.getName()));
    }

    @PreAuthorize("hasRole('ALUMNI')")
    @PutMapping("/profile")
    public ResponseEntity<MentorProfileResponse> updateProfile(
            Authentication authentication, @Valid @RequestBody UpdateAlumniProfileRequest request) {
        return ResponseEntity.ok(alumniProfileService.updateProfile(authentication.getName(), request));
    }

    @PreAuthorize("hasRole('ALUMNI')")
    @GetMapping("/mentor-sessions")
    public ResponseEntity<List<MentorSlotResponse>> mySessions(Authentication authentication) {
        return ResponseEntity.ok(mentorSlotService.getSessionsForAlumni(authentication.getName()));
    }

    @PreAuthorize("hasRole('ALUMNI')")
    @PostMapping("/interview-experiences")
    public ResponseEntity<InterviewExperienceResponse> postExperience(
            Authentication authentication, @Valid @RequestBody InterviewExperienceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interviewExperienceService.postExperience(authentication.getName(), request));
    }
}
