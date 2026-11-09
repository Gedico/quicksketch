package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "word_pool")
public class WordPool {
    @Id
    private String id;
    private String word;

    public WordPool() {}
    public WordPool(String word) { this.word = word; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
}