package com.placenextai.service;

import com.placenextai.dto.SkillGapResponse;

import java.util.List;

public interface SkillGapService {

    SkillGapResponse analyze(String studentEmail, String targetCompany);

    List<String> listTargetCompanies();
}
