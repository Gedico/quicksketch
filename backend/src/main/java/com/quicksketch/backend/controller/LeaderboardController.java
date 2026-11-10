package com.quicksketch.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @GetMapping("/me")
    public ResponseEntity<String> getMyStats(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Autenticazione riuscita per l'utente: " + username);
    }
}