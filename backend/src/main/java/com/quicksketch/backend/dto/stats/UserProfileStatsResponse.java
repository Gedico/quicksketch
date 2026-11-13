package com.quicksketch.backend.dto.stats;

import java.time.Instant;

public class UserProfileStatsResponse {

    private String username;
    private String email;
    private Instant createdAt;

    // Rating e posizioni nelle due classifiche
    private int sketcherElo;
    private int sketcherRank;
    private int guesserElo;
    private int guesserRank;

    // Statistiche da Disegnatore (Sketcher)
    private int sketchesCreated;
    private int sketchesPlayedByOthers;
    private int sketchesGuessedByOthers;
    private double sketchGuessedPercentage;

    // Statistiche da Indovinatore (Guesser)
    private int wordsGuessed;
    private int wordsFailed;
    private int totalGuessAttemptsUsed;
    private double winRatePercentage;
    private double averageAttemptsPerGame;

    public UserProfileStatsResponse(String username, String email, Instant createdAt,
                                    int sketcherElo, int sketcherRank,
                                    int guesserElo, int guesserRank,
                                    int sketchesCreated, int sketchesPlayedByOthers,
                                    int sketchesGuessedByOthers, double sketchGuessedPercentage,
                                    int wordsGuessed, int wordsFailed,
                                    int totalGuessAttemptsUsed, double winRatePercentage,
                                    double averageAttemptsPerGame) {
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.sketcherElo = sketcherElo;
        this.sketcherRank = sketcherRank;
        this.guesserElo = guesserElo;
        this.guesserRank = guesserRank;
        this.sketchesCreated = sketchesCreated;
        this.sketchesPlayedByOthers = sketchesPlayedByOthers;
        this.sketchesGuessedByOthers = sketchesGuessedByOthers;
        this.sketchGuessedPercentage = sketchGuessedPercentage;
        this.wordsGuessed = wordsGuessed;
        this.wordsFailed = wordsFailed;
        this.totalGuessAttemptsUsed = totalGuessAttemptsUsed;
        this.winRatePercentage = winRatePercentage;
        this.averageAttemptsPerGame = averageAttemptsPerGame;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Instant getCreatedAt() { return createdAt; }
    public int getSketcherElo() { return sketcherElo; }
    public int getSketcherRank() { return sketcherRank; }
    public int getGuesserElo() { return guesserElo; }
    public int getGuesserRank() { return guesserRank; }
    public int getSketchesCreated() { return sketchesCreated; }
    public int getSketchesPlayedByOthers() { return sketchesPlayedByOthers; }
    public int getSketchesGuessedByOthers() { return sketchesGuessedByOthers; }
    public double getSketchGuessedPercentage() { return sketchGuessedPercentage; }
    public int getWordsGuessed() { return wordsGuessed; }
    public int getWordsFailed() { return wordsFailed; }
    public int getTotalGuessAttemptsUsed() { return totalGuessAttemptsUsed; }
    public double getWinRatePercentage() { return winRatePercentage; }
    public double getAverageAttemptsPerGame() { return averageAttemptsPerGame; }
}