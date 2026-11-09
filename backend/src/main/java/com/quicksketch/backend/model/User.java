package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;
    private Set<String> roles = new HashSet<>();
    private UserStats stats = new UserStats();
    private Instant createdAt = Instant.now();

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles.add("ROLE_USER");
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public UserStats getStats() { return stats; }
    public void setStats(UserStats stats) { this.stats = stats; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static class UserStats {
        private int sketchesCreated = 0;
        private int sketchesGuessedByOthers = 0;
        private int sketchesPlayedByOthers = 0;
        private int wordsGuessed = 0;
        private int wordsFailed = 0;
        private int totalGuessAttemptsUsed = 0;

        public int getSketchesCreated() { return sketchesCreated; }
        public void setSketchesCreated(int v) { this.sketchesCreated = v; }
        public int getSketchesGuessedByOthers() { return sketchesGuessedByOthers; }
        public void setSketchesGuessedByOthers(int v) { this.sketchesGuessedByOthers = v; }
        public int getSketchesPlayedByOthers() { return sketchesPlayedByOthers; }
        public void setSketchesPlayedByOthers(int v) { this.sketchesPlayedByOthers = v; }
        public int getWordsGuessed() { return wordsGuessed; }
        public void setWordsGuessed(int v) { this.wordsGuessed = v; }
        public int getWordsFailed() { return wordsFailed; }
        public void setWordsFailed(int v) { this.wordsFailed = v; }
        public int getTotalGuessAttemptsUsed() { return totalGuessAttemptsUsed; }
        public void setTotalGuessAttemptsUsed(int v) { this.totalGuessAttemptsUsed = v; }
    }
}