package com.placenextai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorMessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(max = 2000)
    private String content;
}
