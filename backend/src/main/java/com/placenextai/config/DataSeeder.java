package com.placenextai.config;

import com.placenextai.entity.*;
import com.placenextai.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final AlumniRepository alumniRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        Student student = seedStudent();
        Recruiter recruiter = seedRecruiter();
        seedAlumni();
        seedJobsAndApplication(student, recruiter);
    }

    private void seedAdmin() {
        if (adminRepository.count() == 0) {
            adminRepository.save(Admin.builder()
                    .username("placenextadmin")
                    .email("admin@placenextai.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role("ROLE_ADMIN")
                    .build());
        }
    }

    private Student seedStudent() {
        return studentRepository.findByEmail("ananya@college.edu").orElseGet(() ->
                studentRepository.save(Student.builder()
                        .fullName("Ananya Sharma")
                        .email("ananya@college.edu")
                        .password(passwordEncoder.encode("Student@123"))
                        .phone("9876543210")
                        .college("JNTU Hyderabad")
                        .branch("Computer Science - Data Science")
                        .graduationYear(2027)
                        .cgpa(8.6)
                        .skills("Java,Spring Boot,React,MySQL,Python")
                        .role("ROLE_STUDENT")
                        .build()));
    }

    private Recruiter seedRecruiter() {
        return recruiterRepository.findByEmail("vikram@technova.com").orElseGet(() ->
                recruiterRepository.save(Recruiter.builder()
                        .companyName("TechNova")
                        .recruiterName("Vikram Singh")
                        .email("vikram@technova.com")
                        .password(passwordEncoder.encode("Recruit@123"))
                        .designation("Talent Acquisition Lead")
                        .role("ROLE_RECRUITER")
                        .build()));
    }

    private void seedAlumni() {
        if (alumniRepository.count() == 0) {
            alumniRepository.save(Alumni.builder()
                    .fullName("Rhea Kapoor")
                    .email("rhea.alumni@placenextai.com")
                    .password(passwordEncoder.encode("Alumni@123"))
                    .currentCompany("TechNova")
                    .designation("Senior Software Engineer")
                    .graduationYear(2021)
                    .expertise("Java,Spring Boot,System Design,Interview Prep")
                    .bio("JNTU Hyderabad alum, now on the backend platform team at TechNova. Happy to help with interview prep and career questions.")
                    .role("ROLE_ALUMNI")
                    .build());
        }
    }

    private void seedJobsAndApplication(Student student, Recruiter recruiter) {
        if (jobRepository.count() > 0) {
            return;
        }

        Job backendJob = jobRepository.save(Job.builder()
                .title("Backend Engineer Intern")
                .company(recruiter.getCompanyName())
                .location("Hyderabad, India")
                .description("Work on Spring Boot microservices powering our recruitment platform. Build REST APIs, write unit tests and collaborate with the AI team.")
                .salary("6-8 LPA")
                .skillsRequired("Java,Spring Boot,MySQL,REST APIs")
                .build());

        jobRepository.save(Job.builder()
                .title("Frontend Developer")
                .company(recruiter.getCompanyName())
                .location("Remote")
                .description("Build modern, responsive dashboards using React, Tailwind CSS and Framer Motion for our SaaS product suite.")
                .salary("5-7 LPA")
                .skillsRequired("React,JavaScript,Tailwind CSS,HTML,CSS")
                .build());

        jobRepository.save(Job.builder()
                .title("Data Analyst")
                .company("InsightWorks")
                .location("Bengaluru, India")
                .description("Analyze placement and hiring funnel data, build dashboards and support data-driven recruitment decisions.")
                .salary("4.5-6 LPA")
                .skillsRequired("SQL,Python,Excel,Power BI")
                .build());

        jobRepository.save(Job.builder()
                .title("Machine Learning Engineer")
                .company("NeuronLabs")
                .location("Pune, India")
                .description("Develop NLP models for resume parsing and candidate-job matching using transformers and scikit-learn.")
                .salary("10-14 LPA")
                .skillsRequired("Python,PyTorch,Transformers,scikit-learn,NLP")
                .build());

        if (!applicationRepository.existsByStudentAndJob(student, backendJob)) {
            applicationRepository.save(Application.builder()
                    .student(student)
                    .job(backendJob)
                    .status(ApplicationStatus.APPLIED)
                    .build());
        }
    }
}
