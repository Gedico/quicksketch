package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "guesses")
@CompoundIndex(name = "user_sketch_unique", def = "{'username': 1, 'sketchId': 1}", unique = true)
public class Guess {

    @Id
    private String id;
    private String sketchId;
    private String username;
    private int attemptsUsed = 0;
    private List<String> attemptedWords = new ArrayList<>();
    private boolean finished = false;
    private boolean correct = false;
    private Instant startedAt = Instant.now();
    private Instant finishedAt;

    public Guess() {}

    public Guess(String sketchId, String username) {
        this.sketchId = sketchId;
        this.username = username;
        this.attemptsUsed = 0;
        this.attemptedWords = new ArrayList<>();
        this.finished = false;
        this.correct = false;
        this.startedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSketchId() { return sketchId; }
    public void setSketchId(String sketchId) { this.sketchId = sketchId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getAttemptsUsed() { return attemptsUsed; }
    public void setAttemptsUsed(int attemptsUsed) { this.attemptsUsed = attemptsUsed; }

    public List<String> getAttemptedWords() {
        if (attemptedWords == null) {
            attemptedWords = new ArrayList<>();
        }
        return attemptedWords;
    }
    public void setAttemptedWords(List<String> attemptedWords) { this.attemptedWords = attemptedWords; }

    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getFinishedAt() { return finishedAt; }
    public void setFinishedAt(Instant finishedAt) { this.finishedAt = finishedAt; }
}