package com.quicksketch.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class GuessRequest {

    @NotBlank(message = "Il tentativo non può essere vuoto")
    private String guess;

    public GuessRequest() {}

    public GuessRequest(String guess) {
        this.guess = guess;
    }

    public String getGuess() { return guess; }
    public void setGuess(String guess) { this.guess = guess; }
}