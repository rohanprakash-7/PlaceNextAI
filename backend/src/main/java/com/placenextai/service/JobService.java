package com.placenextai.service;

import com.placenextai.dto.JobRequest;
import com.placenextai.dto.JobResponse;

import java.util.List;

public interface JobService {

    List<JobResponse> getAllJobs();

    JobResponse getJobById(Long id);

    JobResponse createJob(JobRequest request, String creatorEmail);

    JobResponse updateJob(Long id, JobRequest request, String editorEmail);

    void deleteJob(Long id, String editorEmail);

    List<JobResponse> getJobsForRecruiter(String recruiterEmail);
}
