package com.placenextai.service;

import com.placenextai.dto.RankedCandidateResponse;

import java.util.List;

public interface CandidateRankingService {

    List<RankedCandidateResponse> rankCandidatesForJob(String recruiterEmail, Long jobId);

    List<RankedCandidateResponse> compareCandidates(String recruiterEmail, Long jobId, List<Long> studentIds);
}
