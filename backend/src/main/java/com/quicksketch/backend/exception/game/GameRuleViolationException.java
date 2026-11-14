package com.quicksketch.backend.exception.game;

public class GameRuleViolationException extends RuntimeException {
    public GameRuleViolationException(String message) {
        super(message);
    }
}