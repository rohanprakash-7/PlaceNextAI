package com.placenextai.controller;

import com.placenextai.dto.BookMentorSessionRequest;
import com.placenextai.dto.MentorBrowseResponse;
import com.placenextai.dto.MentorProfileResponse;
import com.placenextai.dto.MentorSlotResponse;
import com.placenextai.service.MentorSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorSessionController {

    private final MentorSlotService mentorSlotService;

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/api/mentors")
    public ResponseEntity<List<MentorBrowseResponse>> browseMentors(
            Authentication authentication,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String company) {
        return ResponseEntity.ok(mentorSlotService.browseMentors(authentication.getName(), search, company));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/api/mentors/companies")
    public ResponseEntity<List<String>> mentorCompanies() {
        return ResponseEntity.ok(mentorSlotService.listMentorCompanies());
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/api/mentors/{alumniId}")
    public ResponseEntity<MentorProfileResponse> mentorProfile(
            Authentication authentication, @PathVariable Long alumniId) {
        return ResponseEntity.ok(mentorSlotService.getMentorProfile(authentication.getName(), alumniId));
    }

    @PostMapping("/api/student/mentor-sessions/book")
    public ResponseEntity<MentorSlotResponse> book(
            Authentication authentication, @Valid @RequestBody BookMentorSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mentorSlotService.bookSlot(authentication.getName(), request.getSlotId()));
    }

    @GetMapping("/api/student/mentor-sessions")
    public ResponseEntity<List<MentorSlotResponse>> mySessions(Authentication authentication) {
        return ResponseEntity.ok(mentorSlotService.getSessionsForStudent(authentication.getName()));
    }

    @GetMapping("/api/student/mentor-sessions/{id}/calendar.ics")
    public ResponseEntity<String> calendar(Authentication authentication, @PathVariable Long id) {
        String ics = mentorSlotService.generateCalendarInvite(authentication.getName(), id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/calendar"))
                .header("Content-Disposition", "attachment; filename=mentor-session-" + id + ".ics")
                .body(ics);
    }
}
