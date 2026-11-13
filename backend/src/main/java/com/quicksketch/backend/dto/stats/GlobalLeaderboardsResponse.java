package com.quicksketch.backend.dto.stats;

import java.util.List;

public class GlobalLeaderboardsResponse {

    private List<SketcherLeaderboardEntry> topSketchers;
    private List<GuesserLeaderboardEntry> topGuessers;

    public GlobalLeaderboardsResponse(List<SketcherLeaderboardEntry> topSketchers,
                                      List<GuesserLeaderboardEntry> topGuessers) {
        this.topSketchers = topSketchers;
        this.topGuessers = topGuessers;
    }

    public List<SketcherLeaderboardEntry> getTopSketchers() { return topSketchers; }
    public List<GuesserLeaderboardEntry> getTopGuessers() { return topGuessers; }
}