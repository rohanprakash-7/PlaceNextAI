package com.placenextai.service;

import com.placenextai.dto.MentorRequestCreateRequest;
import com.placenextai.dto.MentorRequestResponse;

import java.util.List;

public interface MentorRequestService {

    MentorRequestResponse sendRequest(String studentEmail, MentorRequestCreateRequest request);

    List<MentorRequestResponse> getRequestsForStudent(String studentEmail);

    List<MentorRequestResponse> getRequestsForAlumni(String alumniEmail);

    MentorRequestResponse accept(String alumniEmail, Long requestId);

    MentorRequestResponse reject(String alumniEmail, Long requestId);

    MentorRequestResponse getForParticipant(String userEmail, Long requestId);
}
