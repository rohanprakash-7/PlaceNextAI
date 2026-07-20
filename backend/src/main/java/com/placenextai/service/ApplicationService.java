package com.placenextai.service;

import com.placenextai.dto.ApplicationRequest;
import com.placenextai.dto.ApplicationResponse;

import java.util.List;

public interface ApplicationService {

    ApplicationResponse apply(String studentEmail, ApplicationRequest request);

    List<ApplicationResponse> getApplicationsForStudent(String studentEmail);

    List<ApplicationResponse> getApplicationsForRecruiter(String recruiterEmail);
}
