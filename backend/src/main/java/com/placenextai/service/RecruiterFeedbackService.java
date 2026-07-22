package com.placenextai.service;

import com.placenextai.dto.RecruiterFeedbackRequest;
import com.placenextai.dto.RecruiterFeedbackResponse;
import com.placenextai.dto.StudentFeedbackSummaryResponse;

import java.util.List;

public interface RecruiterFeedbackService {

    RecruiterFeedbackResponse submitFeedback(String recruiterEmail, Long applicationId, RecruiterFeedbackRequest request);

    List<RecruiterFeedbackResponse> getFeedbackForApplication(String userEmail, Long applicationId);

    StudentFeedbackSummaryResponse getStudentSummary(String studentEmail);
}
