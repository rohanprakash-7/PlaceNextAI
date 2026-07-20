package com.placenextai.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequest {

    @NotNull(message = "Job id is required")
    private Long jobId;
}
