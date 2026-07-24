package com.placenextai.service;

import com.placenextai.dto.RecruiterProfileResponse;
import com.placenextai.dto.RecruiterUpdateRequest;

public interface RecruiterProfileService {

    RecruiterProfileResponse getProfile(String email);

    RecruiterProfileResponse updateProfile(String email, RecruiterUpdateRequest request);
}
