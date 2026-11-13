export interface SketcherLeaderboardEntry {
  rank: number;
  username: string;
  sketcherElo: number;
  sketchesCreated: number;
  sketchesPlayedByOthers: number;
  sketchesGuessedByOthers: number;
  guessedPercentage: number;
}

export interface GuesserLeaderboardEntry {
  rank: number;
  username: string;
  guesserElo: number;
  wordsGuessed: number;
  wordsFailed: number;
  totalGuessAttemptsUsed: number;
  winRatePercentage: number;
}

export interface GlobalLeaderboardsResponse {
  topSketchers: SketcherLeaderboardEntry[];
  topGuessers: GuesserLeaderboardEntry[];
}