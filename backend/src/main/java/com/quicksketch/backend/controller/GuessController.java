package com.quicksketch.backend.controller;

import com.quicksketch.backend.dto.guess.GuessAttemptRequest;
import com.quicksketch.backend.dto.guess.GuessSessionResponse;
import com.quicksketch.backend.dto.guess.InProgressGuessResponse;
import com.quicksketch.backend.exception.common.UnauthorizedActionException;
import com.quicksketch.backend.service.GuessService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guesses")
public class GuessController {

    private final GuessService guessService;

    public GuessController(GuessService guessService) {
        this.guessService = guessService;
    }

    @PostMapping("/start/{sketchId}")
    public ResponseEntity<GuessSessionResponse> startOrResumeGuess(@PathVariable String sketchId,
                                                                   Authentication authentication) {
        String username = extractAuthenticatedUsername(authentication);
        return ResponseEntity.ok(guessService.startOrResumeGuess(username, sketchId));
    }

    @PostMapping("/{sketchId}/attempt")
    public ResponseEntity<GuessSessionResponse> submitAttempt(@PathVariable String sketchId,
                                                              @Valid @RequestBody GuessAttemptRequest request,
                                                              Authentication authentication) {
        String username = extractAuthenticatedUsername(authentication);
        return ResponseEntity.ok(guessService.submitAttempt(username, sketchId, request.getWord()));
    }

    @PostMapping("/{sketchId}/surrender")
    public ResponseEntity<GuessSessionResponse> surrenderGuess(@PathVariable String sketchId,
                                                               Authentication authentication) {
        String username = extractAuthenticatedUsername(authentication);
        return ResponseEntity.ok(guessService.surrenderGuess(username, sketchId));
    }

    @GetMapping("/in-progress")
    public ResponseEntity<List<InProgressGuessResponse>> getInProgressGuesses(Authentication authentication) {
        String username = extractAuthenticatedUsername(authentication);
        return ResponseEntity.ok(guessService.getInProgressGuesses(username));
    }

    private String extractAuthenticatedUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedActionException("Devi effettuare l'accesso per eseguire questa operazione.");
        }
        return authentication.getName();
    }
}