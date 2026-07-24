package com.placenextai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorRequestCreateRequest {

    @NotNull(message = "Alumni id is required")
    private Long alumniId;

    @NotBlank(message = "Topic is required")
    @Size(max = 60)
    private String topic;

    @NotBlank(message = "Message is required")
    @Size(max = 1000)
    private String message;
}
