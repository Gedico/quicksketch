import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { StatsService } from '../../core/services/stats.service';
import { UserProfileStatsResponse } from '../../core/dto/stats/user-profile-stats-response';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class ProfileComponent implements OnInit {
  stats: UserProfileStatsResponse | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(private statsService: StatsService) {}

  ngOnInit(): void {
    this.statsService.getMyStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Errore nel caricamento delle statistiche personali:', err);
        this.errorMessage = 'Impossibile caricare le tue statistiche. Assicurati di aver effettuato l’accesso.';
        this.isLoading = false;
      }
    });
  }
}