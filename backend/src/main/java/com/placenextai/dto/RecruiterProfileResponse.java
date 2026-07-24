package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterProfileResponse {

    private Long id;
    private String companyName;
    private String recruiterName;
    private String email;
    private String designation;
}
