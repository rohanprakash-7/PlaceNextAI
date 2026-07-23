package com.placenextai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewExperienceRequest {

    @NotBlank(message = "Company is required")
    @Size(max = 150)
    private String company;

    @NotBlank(message = "Role title is required")
    @Size(max = 120)
    private String roleTitle;

    @NotBlank(message = "Content is required")
    @Size(max = 3000)
    private String content;
}
