package com.placenextai.service;

import com.placenextai.dto.InterviewExperienceRequest;
import com.placenextai.dto.InterviewExperienceResponse;

import java.util.List;

public interface InterviewExperienceService {

    InterviewExperienceResponse postExperience(String alumniEmail, InterviewExperienceRequest request);

    List<InterviewExperienceResponse> getExperiencesForCompany(String company);
}
