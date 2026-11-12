export interface GuessSessionResponse {
  sketchId: string;
  authorUsername: string;
  imageData: string;
  sketchCreatedAt: string;
  status: 'IN_PROGRESS' | 'WON' | 'LOST';
  attemptsUsed: number;
  attemptsRemaining: number;
  maxAttempts: number;
  attemptedWords: string[];
  secretWord: string | null;
}