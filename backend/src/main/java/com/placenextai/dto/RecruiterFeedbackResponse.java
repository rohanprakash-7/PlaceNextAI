package com.placenextai.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterFeedbackResponse {

    private Long id;
    private int communicationRating;
    private int technicalRating;
    private int problemSolvingRating;
    private int cultureFitRating;
    private String outcome;
    private String comment;
    private LocalDateTime createdAt;
}
