package com.placenextai.service.impl;

import com.placenextai.dto.EligibilityResponse;
import com.placenextai.dto.EligibleCompanySummary;
import com.placenextai.entity.Job;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EligibilityServiceImpl implements EligibilityService {

    private static final int SKILL_MATCH_THRESHOLD = 60;

    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EligibilityResponse> checkForCompany(String studentEmail, String company) {
        Student student = findStudent(studentEmail);
        List<Job> jobs = jobRepository.findByCompanyIgnoreCaseOrderByCreatedDateDesc(company);
        if (jobs.isEmpty()) {
            throw new ResourceNotFoundException("No job postings found for " + company);
        }
        return jobs.stream().map(job -> toResponse(student, job)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EligibilityResponse checkForJob(String studentEmail, Long jobId) {
        Student student = findStudent(studentEmail);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
        return toResponse(student, job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listCompanies() {
        return jobRepository.findDistinctCompanies();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EligibleCompanySummary> listEligibleCompanies(String studentEmail) {
        Student student = findStudent(studentEmail);
        Set<String> studentSkills = splitSkills(student.getSkills());

        Map<String, List<Job>> jobsByCompany = jobRepository.findAll().stream()
                .collect(Collectors.groupingBy(Job::getCompany));

        List<EligibleCompanySummary> eligible = new ArrayList<>();
        jobsByCompany.forEach((company, jobs) -> {
            EligibleCompanySummary best = null;
            for (Job job : jobs) {
                EligibilityResponse response = toResponse(student, job);
                if (!response.isOverallEligible()) {
                    continue;
                }
                if (best == null || response.getSuccessProbability() > best.getSuccessProbability()) {
                    best = EligibleCompanySummary.builder()
                            .company(company)
                            .matchPercent(response.getMatchPercent())
                            .successProbability(response.getSuccessProbability())
                            .probabilityLabel(response.getProbabilityLabel())
                            .build();
                }
            }
            if (best != null) {
                eligible.add(best);
            }
        });

        eligible.sort((a, b) -> b.getSuccessProbability() - a.getSuccessProbability());
        return eligible;
    }

    private EligibilityResponse toResponse(Student student, Job job) {
        Set<String> studentSkills = splitSkills(student.getSkills());
        Set<String> requiredSkills = splitSkills(job.getSkillsRequired());

        List<String> matched = requiredSkills.stream()
                .filter(skill -> containsIgnoreCase(studentSkills, skill))
                .toList();
        List<String> missing = requiredSkills.stream()
                .filter(skill -> !containsIgnoreCase(studentSkills, skill))
                .toList();

        int matchPercent = requiredSkills.isEmpty()
                ? 100
                : (int) Math.round(100.0 * matched.size() / requiredSkills.size());
        boolean skillsEligible = matchPercent >= SKILL_MATCH_THRESHOLD;

        // A CGPA cutoff only disqualifies a student once we actually know their CGPA -
        // an unfilled profile field should never silently zero out every company.
        boolean cgpaEligible = job.getMinCgpa() == null
                || student.getCgpa() == null
                || student.getCgpa() >= job.getMinCgpa();

        int successProbability = computeSuccessProbability(student, matchPercent, job.getMinCgpa());

        return EligibilityResponse.builder()
                .jobId(job.getId())
                .jobTitle(job.getTitle())
                .company(job.getCompany())
                .matchedSkills(matched)
                .missingSkills(missing)
                .matchPercent(matchPercent)
                .skillsEligible(skillsEligible)
                .requiredCgpa(job.getMinCgpa())
                .studentCgpa(student.getCgpa())
                .cgpaEligible(cgpaEligible)
                .overallEligible(skillsEligible && cgpaEligible)
                .successProbability(successProbability)
                .probabilityLabel(probabilityLabel(successProbability))
                .build();
    }

    /**
     * A transparent, rule-based estimate (not a trained model) combining skill match,
     * CGPA fit, resume quality and mock-interview performance - the same signals the
     * placement-prediction and roadmap features already use, just company-scoped here.
     */
    private int computeSuccessProbability(Student student, int matchPercent, Double requiredCgpa) {
        double cgpaFactor;
        if (requiredCgpa == null) {
            cgpaFactor = 100;
        } else if (student.getCgpa() == null) {
            cgpaFactor = 65; // unknown - neutral, mildly discounted rather than penalized
        } else if (student.getCgpa() >= requiredCgpa) {
            cgpaFactor = 100;
        } else {
            cgpaFactor = Math.max(0, 100 - (requiredCgpa - student.getCgpa()) * 40);
        }

        double atsFactor = student.getResumeScore() != null ? student.getResumeScore() : 55;
        double interviewFactor = student.getMockInterviewScore() != null ? student.getMockInterviewScore() : 50;

        double raw = 0.55 * matchPercent + 0.20 * cgpaFactor + 0.15 * atsFactor + 0.10 * interviewFactor;
        return (int) Math.round(Math.max(0, Math.min(100, raw)));
    }

    private String probabilityLabel(int successProbability) {
        if (successProbability >= 75) {
            return "High";
        }
        if (successProbability >= 50) {
            return "Medium";
        }
        return "Building";
    }

    private Set<String> splitSkills(String raw) {
        if (raw == null || raw.isBlank()) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean containsIgnoreCase(Set<String> haystack, String needle) {
        return haystack.stream().anyMatch(item -> item.equalsIgnoreCase(needle));
    }

    private Student findStudent(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + email));
    }
}
