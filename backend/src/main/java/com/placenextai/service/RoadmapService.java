package com.placenextai.service;

import com.placenextai.dto.RoadmapResponse;

public interface RoadmapService {

    RoadmapResponse generate(String studentEmail, String targetCompany);

    RoadmapResponse getActive(String studentEmail);

    RoadmapResponse completeItem(String studentEmail, Long itemId);
}
