package com.quicksketch.backend.service.impl;

import com.quicksketch.backend.dto.CreateSketchRequest;
import com.quicksketch.backend.dto.SketchResponse;
import com.quicksketch.backend.model.Sketch;
import com.quicksketch.backend.repository.SketchRepository;
import com.quicksketch.backend.service.SketchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SketchServiceImpl implements SketchService {

    private final SketchRepository sketchRepository;

    public SketchServiceImpl(SketchRepository sketchRepository) {
        this.sketchRepository = sketchRepository;
    }

    @Override
    public List<SketchResponse> getAllSketches() {
        return sketchRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(SketchResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public SketchResponse createSketch(String authorUsername, CreateSketchRequest request) {
        Sketch sketch = new Sketch(authorUsername, request.getWord(), request.getCanvasData());
        Sketch savedSketch = sketchRepository.save(sketch);
        return new SketchResponse(savedSketch);
    }
}