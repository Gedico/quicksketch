package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "sketches")
public class Sketch {
    @Id
    private String id;
    private String word;

    @Indexed
    private String authorId;
    private String authorUsername;
    private String imageData;
    private Instant createdAt = Instant.now();

    public Sketch() {}

    public Sketch(String word, String authorId, String authorUsername, String imageData) {
        this.word = word;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.imageData = imageData;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getImageData() { return imageData; }
    public void setImageData(String imageData) { this.imageData = imageData; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}