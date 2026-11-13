package com.quicksketch.backend.service.impl;

import com.quicksketch.backend.dto.stats.GlobalLeaderboardsResponse;
import com.quicksketch.backend.dto.stats.GuesserLeaderboardEntry;
import com.quicksketch.backend.dto.stats.SketcherLeaderboardEntry;
import com.quicksketch.backend.dto.stats.UserProfileStatsResponse;
import com.quicksketch.backend.model.User;
import com.quicksketch.backend.repository.UserRepository;
import com.quicksketch.backend.service.StatsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {

    private static final double BAYESIAN_PRIOR_GAMES = 5.0;
    private static final double BAYESIAN_PRIOR_RATE = 0.5;

    private final UserRepository userRepository;

    public StatsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public GlobalLeaderboardsResponse getGlobalLeaderboards() {
        List<User> allUsers = userRepository.findAll();


        List<User> sketchersSorted = new ArrayList<>(allUsers);
        sketchersSorted.sort(
                Comparator.comparingInt(this::calculateSketcherElo).reversed()
                        .thenComparing(Comparator.comparingInt(User::getSketchesGuessedByOthers).reversed())
                        .thenComparing(Comparator.comparingInt(User::getSketchesCreated).reversed())
                        .thenComparing(User::getUsername)
        );

        List<SketcherLeaderboardEntry> topSketchers = new ArrayList<>();
        for (int i = 0; i < sketchersSorted.size(); i++) {
            User u = sketchersSorted.get(i);
            topSketchers.add(new SketcherLeaderboardEntry(
                    i + 1,
                    u.getUsername(),
                    calculateSketcherElo(u),
                    u.getSketchesCreated(),
                    u.getSketchesPlayedByOthers(),
                    u.getSketchesGuessedByOthers(),
                    calculateSketchGuessedPercentage(u)
            ));
        }

        List<User> guessersSorted = new ArrayList<>(allUsers);
        guessersSorted.sort(
                Comparator.comparingInt(this::calculateGuesserElo).reversed()
                        .thenComparing(Comparator.comparingInt(User::getWordsGuessed).reversed())
                        .thenComparingInt(User::getTotalGuessAttemptsUsed)
                        .thenComparing(User::getUsername)
        );

        List<GuesserLeaderboardEntry> topGuessers = new ArrayList<>();
        for (int i = 0; i < guessersSorted.size(); i++) {
            User u = guessersSorted.get(i);
            topGuessers.add(new GuesserLeaderboardEntry(
                    i + 1,
                    u.getUsername(),
                    calculateGuesserElo(u),
                    u.getWordsGuessed(),
                    u.getWordsFailed(),
                    u.getTotalGuessAttemptsUsed(),
                    calculateGuesserWinRate(u)
            ));
        }

        return new GlobalLeaderboardsResponse(topSketchers, topGuessers);
    }

    @Override
    public UserProfileStatsResponse getPersonalStats(String username) {
        GlobalLeaderboardsResponse boards = getGlobalLeaderboards();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        int sketcherRank = boards.getTopSketchers().stream()
                .filter(e -> e.getUsername().equals(username))
                .map(SketcherLeaderboardEntry::getRank)
                .findFirst()
                .orElse(0);

        int guesserRank = boards.getTopGuessers().stream()
                .filter(e -> e.getUsername().equals(username))
                .map(GuesserLeaderboardEntry::getRank)
                .findFirst()
                .orElse(0);

        int totalGames = currentUser.getWordsGuessed() + currentUser.getWordsFailed();
        double avgAttempts = totalGames > 0
                ? Math.round(((double) currentUser.getTotalGuessAttemptsUsed() / totalGames) * 10.0) / 10.0
                : 0.0;

        return new UserProfileStatsResponse(
                currentUser.getUsername(),
                currentUser.getEmail(),
                currentUser.getCreatedAt(),
                calculateSketcherElo(currentUser),
                sketcherRank,
                calculateGuesserElo(currentUser),
                guesserRank,
                currentUser.getSketchesCreated(),
                currentUser.getSketchesPlayedByOthers(),
                currentUser.getSketchesGuessedByOthers(),
                calculateSketchGuessedPercentage(currentUser),
                currentUser.getWordsGuessed(),
                currentUser.getWordsFailed(),
                currentUser.getTotalGuessAttemptsUsed(),
                calculateGuesserWinRate(currentUser),
                avgAttempts
        );
    }

    private int calculateSketcherElo(User user) {
        int played = user.getSketchesPlayedByOthers();
        int guessed = user.getSketchesGuessedByOthers();
        int created = user.getSketchesCreated();

        double bayesianRate = (guessed + (BAYESIAN_PRIOR_GAMES * BAYESIAN_PRIOR_RATE))
                / (played + BAYESIAN_PRIOR_GAMES);

        double activityBonus = Math.min(created * 5.0, 150.0);
        double elo = 1000.0 + ((bayesianRate - 0.5) * 1000.0) + activityBonus;
        return Math.max(100, (int) Math.round(elo));
    }

    private int calculateGuesserElo(User user) {
        int won = user.getWordsGuessed();
        int lost = user.getWordsFailed();
        int totalGames = won + lost;

        if (totalGames == 0) {
            return 1000;
        }

        int attemptsUsed = user.getTotalGuessAttemptsUsed();

        double bayesianWinRate = (won + (BAYESIAN_PRIOR_GAMES * BAYESIAN_PRIOR_RATE))
                / (totalGames + BAYESIAN_PRIOR_GAMES);

        // Efficienza tentativi (max 10 tentativi per partita)
        double maxPossibleAttempts = (totalGames + 1) * 10.0;
        double efficiency = Math.max(0.0, 1.0 - (attemptsUsed / maxPossibleAttempts));

        double elo = 1000.0
                + ((bayesianWinRate - 0.5) * 800.0)
                + (efficiency * 200.0)
                + (won * 10.0);

        return Math.max(100, (int) Math.round(elo));
    }

    private double calculateSketchGuessedPercentage(User user) {
        if (user.getSketchesPlayedByOthers() == 0) {
            return 0.0;
        }
        double raw = ((double) user.getSketchesGuessedByOthers() / user.getSketchesPlayedByOthers()) * 100.0;
        return Math.round(raw * 10.0) / 10.0;
    }

    private double calculateGuesserWinRate(User user) {
        int totalGames = user.getWordsGuessed() + user.getWordsFailed();
        if (totalGames == 0) {
            return 0.0;
        }
        double raw = ((double) user.getWordsGuessed() / totalGames) * 100.0;
        return Math.round(raw * 10.0) / 10.0;
    }
}