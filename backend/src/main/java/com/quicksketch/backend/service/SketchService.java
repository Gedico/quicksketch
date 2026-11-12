package com.quicksketch.backend.service;

import com.quicksketch.backend.dto.sketch.*;
import java.util.List;

public interface SketchService {
    SketchResponse startSketch(String authorUsername, StartSketchRequest request);
    SketchResponse completeSketch(String sketchId, String authorUsername, CompleteSketchRequest request);
    List<SketchResponse> getAllSketches();
}