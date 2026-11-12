package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "guesses")
@CompoundIndex(name = "user_sketch_unique", def = "{'username': 1, 'sketchId': 1}", unique = true)
public class Guess {

    @Id
    private String id;
    private String sketchId;
    private String username;
    private String guessedWord;
    private boolean correct;
    private LocalDateTime attemptedAt;

    public Guess() {
        this.attemptedAt = LocalDateTime.now();
    }

    public Guess(String sketchId, String username, String guessedWord, boolean correct) {
        this.sketchId = sketchId;
        this.username = username;
        this.guessedWord = guessedWord;
        this.correct = correct;
        this.attemptedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSketchId() { return sketchId; }
    public void setSketchId(String sketchId) { this.sketchId = sketchId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getGuessedWord() { return guessedWord; }
    public void setGuessedWord(String guessedWord) { this.guessedWord = guessedWord; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}