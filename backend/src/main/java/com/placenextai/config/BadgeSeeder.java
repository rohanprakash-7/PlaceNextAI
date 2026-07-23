package com.placenextai.config;

import com.placenextai.entity.Badge;
import com.placenextai.repository.BadgeRepository;
import com.placenextai.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BadgeSeeder implements CommandLineRunner {

    private final BadgeRepository badgeRepository;

    @Override
    public void run(String... args) {
        seed(BadgeService.FIRST_APPLICATION, "First Application", "Submitted your first job application.", "FiSend");
        seed(BadgeService.RESUME_REFINED, "Resume Refined", "Uploaded three or more resume versions.", "FiFileText");
        seed(BadgeService.CONSISTENCY_STREAK, "Consistency Streak", "Completed four or more roadmap items within a month.", "FiTrendingUp");
        seed(BadgeService.INTERVIEW_READY, "Interview Ready", "Completed a mock interview.", "FiMic");
        seed(BadgeService.OFFER_RECEIVED, "Offer Received", "Reached the offer stage on an application.", "FiAward");
    }

    private void seed(String code, String name, String description, String icon) {
        if (badgeRepository.findByCode(code).isEmpty()) {
            badgeRepository.save(Badge.builder()
                    .code(code)
                    .name(name)
                    .description(description)
                    .icon(icon)
                    .build());
        }
    }
}
