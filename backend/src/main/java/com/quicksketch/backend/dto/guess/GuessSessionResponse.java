package com.quicksketch.backend.dto.guess;

import java.time.Instant;
import java.util.List;

public class GuessSessionResponse {

    private String sketchId;
    private String authorUsername;
    private String imageData;
    private Instant sketchCreatedAt;
    private String status; // "IN_PROGRESS", "WON", "LOST"
    private int attemptsUsed;
    private int attemptsRemaining;
    private int maxAttempts;
    private List<String> attemptedWords;
    private String secretWord; // Popolato SOLO quando status è "WON" o "LOST"

    public GuessSessionResponse(String sketchId, String authorUsername, String imageData,
                                Instant sketchCreatedAt, String status, int attemptsUsed,
                                int attemptsRemaining, int maxAttempts,
                                List<String> attemptedWords, String secretWord) {
        this.sketchId = sketchId;
        this.authorUsername = authorUsername;
        this.imageData = imageData;
        this.sketchCreatedAt = sketchCreatedAt;
        this.status = status;
        this.attemptsUsed = attemptsUsed;
        this.attemptsRemaining = attemptsRemaining;
        this.maxAttempts = maxAttempts;
        this.attemptedWords = attemptedWords;
        this.secretWord = secretWord;
    }

    public String getSketchId() { return sketchId; }
    public String getAuthorUsername() { return authorUsername; }
    public String getImageData() { return imageData; }
    public Instant getSketchCreatedAt() { return sketchCreatedAt; }
    public String getStatus() { return status; }
    public int getAttemptsUsed() { return attemptsUsed; }
    public int getAttemptsRemaining() { return attemptsRemaining; }
    public int getMaxAttempts() { return maxAttempts; }
    public List<String> getAttemptedWords() { return attemptedWords; }
    public String getSecretWord() { return secretWord; }
}