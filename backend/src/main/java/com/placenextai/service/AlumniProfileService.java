package com.placenextai.service;

import com.placenextai.dto.MentorProfileResponse;
import com.placenextai.dto.UpdateAlumniProfileRequest;

public interface AlumniProfileService {

    MentorProfileResponse getMyProfile(String alumniEmail);

    MentorProfileResponse updateProfile(String alumniEmail, UpdateAlumniProfileRequest request);
}
