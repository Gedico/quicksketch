package com.quicksketch.backend.dto.sketch;

import java.time.Instant;

public class SketchResponse {
    private String id;
    private String authorUsername;
    private String imageData;
    private Instant createdAt;

    public SketchResponse(String id, String authorUsername, String imageData, Instant createdAt) {
        this.id = id;
        this.authorUsername = authorUsername;
        this.imageData = imageData;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getAuthorUsername() { return authorUsername; }
    public String getImageData() { return imageData; }
    public Instant getCreatedAt() { return createdAt; }
}