export interface InProgressGuessResponse {
  sketchId: string;
  authorUsername: string;
  imageData: string;
  attemptsUsed: number;
  maxAttempts: number;
  startedAt: string;
}