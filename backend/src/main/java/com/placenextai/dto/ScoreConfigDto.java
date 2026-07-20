package com.placenextai.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreConfigDto {

    @DecimalMin("0.0") @DecimalMax("1.0") @NotNull
    private Double academicWeight;

    @DecimalMin("0.0") @DecimalMax("1.0") @NotNull
    private Double resumeWeight;

    @DecimalMin("0.0") @DecimalMax("1.0") @NotNull
    private Double skillWeight;

    @DecimalMin("0.0") @DecimalMax("1.0") @NotNull
    private Double interviewWeight;

    @DecimalMin("0.0") @DecimalMax("1.0") @NotNull
    private Double activityWeight;

    @Min(0) @Max(25) @NotNull
    private Integer feedbackAdjustmentCap;
}
