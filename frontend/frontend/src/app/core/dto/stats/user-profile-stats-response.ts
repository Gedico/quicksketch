export interface UserProfileStatsResponse {
  username: string;
  email: string;
  createdAt: string;
  sketcherElo: number;
  sketcherRank: number;
  guesserElo: number;
  guesserRank: number;
  sketchesCreated: number;
  sketchesPlayedByOthers: number;
  sketchesGuessedByOthers: number;
  sketchGuessedPercentage: number;
  wordsGuessed: number;
  wordsFailed: number;
  totalGuessAttemptsUsed: number;
  winRatePercentage: number;
  averageAttemptsPerGame: number;
}