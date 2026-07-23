package com.placenextai.service;

import com.placenextai.dto.AlumniRegisterRequest;
import com.placenextai.dto.AuthResponse;
import com.placenextai.dto.LoginRequest;
import com.placenextai.dto.MeResponse;
import com.placenextai.dto.RecruiterRegisterRequest;
import com.placenextai.dto.StudentRegisterRequest;

public interface AuthService {

    AuthResponse registerStudent(StudentRegisterRequest request);

    AuthResponse registerRecruiter(RecruiterRegisterRequest request);

    AuthResponse registerAlumni(AlumniRegisterRequest request);

    AuthResponse login(LoginRequest request, String expectedRole);

    AuthResponse login(LoginRequest request);

    MeResponse getCurrentUser(String email);
}
