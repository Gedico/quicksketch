package com.quicksketch.backend.dto.sketch;

import jakarta.validation.constraints.NotBlank;

public class CompleteSketchRequest {
    @NotBlank(message = "Il disegno non può essere vuoto")
    private String imageData;

    public String getImageData() { return imageData; }
    public void setImageData(String imageData) { this.imageData = imageData; }
}