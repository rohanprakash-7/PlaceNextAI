package com.placenextai.repository;

import com.placenextai.entity.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlumniRepository extends JpaRepository<Alumni, Long> {

    Optional<Alumni> findByEmail(String email);

    boolean existsByEmail(String email);
}
