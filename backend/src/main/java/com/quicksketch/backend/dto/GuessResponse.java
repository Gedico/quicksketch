package com.quicksketch.backend.dto;

public class GuessResponse {

    private boolean correct;
    private String message;

    public GuessResponse() {}

    public GuessResponse(boolean correct, String message) {
        this.correct = correct;
        this.message = message;
    }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}