package com.placenextai.service.impl;

import com.placenextai.dto.RecruiterProfileResponse;
import com.placenextai.dto.RecruiterUpdateRequest;
import com.placenextai.entity.Recruiter;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.service.RecruiterProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecruiterProfileServiceImpl implements RecruiterProfileService {

    private final RecruiterRepository recruiterRepository;

    @Override
    @Transactional(readOnly = true)
    public RecruiterProfileResponse getProfile(String email) {
        return toResponse(findByEmail(email));
    }

    @Override
    @Transactional
    public RecruiterProfileResponse updateProfile(String email, RecruiterUpdateRequest request) {
        Recruiter recruiter = findByEmail(email);
        recruiter.setCompanyName(request.getCompanyName());
        recruiter.setRecruiterName(request.getRecruiterName());
        recruiter.setDesignation(request.getDesignation());
        return toResponse(recruiterRepository.save(recruiter));
    }

    private Recruiter findByEmail(String email) {
        return recruiterRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with email: " + email));
    }

    private RecruiterProfileResponse toResponse(Recruiter recruiter) {
        return RecruiterProfileResponse.builder()
                .id(recruiter.getId())
                .companyName(recruiter.getCompanyName())
                .recruiterName(recruiter.getRecruiterName())
                .email(recruiter.getEmail())
                .designation(recruiter.getDesignation())
                .build();
    }
}
