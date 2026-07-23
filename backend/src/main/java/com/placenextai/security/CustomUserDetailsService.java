package com.placenextai.security;

import com.placenextai.entity.Admin;
import com.placenextai.entity.Alumni;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.Student;
import com.placenextai.repository.AdminRepository;
import com.placenextai.repository.AlumniRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final AdminRepository adminRepository;
    private final AlumniRepository alumniRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            return buildUser(admin.get().getEmail(), admin.get().getPassword(), admin.get().getRole());
        }

        Optional<Recruiter> recruiter = recruiterRepository.findByEmail(email);
        if (recruiter.isPresent()) {
            return buildUser(recruiter.get().getEmail(), recruiter.get().getPassword(), recruiter.get().getRole());
        }

        Optional<Alumni> alumni = alumniRepository.findByEmail(email);
        if (alumni.isPresent()) {
            return buildUser(alumni.get().getEmail(), alumni.get().getPassword(), alumni.get().getRole());
        }

        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            return buildUser(student.get().getEmail(), student.get().getPassword(), student.get().getRole());
        }

        throw new UsernameNotFoundException("No account found with email: " + email);
    }

    private UserDetails buildUser(String email, String password, String role) {
        return new User(email, password, List.of(new SimpleGrantedAuthority(role)));
    }
}
