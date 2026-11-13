import { Component, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { SketchService } from '../../core/services/sketch';
import { GuessService } from '../../core/services/guess';
import { AuthService } from '../../core/services/auth';
import { SketchResponse } from '../../core/dto/sketch/sketch-response';
import { InProgressGuessResponse } from '../../core/dto/guess/in-progress-guess-response';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class HomeComponent {
  sketches: SketchResponse[] = [];
  inProgressGuesses: InProgressGuessResponse[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private sketchService: SketchService,
    private guessService: GuessService,
    public authService: AuthService
  ) {
    effect(() => {
      const isLoggedIn = this.authService.isAuthenticatedSignal();
      this.loadHomeData(isLoggedIn);
    });
  }

  loadHomeData(isLoggedIn: boolean): void {
    this.isLoading = true;
    this.errorMessage = '';

    if (!isLoggedIn) {
      this.inProgressGuesses = [];
      this.sketchService.getAllSketches().subscribe({
        next: (data) => {
          this.sketches = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Errore nel caricamento degli sketch:', err);
          this.errorMessage = 'Impossibile caricare gli sketch al momento.';
          this.isLoading = false;
        }
      });
      return;
    }

    forkJoin({
      playable: this.sketchService.getPlayableSketches(),
      inProgress: this.guessService.getInProgressGuesses()
    }).subscribe({
      next: ({ playable, inProgress }) => {
        this.sketches = playable;
        this.inProgressGuesses = inProgress;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Errore nel caricamento dei dati di gioco:', err);
        this.errorMessage = 'Impossibile caricare gli sketch al momento.';
        this.isLoading = false;
      }
    });
  }

  scrollToGallery(): void {
    const galleryEl = document.getElementById('gallery-section');
    if (galleryEl) {
      galleryEl.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
}