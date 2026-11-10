package com.quicksketch.backend.service.impl;

import com.quicksketch.backend.dto.CreateSketchRequest;
import com.quicksketch.backend.dto.SketchResponse;
import com.quicksketch.backend.model.Sketch;
import com.quicksketch.backend.repository.SketchRepository;
import com.quicksketch.backend.service.SketchService;
import com.quicksketch.backend.service.WordService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SketchServiceImpl implements SketchService {

    private final SketchRepository sketchRepository;
    private final WordService wordService;

    public SketchServiceImpl(SketchRepository sketchRepository, WordService wordService) {
        this.sketchRepository = sketchRepository;
        this.wordService = wordService;
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
        // Se per qualsiasi motivo la parola non viene passata nel request, la peschiamo dal WordService
        String targetWord = (request.getWord() != null && !request.getWord().isBlank())
                ? request.getWord().trim().toLowerCase()
                : wordService.getRandomWord();

        Sketch sketch = new Sketch(authorUsername, targetWord, request.getCanvasData());
        Sketch savedSketch = sketchRepository.save(sketch);

        return new SketchResponse(savedSketch);
    }
}