package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "sketches")
public class Sketch {

    @Id
    private String id;
    private String authorUsername;
    private String word;
    private String canvasData;
    private Instant createdAt;
    private boolean solved;

    public Sketch() {
        this.createdAt = Instant.now();
        this.solved = false;
    }

    public Sketch(String authorUsername, String word, String canvasData) {
        this.authorUsername = authorUsername;
        this.word = word;
        this.canvasData = canvasData;
        this.createdAt = Instant.now();
        this.solved = false;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getCanvasData() { return canvasData; }
    public void setCanvasData(String canvasData) { this.canvasData = canvasData; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public boolean isSolved() { return solved; }
    public void setSolved(boolean solved) { this.solved = solved; }
}