package com.placenextai.service.impl;

import com.placenextai.dto.BadgeResponse;
import com.placenextai.entity.Application;
import com.placenextai.entity.ApplicationStatus;
import com.placenextai.entity.Badge;
import com.placenextai.entity.Job;
import com.placenextai.entity.Recruiter;
import com.placenextai.entity.RecruiterBadge;
import com.placenextai.exception.ResourceNotFoundException;
import com.placenextai.repository.ApplicationRepository;
import com.placenextai.repository.BadgeRepository;
import com.placenextai.repository.JobRepository;
import com.placenextai.repository.RecruiterBadgeRepository;
import com.placenextai.repository.RecruiterRepository;
import com.placenextai.service.RecruiterBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruiterBadgeServiceImpl implements RecruiterBadgeService {

    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final BadgeRepository badgeRepository;
    private final RecruiterBadgeRepository recruiterBadgeRepository;

    @Override
    @Transactional
    public void checkAndAward(Long recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId).orElse(null);
        if (recruiter == null) {
            return;
        }

        List<Job> jobs = jobRepository.findByCompanyIgnoreCaseOrderByCreatedDateDesc(recruiter.getCompanyName());
        if (!jobs.isEmpty()) {
            award(recruiterId, JOB_POSTER);
        }

        List<Application> applications = jobs.isEmpty()
                ? List.of()
                : applicationRepository.findByJobInOrderByAppliedDateDesc(jobs);

        if (applications.size() >= 20) {
            award(recruiterId, ACTIVE_RECRUITER);
        }
        if (applications.stream().anyMatch(application -> application.getStatus() == ApplicationStatus.HIRED)) {
            award(recruiterId, TOP_HIRER);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeResponse> getBadgesForRecruiter(String recruiterEmail) {
        Recruiter recruiter = recruiterRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with email: " + recruiterEmail));

        Map<Long, LocalDateTime> earnedByBadgeId = recruiterBadgeRepository.findByRecruiterId(recruiter.getId()).stream()
                .collect(Collectors.toMap(RecruiterBadge::getBadgeId, RecruiterBadge::getAwardedAt));

        return badgeRepository.findAll().stream()
                .filter(badge -> "RECRUITER".equals(badge.getCategory()))
                .map(badge -> BadgeResponse.builder()
                        .code(badge.getCode())
                        .name(badge.getName())
                        .description(badge.getDescription())
                        .icon(badge.getIcon())
                        .earned(earnedByBadgeId.containsKey(badge.getId()))
                        .awardedAt(earnedByBadgeId.get(badge.getId()))
                        .build())
                .toList();
    }

    private void award(Long recruiterId, String code) {
        Badge badge = badgeRepository.findByCode(code).orElse(null);
        if (badge == null) {
            return;
        }
        if (recruiterBadgeRepository.existsByRecruiterIdAndBadgeId(recruiterId, badge.getId())) {
            return;
        }
        recruiterBadgeRepository.save(RecruiterBadge.builder()
                .recruiterId(recruiterId)
                .badgeId(badge.getId())
                .build());
    }
}
