package com.placenextai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAlumniProfileRequest {

    @Size(max = 150)
    private String currentCompany;

    @Size(max = 100)
    private String designation;

    @Min(value = 1980, message = "Graduation year must be 1980 or later")
    @Max(value = 2040, message = "Graduation year must be 2040 or earlier")
    private Integer graduationYear;

    @Size(max = 500)
    private String expertise;

    @Size(max = 1000)
    private String bio;

    @Size(max = 300)
    private String linkedinUrl;

    @Size(max = 500)
    private String profileImageUrl;

    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;
}
