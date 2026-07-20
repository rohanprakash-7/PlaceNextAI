package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private int profileCompletion;
}
