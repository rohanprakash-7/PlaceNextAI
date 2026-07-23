package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperienceResponse {

    private Long id;
    private String alumniName;
    private String company;
    private String roleTitle;
    private String content;
    private LocalDateTime createdAt;
}
