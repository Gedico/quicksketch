package com.quicksketch.backend.dto.guess;

import java.time.Instant;

public class InProgressGuessResponse {

    private String sketchId;
    private String authorUsername;
    private String imageData;
    private int attemptsUsed;
    private int maxAttempts;
    private Instant startedAt;

    public InProgressGuessResponse(String sketchId, String authorUsername, String imageData,
                                   int attemptsUsed, int maxAttempts, Instant startedAt) {
        this.sketchId = sketchId;
        this.authorUsername = authorUsername;
        this.imageData = imageData;
        this.attemptsUsed = attemptsUsed;
        this.maxAttempts = maxAttempts;
        this.startedAt = startedAt;
    }

    public String getSketchId() { return sketchId; }
    public String getAuthorUsername() { return authorUsername; }
    public String getImageData() { return imageData; }
    public int getAttemptsUsed() { return attemptsUsed; }
    public int getMaxAttempts() { return maxAttempts; }
    public Instant getStartedAt() { return startedAt; }
}