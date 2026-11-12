package com.quicksketch.backend.controller;

import com.quicksketch.backend.dto.guess.GuessAttemptRequest;
import com.quicksketch.backend.dto.guess.GuessSessionResponse;
import com.quicksketch.backend.dto.guess.InProgressGuessResponse;
import com.quicksketch.backend.service.GuessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guesses")
public class GuessController {

    private final GuessService guessService;

    public GuessController(GuessService guessService) {
        this.guessService = guessService;
    }

    @PostMapping("/start/{sketchId}")
    public ResponseEntity<?> startOrResumeGuess(@PathVariable String sketchId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(guessService.startOrResumeGuess(authentication.getName(), sketchId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{sketchId}/attempt")
    public ResponseEntity<?> submitAttempt(@PathVariable String sketchId,
                                           @Valid @RequestBody GuessAttemptRequest request,
                                           Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(guessService.submitAttempt(authentication.getName(), sketchId, request.getWord()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/{sketchId}/surrender")
    public ResponseEntity<?> surrenderGuess(@PathVariable String sketchId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            return ResponseEntity.ok(guessService.surrenderGuess(authentication.getName(), sketchId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<InProgressGuessResponse>> getInProgressGuesses(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(guessService.getInProgressGuesses(authentication.getName()));
    }
}