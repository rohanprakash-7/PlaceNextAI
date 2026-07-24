package com.placenextai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfileResponse {

    private Long alumniId;
    private String fullName;
    private String currentCompany;
    private String designation;
    private Integer graduationYear;
    private String expertise;
    private String bio;
    private String linkedinUrl;
    private String profileImageUrl;
    private Integer yearsOfExperience;
    private Double averageRating;
    private Long reviewCount;
    private boolean bookmarked;
    private List<MentorSlotResponse> openSlots;
    private List<MentorReviewResponse> recentReviews;
}
