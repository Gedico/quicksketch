package com.quicksketch.backend.controller;

import com.quicksketch.backend.dto.stats.GlobalLeaderboardsResponse;
import com.quicksketch.backend.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final StatsService statsService;

    public LeaderboardController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/global")
    public ResponseEntity<GlobalLeaderboardsResponse> getGlobalLeaderboards() {
        return ResponseEntity.ok(statsService.getGlobalLeaderboards());
    }
}