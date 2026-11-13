package com.quicksketch.backend.dto.stats;

public class SketcherLeaderboardEntry {

    private int rank;
    private String username;
    private int sketcherElo;
    private int sketchesCreated;
    private int sketchesPlayedByOthers;
    private int sketchesGuessedByOthers;
    private double guessedPercentage; // Percentuale grezza (es. 90.0%) richiesta dalla traccia

    public SketcherLeaderboardEntry(int rank, String username, int sketcherElo,
                                    int sketchesCreated, int sketchesPlayedByOthers,
                                    int sketchesGuessedByOthers, double guessedPercentage) {
        this.rank = rank;
        this.username = username;
        this.sketcherElo = sketcherElo;
        this.sketchesCreated = sketchesCreated;
        this.sketchesPlayedByOthers = sketchesPlayedByOthers;
        this.sketchesGuessedByOthers = sketchesGuessedByOthers;
        this.guessedPercentage = guessedPercentage;
    }

    public int getRank() { return rank; }
    public String getUsername() { return username; }
    public int getSketcherElo() { return sketcherElo; }
    public int getSketchesCreated() { return sketchesCreated; }
    public int getSketchesPlayedByOthers() { return sketchesPlayedByOthers; }
    public int getSketchesGuessedByOthers() { return sketchesGuessedByOthers; }
    public double getGuessedPercentage() { return guessedPercentage; }
}