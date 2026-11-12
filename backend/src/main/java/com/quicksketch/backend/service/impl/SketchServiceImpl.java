package com.quicksketch.backend.service.impl;

import com.quicksketch.backend.dto.sketch.*;
import com.quicksketch.backend.model.Sketch;
import com.quicksketch.backend.repository.SketchRepository;
import com.quicksketch.backend.service.SketchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SketchServiceImpl implements SketchService {

    private final SketchRepository sketchRepository;

    public SketchServiceImpl(SketchRepository sketchRepository) {
        this.sketchRepository = sketchRepository;
    }

    @Override
    public SketchResponse startSketch(String authorUsername, StartSketchRequest request) {
        Sketch sketch = new Sketch(
                authorUsername,
                request.getWord().trim().toLowerCase()
        );

        Sketch saved = sketchRepository.save(sketch);

        return new SketchResponse(
                saved.getId(),
                saved.getAuthorUsername(),
                saved.getImageData(),
                saved.getCreatedAt()
        );
    }

    @Override
    public SketchResponse completeSketch(String sketchId, String authorUsername, CompleteSketchRequest request) {
        Sketch sketch = sketchRepository.findById(sketchId)
                .orElseThrow(() -> new IllegalArgumentException("Sketch non trovato"));

        if (!sketch.getAuthorUsername().equals(authorUsername)) {
            throw new SecurityException("Non sei autorizzato a modificare questo sketch");
        }

        if (sketch.isCompleted()) {
            throw new IllegalStateException("Questo sketch è già stato completato");
        }

        sketch.setImageData(request.getImageData());
        sketch.setCompleted(true);

        Sketch updated = sketchRepository.save(sketch);

        return new SketchResponse(
                updated.getId(),
                updated.getAuthorUsername(),
                updated.getImageData(),
                updated.getCreatedAt()
        );
    }

    @Override
    public List<SketchResponse> getAllSketches() {
        return sketchRepository.findAllByCompletedTrueOrderByCreatedAtDesc()
                .stream()
                .map(s -> new SketchResponse(
                        s.getId(),
                        s.getAuthorUsername(),
                        s.getImageData(),
                        s.getCreatedAt()
                ))
                .toList();
    }
}