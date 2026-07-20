package com.placenextai.service;

import com.placenextai.dto.StudentResponse;
import com.placenextai.dto.StudentUpdateRequest;

public interface StudentService {

    StudentResponse getProfile(String email);

    StudentResponse updateProfile(String email, StudentUpdateRequest request);
}
