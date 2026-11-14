package com.quicksketch.backend.controller;

import com.quicksketch.backend.dto.stats.UserProfileStatsResponse;
import com.quicksketch.backend.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final StatsService statsService;

    public UserController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/me/stats")
    public ResponseEntity<UserProfileStatsResponse> getMyStats(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(statsService.getPersonalStats(authentication.getName()));
    }
}