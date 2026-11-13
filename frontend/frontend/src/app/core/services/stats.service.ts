import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GlobalLeaderboardsResponse } from '../dto/stats/global-leaderboards-response';
import { UserProfileStatsResponse } from '../dto/stats/user-profile-stats-response';

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Pubblico: accessibile anche agli utenti non autenticati
  getGlobalLeaderboards(): Observable<GlobalLeaderboardsResponse> {
    return this.http.get<GlobalLeaderboardsResponse>(`${this.apiUrl}/leaderboard/global`);
  }

  // Protetto: statistiche personali dell'utente loggato
  getMyStats(): Observable<UserProfileStatsResponse> {
    return this.http.get<UserProfileStatsResponse>(`${this.apiUrl}/users/me/stats`, {
      withCredentials: true
    });
  }
}