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
    private String authorUsername;
    private String imageData; // Stringa Base64 del disegno estratto dalla Canvas HTML5
    private Instant createdAt = Instant.now();

    // --- COSTRUTTORI ---
    public Sketch() {}

    public Sketch(String word, String authorId, String authorUsername, String imageData) {
        this.word = word;
        this.authorUsername = authorUsername;
        this.imageData = imageData;
    }

    // --- GETTER E SETTER  ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getImageData() { return imageData; }
    public void setImageData(String imageData) { this.imageData = imageData; }
    public Instant getCreatedAt() { return createdAt; }
}}