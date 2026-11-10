package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "guess_attempts")
@CompoundIndex(name = "user_sketch_idx", def = "{'username': 1, 'sketchId': 1}", unique = true)
public class GuessAttempt {

    @Id
    private String id;

    private String username; // Utente che sta provando a indovinare
    private String sketchId; // ID dello sketch da indovinare

    // --- STATO DELLA PARTITA ---
    private List<String> attempts = new ArrayList<>();
    private boolean isGuessed = false;
    private boolean isCompleted = false;

    private Instant updatedAt = Instant.now();

    // --- COSTRUTTORI ---
    public GuessAttempt() {}

    public GuessAttempt(String username, String sketchId) {
        this.username = username;
        this.sketchId = sketchId;
    }

    // --- GETTER E SETTER  ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSketchId() { return sketchId; }
    public void setSketchId(String sketchId) { this.sketchId = sketchId; }
    public List<String> getAttempts() { return attempts; }
    public void setAttempts(List<String> attempts) { this.attempts = attempts; }
    public boolean isGuessed() { return isGuessed; }
    public void setGuessed(boolean guessed) { isGuessed = guessed; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Metodo helper per aggiornare la data prima di ogni salvataggio
    public void touch() {
        this.updatedAt = Instant.now();
    }
}