package com.quicksketch.backend.exception.game;

public class InvalidSketchDataException extends RuntimeException {
    public InvalidSketchDataException(String message) {
        super(message);
    }
}