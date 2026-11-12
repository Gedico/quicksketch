package com.quicksketch.backend.dto.sketch;

import jakarta.validation.constraints.NotBlank;

public class StartSketchRequest {
    @NotBlank(message = "Devi selezionare una parola")
    private String word;

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
}