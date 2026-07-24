package com.placenextai.service;

import com.placenextai.dto.EligibilityResponse;
import com.placenextai.dto.EligibleCompanySummary;

import java.util.List;

public interface EligibilityService {

    List<EligibilityResponse> checkForCompany(String studentEmail, String company);

    EligibilityResponse checkForJob(String studentEmail, Long jobId);

    List<String> listCompanies();

    List<EligibleCompanySummary> listEligibleCompanies(String studentEmail);
}
