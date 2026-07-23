package com.placenextai.service.impl;

import com.placenextai.dto.AlumniRegisterRequest;
import com.placenextai.dto.AuthResponse;
import com.placenextai.dto.LoginRequest;
import com.placenextai.dto.MeResponse;
import com.placenextai.dto.RecruiterRegisterRequest;
import com.placenextai.dto.StudentRegisterRequest;
import com.placenextai.entity.Admin;
import com.placenextai.entity.Alumni;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.Student;
import com.placenextai.exception.DuplicateResourceException;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.AdminRepository;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.StudentRepository;
import com.placenextai.entity.EventType;
import com.placenextai.service.AuthService;
import com.placenextai.service.EventService;
import com.placenextai.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final AdminRepository adminRepository;
    private final AlumniRepository alumniRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EventService eventService;

    @Override
    @Transactional
    public AuthResponse registerStudent(StudentRegisterRequest request) {
        assertEmailIsFree(request.getEmail());

        Student student = Student.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .college(request.getCollege())
                .branch(request.getBranch())
                .graduationYear(request.getGraduationYear())
                .cgpa(request.getCgpa())
                .skills(request.getSkills())
                .role("ROLE_STUDENT")
                .build();

        Student saved = studentRepository.save(student);
        return buildResponse(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole());
    }

    @Override
    @Transactional
    public AuthResponse registerRecruiter(RecruiterRegisterRequest request) {
        assertEmailIsFree(request.getEmail());

        Recruiter recruiter = Recruiter.builder()
                .companyName(request.getCompanyName())
                .recruiterName(request.getRecruiterName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .designation(request.getDesignation())
                .role("ROLE_RECRUITER")
                .build();

        Recruiter saved = recruiterRepository.save(recruiter);
        return buildResponse(saved.getId(), saved.getRecruiterName(), saved.getEmail(), saved.getRole());
    }

    @Override
    @Transactional
    public AuthResponse registerAlumni(AlumniRegisterRequest request) {
        assertEmailIsFree(request.getEmail());

        Alumni alumni = Alumni.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .currentCompany(request.getCurrentCompany())
                .designation(request.getDesignation())
                .graduationYear(request.getGraduationYear())
                .expertise(request.getExpertise())
                .bio(request.getBio())
                .role("ROLE_ALUMNI")
                .build();

        Alumni saved = alumniRepository.save(alumni);
        return buildResponse(saved.getId(), saved.getFullName(), saved.getEmail(), saved.getRole());
    }

    @Override
    public AuthResponse login(LoginRequest request, String expectedRole) {
        AuthResponse response = login(request);
        if (!response.getRole().equals(expectedRole)) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword()));

        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            Admin found = admin.get();
            return buildResponse(found.getId(), found.getUsername(), found.getEmail(), found.getRole());
        }

        Optional<Recruiter> recruiter = recruiterRepository.findByEmail(email);
        if (recruiter.isPresent()) {
            Recruiter found = recruiter.get();
            return buildResponse(found.getId(), found.getRecruiterName(), found.getEmail(), found.getRole());
        }

        Optional<Alumni> alumni = alumniRepository.findByEmail(email);
        if (alumni.isPresent()) {
            Alumni found = alumni.get();
            return buildResponse(found.getId(), found.getFullName(), found.getEmail(), found.getRole());
        }

        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            Student found = student.get();
            eventService.record(found.getId(), EventType.LOGIN, "Signed in");
            return buildResponse(found.getId(), found.getFullName(), found.getEmail(), found.getRole());
        }

        throw new BadCredentialsException("Invalid email or password");
    }

    @Override
    @Transactional(readOnly = true)
    public MeResponse getCurrentUser(String email) {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            Admin found = admin.get();
            return MeResponse.builder()
                    .id(found.getId())
                    .name(found.getUsername())
                    .email(found.getEmail())
                    .role(found.getRole())
                    .profileCompletion(100)
                    .build();
        }

        Optional<Recruiter> recruiter = recruiterRepository.findByEmail(email);
        if (recruiter.isPresent()) {
            Recruiter found = recruiter.get();
            return MeResponse.builder()
                    .id(found.getId())
                    .name(found.getRecruiterName())
                    .email(found.getEmail())
                    .role(found.getRole())
                    .profileCompletion(recruiterCompletion(found))
                    .build();
        }

        Optional<Alumni> alumni = alumniRepository.findByEmail(email);
        if (alumni.isPresent()) {
            Alumni found = alumni.get();
            return MeResponse.builder()
                    .id(found.getId())
                    .name(found.getFullName())
                    .email(found.getEmail())
                    .role(found.getRole())
                    .profileCompletion(alumniCompletion(found))
                    .build();
        }

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found with email: " + email));

        return MeResponse.builder()
                .id(student.getId())
                .name(student.getFullName())
                .email(student.getEmail())
                .role(student.getRole())
                .profileCompletion(studentCompletion(student))
                .build();
    }

    private int studentCompletion(Student student) {
        int total = 8;
        int filled = 0;
        if (hasText(student.getFullName())) filled++;
        if (hasText(student.getEmail())) filled++;
        if (hasText(student.getPhone())) filled++;
        if (hasText(student.getCollege())) filled++;
        if (hasText(student.getBranch())) filled++;
        if (hasText(student.getSkills())) filled++;
        if (student.getGraduationYear() != null) filled++;
        if (student.getCgpa() != null) filled++;
        return Math.round(filled * 100f / total);
    }

    private int recruiterCompletion(Recruiter recruiter) {
        int total = 4;
        int filled = 0;
        if (hasText(recruiter.getCompanyName())) filled++;
        if (hasText(recruiter.getRecruiterName())) filled++;
        if (hasText(recruiter.getEmail())) filled++;
        if (hasText(recruiter.getDesignation())) filled++;
        return Math.round(filled * 100f / total);
    }

    private int alumniCompletion(Alumni alumni) {
        int total = 5;
        int filled = 0;
        if (hasText(alumni.getFullName())) filled++;
        if (hasText(alumni.getCurrentCompany())) filled++;
        if (hasText(alumni.getDesignation())) filled++;
        if (hasText(alumni.getExpertise())) filled++;
        if (hasText(alumni.getBio())) filled++;
        return Math.round(filled * 100f / total);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private AuthResponse buildResponse(Long id, String name, String email, String role) {
        String token = jwtUtil.generateToken(email, role);
        return AuthResponse.builder()
                .token(token)
                .tokenType(TOKEN_TYPE)
                .id(id)
                .name(name)
                .email(email)
                .role(role)
                .build();
    }

    private void assertEmailIsFree(String email) {
        String normalized = email.toLowerCase();
        boolean taken = studentRepository.existsByEmail(normalized)
                || recruiterRepository.existsByEmail(normalized)
                || adminRepository.existsByEmail(normalized)
                || alumniRepository.existsByEmail(normalized);
        if (taken) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
    }
}
