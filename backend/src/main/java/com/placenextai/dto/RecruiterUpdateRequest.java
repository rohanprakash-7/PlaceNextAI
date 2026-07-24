package com.placenextai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterUpdateRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 150)
    private String companyName;

    @NotBlank(message = "Recruiter name is required")
    @Size(max = 100)
    private String recruiterName;

    @Size(max = 100)
    private String designation;
}
