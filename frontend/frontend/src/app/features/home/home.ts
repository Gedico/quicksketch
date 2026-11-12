import { Component, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SketchService } from '../../core/services/sketch';
import { AuthService } from '../../core/services/auth';
import { SketchResponse } from '../../core/dto/sketch/sketch-response';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class HomeComponent {
  sketches: SketchResponse[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private sketchService: SketchService,
    public authService: AuthService
  ) {
    // Reagisce automaticamente allo stato di autenticazione (login / logout / caricamento iniziale)
    effect(() => {
      const isLoggedIn = this.authService.isAuthenticatedSignal();
      this.loadSketches(isLoggedIn);
    });
  }

  loadSketches(isLoggedIn: boolean): void {
    this.isLoading = true;
    this.errorMessage = '';

    const request$ = isLoggedIn
      ? this.sketchService.getPlayableSketches()
      : this.sketchService.getAllSketches();

    request$.subscribe({
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
  }
}
