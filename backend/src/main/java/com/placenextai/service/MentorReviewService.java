package com.placenextai.service;

import com.placenextai.dto.MentorReviewRequest;
import com.placenextai.dto.MentorReviewResponse;

import java.util.List;

public interface MentorReviewService {

    MentorReviewResponse submitReview(String studentEmail, MentorReviewRequest request);

    List<MentorReviewResponse> getReviewsForAlumni(Long alumniId);
}
