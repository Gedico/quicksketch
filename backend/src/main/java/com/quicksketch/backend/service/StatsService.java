package com.quicksketch.backend.service;

import com.quicksketch.backend.dto.stats.GlobalLeaderboardsResponse;
import com.quicksketch.backend.dto.stats.UserProfileStatsResponse;

public interface StatsService {

    GlobalLeaderboardsResponse getGlobalLeaderboards();

    UserProfileStatsResponse getPersonalStats(String username);
}