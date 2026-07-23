package com.placenextai.service;

import com.placenextai.dto.PlacementPredictionResponse;
import com.placenextai.entity.Student;

public interface PlacementPredictionService {

    PlacementPredictionResponse getPrediction(String studentEmail);

    PlacementPredictionResponse recompute(String studentEmail);

    PlacementPredictionResponse getOrComputeLatest(Student student);
}
