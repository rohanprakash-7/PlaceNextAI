package com.placenextai.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartInterviewRequest {

    @Size(max = 150)
    private String targetCompany;
}
