package com.placenextai.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @Pattern(regexp = "^$|^[0-9+\\- ]{8,15}$", message = "Phone must be a valid number")
    private String phone;

    @Size(max = 150)
    private String college;

    @Size(max = 80)
    private String branch;

    @Min(value = 2000)
    @Max(value = 2040)
    private Integer graduationYear;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double cgpa;

    @Size(max = 500)
    private String skills;
}
