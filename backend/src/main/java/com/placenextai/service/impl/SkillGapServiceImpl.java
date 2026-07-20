package com.placenextai.service.impl;

import com.placenextai.dto.SkillGapResponse;
import com.placenextai.entity.Job;
import com.placenextai.entity.Student;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.service.SkillGapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillGapServiceImpl implements SkillGapService {

    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional(readOnly = true)
    public SkillGapResponse analyze(String studentEmail, String targetCompany) {
        Student student = studentRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with email: " + studentEmail));

        Set<String> current = splitSkills(student.getSkills());

        List<Job> relevantJobs = (targetCompany == null || targetCompany.isBlank())
                ? jobRepository.findAllByOrderByCreatedDateDesc()
                : jobRepository.findByCompanyIgnoreCaseOrderByCreatedDateDesc(targetCompany);

        if (relevantJobs.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No job postings found" + (targetCompany != null ? " for " + targetCompany : ""));
        }

        Set<String> required = relevantJobs.stream()
                .flatMap(job -> splitSkills(job.getSkillsRequired()).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<String> missing = required.stream()
                .filter(skill -> !containsIgnoreCase(current, skill))
                .toList();

        int coverage = required.isEmpty()
                ? 100
                : (int) Math.round(100.0 * (required.size() - missing.size()) / required.size());

        return SkillGapResponse.builder()
                .targetCompany(targetCompany == null || targetCompany.isBlank() ? "All companies" : targetCompany)
                .currentSkills(new ArrayList<>(current))
                .requiredSkills(new ArrayList<>(required))
                .missingSkills(missing)
                .coveragePercent(coverage)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listTargetCompanies() {
        return jobRepository.findDistinctCompanies();
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
}
