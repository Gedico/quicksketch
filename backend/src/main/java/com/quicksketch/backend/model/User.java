package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    // Statistiche appiattite
    private int sketchesCreated = 0;
    private int sketchesGuessedByOthers = 0;
    private int sketchesPlayedByOthers = 0;
    private int wordsGuessed = 0;
    private int wordsFailed = 0;
    private int totalGuessAttemptsUsed = 0;

    // Data di creazione immutabile
    private Instant createdAt = Instant.now();

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // --- GETTER E SETTER ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getSketchesCreated() { return sketchesCreated; }
    public void setSketchesCreated(int sketchesCreated) { this.sketchesCreated = sketchesCreated; }

    public int getSketchesGuessedByOthers() { return sketchesGuessedByOthers; }
    public void setSketchesGuessedByOthers(int sketchesGuessedByOthers) { this.sketchesGuessedByOthers = sketchesGuessedByOthers; }

    public int getSketchesPlayedByOthers() { return sketchesPlayedByOthers; }
    public void setSketchesPlayedByOthers(int sketchesPlayedByOthers) { this.sketchesPlayedByOthers = sketchesPlayedByOthers; }

    public int getWordsGuessed() { return wordsGuessed; }
    public void setWordsGuessed(int wordsGuessed) { this.wordsGuessed = wordsGuessed; }

    public int getWordsFailed() { return wordsFailed; }
    public void setWordsFailed(int wordsFailed) { this.wordsFailed = wordsFailed; }

    public int getTotalGuessAttemptsUsed() { return totalGuessAttemptsUsed; }
    public void setTotalGuessAttemptsUsed(int totalGuessAttemptsUsed) { this.totalGuessAttemptsUsed = totalGuessAttemptsUsed; }

    // Solo Getter per la data di creazione (nessun Setter)
    public Instant getCreatedAt() { return createdAt; }
}