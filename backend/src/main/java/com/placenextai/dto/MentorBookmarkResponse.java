package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorBookmarkResponse {

    private Long alumniId;
    private String fullName;
    private String currentCompany;
    private String designation;
    private Double averageRating;
    private LocalDateTime bookmarkedAt;
}
