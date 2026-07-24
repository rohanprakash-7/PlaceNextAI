package com.placenextai.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardResponse {

    private List<LeaderboardEntryResponse> topEntries;
    private LeaderboardEntryResponse myEntry;
}
