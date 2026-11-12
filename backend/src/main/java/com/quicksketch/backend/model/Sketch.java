package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "sketches")
public class Sketch {

    @Id
    private String id;
    private String authorUsername;
    private String word;
    private String imageData;
    private boolean completed;
    private Instant createdAt;

    public Sketch() {
        this.createdAt = Instant.now();
        this.completed = false;
    }

    public Sketch(String authorUsername, String word) {
        this.authorUsername = authorUsername;
        this.word = word;
        this.imageData = null;
        this.completed = false;
        this.createdAt = Instant.now();
    }

    // Getter e Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getImageData() { return imageData; }
    public void setImageData(String imageData) { this.imageData = imageData; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }


//---------------------  METODI AUSILIARI  -------------------------------------------------
    private List<String> playedByUsernames = new ArrayList<>();

    public List<String> getPlayedByUsernames() {
        if (playedByUsernames == null) {
            playedByUsernames = new ArrayList<>();
        }
        return playedByUsernames;
    }

    public void setPlayedByUsernames(List<String> playedByUsernames) {
        this.playedByUsernames = playedByUsernames;
    }
}