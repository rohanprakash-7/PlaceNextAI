package com.placenextai.service.impl;

import com.placenextai.dto.EligibilityResponse;
import com.placenextai.dto.EligibleCompanySummary;
import com.placenextai.entity.Job;
import com.placenextai.entity.Student;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EligibilityServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private EligibilityServiceImpl eligibilityService;

    private Student studentWith(String skills, Double cgpa) {
        return Student.builder()
                .id(1L)
                .fullName("Test Student")
                .email("student@test.com")
                .skills(skills)
                .cgpa(cgpa)
                .build();
    }

    private Job jobWith(String skillsRequired, Double minCgpa) {
        return Job.builder()
                .id(1L)
                .title("Software Engineer")
                .company("Amazon")
                .skillsRequired(skillsRequired)
                .minCgpa(minCgpa)
                .build();
    }

    @Test
    void fullSkillMatchAndNoCgpaRequirement_isFullyEligible() {
        Student student = studentWith("Java, Spring Boot, SQL", 7.0);
        Job job = jobWith("Java,SQL", null);

        when(studentRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        EligibilityResponse response = eligibilityService.checkForJob("student@test.com", 1L);

        assertThat(response.getMatchPercent()).isEqualTo(100);
        assertThat(response.isSkillsEligible()).isTrue();
        assertThat(response.isCgpaEligible()).isTrue();
        assertThat(response.isOverallEligible()).isTrue();
        assertThat(response.getMissingSkills()).isEmpty();
    }

    @Test
    void belowCgpaRequirement_isNotEligibleEvenWithMatchingSkills() {
        Student student = studentWith("Java,SQL", 6.0);
        Job job = jobWith("Java,SQL", 7.5);

        when(studentRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        EligibilityResponse response = eligibilityService.checkForJob("student@test.com", 1L);

        assertThat(response.isSkillsEligible()).isTrue();
        assertThat(response.isCgpaEligible()).isFalse();
        assertThat(response.isOverallEligible()).isFalse();
    }

    @Test
    void partialSkillMatchBelowThreshold_isNotEligible() {
        Student student = studentWith("Java", null);
        Job job = jobWith("Java,SQL,AWS,System Design,Data Structures", null);

        when(studentRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        EligibilityResponse response = eligibilityService.checkForJob("student@test.com", 1L);

        // 1 of 5 required skills matched = 20%, below the 60% eligibility threshold.
        assertThat(response.getMatchPercent()).isEqualTo(20);
        assertThat(response.isSkillsEligible()).isFalse();
        assertThat(response.isOverallEligible()).isFalse();
        assertThat(response.getMissingSkills()).containsExactlyInAnyOrder("SQL", "AWS", "System Design", "Data Structures");
    }

    @Test
    void unknownStudentCgpa_doesNotBlockEligibilityWhenSkillsMatch() {
        Student student = studentWith("Java,SQL,Data Structures,Algorithms,AWS", null);
        Job job = jobWith("Java,SQL,Data Structures,Algorithms,AWS", 7.0);

        when(studentRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        EligibilityResponse response = eligibilityService.checkForJob("student@test.com", 1L);

        // A blank CGPA profile field must not silently zero out every company - only a
        // known, too-low CGPA should disqualify. See belowCgpaRequirement_isNotEligibleEvenWithMatchingSkills.
        assertThat(response.isCgpaEligible()).isTrue();
        assertThat(response.isOverallEligible()).isTrue();
    }

    @Test
    void listEligibleCompanies_onlyIncludesCompaniesWithAtLeastOneEligibleJob() {
        Student student = studentWith("Java,SQL", 8.0);
        Job eligibleJob = jobWith("Java,SQL", 7.0);
        eligibleJob.setCompany("Amazon");
        Job ineligibleJob = jobWith("Python,Machine Learning,AWS", null);
        ineligibleJob.setCompany("Google");

        when(studentRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(jobRepository.findAll()).thenReturn(List.of(eligibleJob, ineligibleJob));

        List<EligibleCompanySummary> eligibleCompanies = eligibilityService.listEligibleCompanies("student@test.com");

        assertThat(eligibleCompanies).extracting(EligibleCompanySummary::getCompany).containsExactly("Amazon");
    }
}
