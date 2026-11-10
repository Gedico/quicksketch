package com.quicksketch.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateSketchRequest {

    @NotBlank(message = "I dati della tela non possono essere vuoti")
    private String canvasData;

    @NotBlank(message = "La parola associata è obbligatoria")
    private String word;

    public CreateSketchRequest() {}

    public CreateSketchRequest(String canvasData, String word) {
        this.canvasData = canvasData;
        this.word = word;
    }

    public String getCanvasData() { return canvasData; }
    public void setCanvasData(String canvasData) { this.canvasData = canvasData; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
}