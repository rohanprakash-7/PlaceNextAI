package com.placenextai.service;

import com.placenextai.dto.ResumeVersionResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeService {

    ResumeVersionResponse uploadAndAnalyze(String studentEmail, MultipartFile file, String jobDescription);

    List<ResumeVersionResponse> getVersions(String studentEmail);

    ResumeVersionResponse getVersion(String studentEmail, Long versionId);
}
