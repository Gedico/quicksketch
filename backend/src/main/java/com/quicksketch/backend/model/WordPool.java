package com.quicksketch.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "words")
public class WordPool {

    @Id
    private String idWord;

    @Indexed(unique = true)
    private String word;

    // --- COSTRUTTORI ---
    public WordPool() {}

    public WordPool(String word) {
        this.word = word;
    }

    // --- GETTER E SETTER ---
    public String getIdWord() { return idWord; }
    public void setIdWord(String idWord) { this.idWord = idWord; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
}