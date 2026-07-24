package com.placenextai.controller;

import com.placenextai.dto.MentorBookmarkResponse;
import com.placenextai.service.MentorBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/bookmarks")
@RequiredArgsConstructor
public class MentorBookmarkController {

    private final MentorBookmarkService mentorBookmarkService;

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{alumniId}")
    public ResponseEntity<Map<String, Boolean>> toggle(Authentication authentication, @PathVariable Long alumniId) {
        boolean bookmarked = mentorBookmarkService.toggleBookmark(authentication.getName(), alumniId);
        return ResponseEntity.ok(Map.of("bookmarked", bookmarked));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<List<MentorBookmarkResponse>> myBookmarks(Authentication authentication) {
        return ResponseEntity.ok(mentorBookmarkService.getBookmarks(authentication.getName()));
    }
}
