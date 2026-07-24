package com.placenextai.service;

import com.placenextai.dto.InterviewQuestionResponse;
import com.placenextai.dto.InterviewSessionResponse;
import com.placenextai.dto.StartInterviewRequest;
import com.placenextai.dto.SubmitAnswerRequest;

import java.util.List;

public interface InterviewService {

    InterviewSessionResponse startInterview(String studentEmail, StartInterviewRequest request);

    InterviewSessionResponse getSession(String studentEmail, Long sessionId);

    InterviewQuestionResponse submitAnswer(String studentEmail, Long sessionId, Long questionId, SubmitAnswerRequest request);

    InterviewSessionResponse completeInterview(String studentEmail, Long sessionId);

    List<InterviewSessionResponse> getHistory(String studentEmail);
}
