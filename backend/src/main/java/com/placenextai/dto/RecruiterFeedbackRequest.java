package com.placenextai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterFeedbackRequest {

    @Min(1) @Max(5)
    private int communicationRating;

    @Min(1) @Max(5)
    private int technicalRating;

    @Min(1) @Max(5)
    private int problemSolvingRating;

    @Min(1) @Max(5)
    private int cultureFitRating;

    @NotBlank(message = "Outcome is required")
    private String outcome;

    @Size(max = 500)
    private String comment;
}
