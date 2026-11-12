import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GuessSessionResponse } from '../dto/guess/guess-session-response';
import { InProgressGuessResponse } from '../dto/guess/in-progress-guess-response';

@Injectable({
  providedIn: 'root'
})
export class GuessService {
  private readonly apiUrl = 'http://localhost:8080/api/guesses';

  constructor(private http: HttpClient) {}

  startOrResumeGuess(sketchId: string): Observable<GuessSessionResponse> {
    return this.http.post<GuessSessionResponse>(
      `${this.apiUrl}/start/${sketchId}`,
      {},
      { withCredentials: true }
    );
  }

  submitAttempt(sketchId: string, word: string): Observable<GuessSessionResponse> {
    return this.http.post<GuessSessionResponse>(
      `${this.apiUrl}/${sketchId}/attempt`,
      { word },
      { withCredentials: true }
    );
  }

  surrenderGuess(sketchId: string): Observable<GuessSessionResponse> {
    return this.http.post<GuessSessionResponse>(
      `${this.apiUrl}/${sketchId}/surrender`,
      {},
      { withCredentials: true }
    );
  }

  getInProgressGuesses(): Observable<InProgressGuessResponse[]> {
    return this.http.get<InProgressGuessResponse[]>(`${this.apiUrl}/in-progress`, {
      withCredentials: true
    });
  }
}