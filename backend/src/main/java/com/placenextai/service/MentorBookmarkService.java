package com.placenextai.service;

import com.placenextai.dto.MentorBookmarkResponse;

import java.util.List;

public interface MentorBookmarkService {

    boolean toggleBookmark(String studentEmail, Long alumniId);

    List<MentorBookmarkResponse> getBookmarks(String studentEmail);
}
