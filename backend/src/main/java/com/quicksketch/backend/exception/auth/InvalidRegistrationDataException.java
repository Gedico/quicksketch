package com.quicksketch.backend.exception.auth;

public class InvalidRegistrationDataException extends RuntimeException {
    public InvalidRegistrationDataException(String message) {
        super(message);
    }
}