package com.placenextai.service.impl;

import com.placenextai.dto.MentorProfileResponse;
import com.placenextai.dto.UpdateAlumniProfileRequest;
import com.placenextai.entity.Alumni;
import com.placenextai.entity.EventType;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.MentorReviewRepository;
import com.placenextai.service.AlumniProfileService;
import com.placenextai.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlumniProfileServiceImpl implements AlumniProfileService {

    private final AlumniRepository alumniRepository;
    private final MentorReviewRepository mentorReviewRepository;
    private final EventService eventService;

    @Override
    @Transactional(readOnly = true)
    public MentorProfileResponse getMyProfile(String alumniEmail) {
        return toResponse(findAlumni(alumniEmail));
    }

    @Override
    @Transactional
    public MentorProfileResponse updateProfile(String alumniEmail, UpdateAlumniProfileRequest request) {
        Alumni alumni = findAlumni(alumniEmail);

        alumni.setCurrentCompany(request.getCurrentCompany());
        alumni.setDesignation(request.getDesignation());
        alumni.setGraduationYear(request.getGraduationYear());
        alumni.setExpertise(request.getExpertise());
        alumni.setBio(request.getBio());
        alumni.setLinkedinUrl(request.getLinkedinUrl());
        alumni.setProfileImageUrl(request.getProfileImageUrl());
        alumni.setYearsOfExperience(request.getYearsOfExperience());

        Alumni saved = alumniRepository.save(alumni);
        return toResponse(saved);
    }

    private MentorProfileResponse toResponse(Alumni alumni) {
        return MentorProfileResponse.builder()
                .alumniId(alumni.getId())
                .fullName(alumni.getFullName())
                .currentCompany(alumni.getCurrentCompany())
                .designation(alumni.getDesignation())
                .graduationYear(alumni.getGraduationYear())
                .expertise(alumni.getExpertise())
                .bio(alumni.getBio())
                .linkedinUrl(alumni.getLinkedinUrl())
                .profileImageUrl(alumni.getProfileImageUrl())
                .yearsOfExperience(alumni.getYearsOfExperience())
                .averageRating(mentorReviewRepository.averageRatingForAlumni(alumni.getId()))
                .reviewCount(mentorReviewRepository.countByAlumniId(alumni.getId()))
                .bookmarked(false)
                .openSlots(java.util.List.of())
                .recentReviews(java.util.List.of())
                .build();
    }

    private Alumni findAlumni(String email) {
        return alumniRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni not found with email: " + email));
    }
}
