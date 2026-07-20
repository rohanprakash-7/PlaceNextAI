package com.placenextai.repository;

import com.placenextai.entity.ScoreConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreConfigRepository extends JpaRepository<ScoreConfig, Long> {

    Optional<ScoreConfig> findTopByOrderByIdAsc();
}
