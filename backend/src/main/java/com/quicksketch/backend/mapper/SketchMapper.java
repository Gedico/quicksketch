package com.quicksketch.backend.mapper;

import com.quicksketch.backend.dto.sketch.SketchResponse;
import com.quicksketch.backend.model.Sketch;
import org.springframework.stereotype.Component;

@Component
public class SketchMapper {

    public SketchResponse toResponse(Sketch sketch) {
        if (sketch == null) {
            return null;
        }
        return new SketchResponse(
                sketch.getId(),
                sketch.getAuthorUsername(),
                sketch.getImageData(),
                sketch.getCreatedAt()
        );
    }
}