package com.placenextai.service.impl;

import com.placenextai.dto.InterviewExperienceRequest;
import com.placenextai.dto.InterviewExperienceResponse;
import com.placenextai.entity.Alumni;
import com.placenextai.entity.InterviewExperience;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.InterviewExperienceRepository;
import com.placenextai.service.InterviewExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewExperienceServiceImpl implements InterviewExperienceService {

    private final AlumniRepository alumniRepository;
    private final InterviewExperienceRepository interviewExperienceRepository;

    @Override
    @Transactional
    public InterviewExperienceResponse postExperience(String alumniEmail, InterviewExperienceRequest request) {
        Alumni alumni = alumniRepository.findByEmail(alumniEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni not found with email: " + alumniEmail));

        InterviewExperience saved = interviewExperienceRepository.save(InterviewExperience.builder()
                .alumniId(alumni.getId())
                .company(request.getCompany())
                .roleTitle(request.getRoleTitle())
                .content(request.getContent())
                .build());

        return toResponse(saved, alumni);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewExperienceResponse> getExperiencesForCompany(String company) {
        return interviewExperienceRepository.findByCompanyIgnoreCaseOrderByCreatedAtDesc(company).stream()
                .map(experience -> toResponse(experience, alumniRepository.findById(experience.getAlumniId())
                        .orElse(null)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewExperienceResponse> browse(String company, String search) {
        String normalizedCompany = (company == null || company.isBlank()) ? null : company.trim();
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
        return interviewExperienceRepository.search(normalizedCompany, normalizedSearch).stream()
                .map(experience -> toResponse(experience, alumniRepository.findById(experience.getAlumniId())
                        .orElse(null)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listCompanies() {
        return interviewExperienceRepository.findDistinctCompanies();
    }

    private InterviewExperienceResponse toResponse(InterviewExperience experience, Alumni alumni) {
        return InterviewExperienceResponse.builder()
                .id(experience.getId())
                .alumniName(alumni == null ? "Unknown" : alumni.getFullName())
                .company(experience.getCompany())
                .roleTitle(experience.getRoleTitle())
                .content(experience.getContent())
                .createdAt(experience.getCreatedAt())
                .build();
    }
}
