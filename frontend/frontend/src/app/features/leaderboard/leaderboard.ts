import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StatsService } from '../../core/services/stats.service';
import {
  GuesserLeaderboardEntry,
  SketcherLeaderboardEntry
} from '../../core/dto/stats/global-leaderboards-response';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './leaderboard.html',
  styleUrl: './leaderboard.scss'
})
export class LeaderboardComponent implements OnInit {
  activeTab: 'sketchers' | 'guessers' = 'guessers';
  topSketchers: SketcherLeaderboardEntry[] = [];
  topGuessers: GuesserLeaderboardEntry[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(private statsService: StatsService) {}

  ngOnInit(): void {
    this.loadLeaderboards();
  }

  loadLeaderboards(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.statsService.getGlobalLeaderboards().subscribe({
      next: (data) => {
        this.topSketchers = data.topSketchers;
        this.topGuessers = data.topGuessers;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Errore nel caricamento delle classifiche:', err);
        this.errorMessage = 'Impossibile caricare la classifica al momento.';
        this.isLoading = false;
      }
    });
  }

  selectTab(tab: 'sketchers' | 'guessers'): void {
    this.activeTab = tab;
  }
}
