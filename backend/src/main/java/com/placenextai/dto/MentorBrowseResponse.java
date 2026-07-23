package com.placenextai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorBrowseResponse {

    private Long alumniId;
    private String fullName;
    private String currentCompany;
    private String designation;
    private String expertise;
    private String bio;
    private List<MentorSlotResponse> openSlots;
}
