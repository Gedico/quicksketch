package com.quicksketch.backend.dto.guess;

import jakarta.validation.constraints.NotBlank;

public class GuessAttemptRequest {

    @NotBlank(message = "Inserisci una parola")
    private String word;

    public GuessAttemptRequest() {}

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
}