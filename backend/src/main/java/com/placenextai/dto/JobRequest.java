package com.placenextai.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 120)
    private String title;

    @Size(max = 150)
    private String company;

    @Size(max = 120)
    private String location;

    @NotBlank(message = "Description is required")
    @Size(max = 3000)
    private String description;

    @Size(max = 60)
    private String salary;

    @Size(max = 500)
    private String skillsRequired;

    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "Minimum CGPA cannot exceed 10.0")
    private Double minCgpa;
}
