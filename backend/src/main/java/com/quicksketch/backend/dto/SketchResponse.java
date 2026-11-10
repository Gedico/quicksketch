package com.quicksketch.backend.dto;

import com.quicksketch.backend.model.Sketch;
import java.time.Instant;

public class SketchResponse {
    private String id;
    private String authorUsername;
    private String canvasData;
    private Instant createdAt;
    private boolean solved;

    public SketchResponse(Sketch sketch) {
        this.id = sketch.getId();
        this.authorUsername = sketch.getAuthorUsername();
        this.canvasData = sketch.getCanvasData();
        this.createdAt = sketch.getCreatedAt();
        this.solved = sketch.isSolved();
    }

    public String getId() { return id; }
    public String getAuthorUsername() { return authorUsername; }
    public String getCanvasData() { return canvasData; }
    public Instant getCreatedAt() { return createdAt; }
    public boolean isSolved() { return solved; }
}