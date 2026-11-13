package com.quicksketch.backend.dto.stats;

public class GuesserLeaderboardEntry {

    private int rank;
    private String username;
    private int guesserElo;
    private int wordsGuessed;
    private int wordsFailed;
    private int totalGuessAttemptsUsed;
    private double winRatePercentage;

    public GuesserLeaderboardEntry(int rank, String username, int guesserElo,
                                   int wordsGuessed, int wordsFailed,
                                   int totalGuessAttemptsUsed, double winRatePercentage) {
        this.rank = rank;
        this.username = username;
        this.guesserElo = guesserElo;
        this.wordsGuessed = wordsGuessed;
        this.wordsFailed = wordsFailed;
        this.totalGuessAttemptsUsed = totalGuessAttemptsUsed;
        this.winRatePercentage = winRatePercentage;
    }

    public int getRank() { return rank; }
    public String getUsername() { return username; }
    public int getGuesserElo() { return guesserElo; }
    public int getWordsGuessed() { return wordsGuessed; }
    public int getWordsFailed() { return wordsFailed; }
    public int getTotalGuessAttemptsUsed() { return totalGuessAttemptsUsed; }
    public double getWinRatePercentage() { return winRatePercentage; }
}