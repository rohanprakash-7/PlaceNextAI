package com.placenextai.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlumniRegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

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
}
