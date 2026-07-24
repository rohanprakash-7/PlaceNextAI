package com.placenextai.repository;

import com.placenextai.entity.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlumniRepository extends JpaRepository<Alumni, Long> {

    Optional<Alumni> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT a FROM Alumni a WHERE "
            + "(:company IS NULL OR LOWER(a.currentCompany) = LOWER(:company)) AND "
            + "(:search IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(a.expertise) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "OR LOWER(a.currentCompany) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Alumni> search(@Param("company") String company, @Param("search") String search);

    @Query("SELECT DISTINCT a.currentCompany FROM Alumni a WHERE a.currentCompany IS NOT NULL ORDER BY a.currentCompany")
    List<String> findDistinctCompanies();
}
