package com.placenextai.repository;

import com.placenextai.entity.MentorMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorMessageRepository extends JpaRepository<MentorMessage, Long> {

    List<MentorMessage> findByRequestIdOrderBySentAtAsc(Long requestId);
}
