package com.placenextai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntryResponse {

    private int rank;
    private Long studentId;
    private String fullName;
    private String college;
    private String branch;
    private Integer xp;
    private Integer level;
    private boolean currentUser;
}
