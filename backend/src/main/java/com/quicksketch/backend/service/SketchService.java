package com.quicksketch.backend.service;

import com.quicksketch.backend.dto.CreateSketchRequest;
import com.quicksketch.backend.dto.SketchResponse;

import java.util.List;

public interface SketchService {
    List<SketchResponse> getAllSketches();
    SketchResponse createSketch(String authorUsername, CreateSketchRequest request);
}