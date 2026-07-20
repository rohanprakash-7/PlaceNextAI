package com.placenextai.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be at most 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Pattern(regexp = "^$|^[0-9+\\- ]{8,15}$", message = "Phone must be a valid number")
    private String phone;

    @Size(max = 150)
    private String college;

    @Size(max = 80)
    private String branch;

    @Min(value = 2000, message = "Graduation year must be 2000 or later")
    @Max(value = 2040, message = "Graduation year must be 2040 or earlier")
    private Integer graduationYear;

    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10")
    private Double cgpa;

    @Size(max = 500)
    private String skills;
}
