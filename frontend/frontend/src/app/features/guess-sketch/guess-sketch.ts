import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { GuessService } from '../../core/services/guess';
import { GuessSessionResponse } from '../../core/dto/guess/guess-session-response';

@Component({
  selector: 'app-guess-sketch',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './guess-sketch.html',
  styleUrl: './guess-sketch.scss'
})
export class GuessSketchComponent implements OnInit {
  sketchId: string = '';
  session: GuessSessionResponse | null = null;
  guessInput: string = '';
  feedbackMessage: string = '';
  errorMessage: string = '';
  isLoading: boolean = true;
  isSubmitting: boolean = false;
  confirmSurrender: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private guessService: GuessService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.router.navigate(['/']);
      return;
    }
    this.sketchId = id;
    this.initGameSession();
  }

  initGameSession(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.guessService.startOrResumeGuess(this.sketchId).subscribe({
      next: (res) => {
        this.session = res;
        this.isLoading = false;
        if (res.attemptsUsed > 0 && res.status === 'IN_PROGRESS') {
          this.feedbackMessage = `Partita ripresa: hai ancora ${res.attemptsRemaining} tentativi disponibili.`;
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Impossibile avviare la partita per questo sketch.';
      }
    });
  }

  onSubmitGuess(): void {
    if (!this.session || this.session.status !== 'IN_PROGRESS' || this.isSubmitting) {
      return;
    }

    const cleaned = this.guessInput.trim();
    if (!cleaned) {
      return;
    }

    // Evita di sprecare un tentativo ripetendo una parola già provata in questa sessione
    if (this.session.attemptedWords.includes(cleaned.toLowerCase())) {
      this.feedbackMessage = `Hai già provato "${cleaned.toLowerCase()}". Inserisci un'altra parola!`;
      this.guessInput = '';
      return;
    }

    this.isSubmitting = true;
    this.feedbackMessage = '';
    this.errorMessage = '';

    this.guessService.submitAttempt(this.sketchId, cleaned).subscribe({
      next: (res) => {
        this.session = res;
        this.guessInput = '';
        this.isSubmitting = false;

        if (res.status === 'IN_PROGRESS') {
          this.feedbackMessage = `Non è "${cleaned.toLowerCase()}". Riprova! (${res.attemptsRemaining} tentativi rimasti)`;
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || 'Errore durante l’invio del tentativo.';
      }
    });
  }

  onSurrender(): void {
    if (!this.session || this.session.status !== 'IN_PROGRESS' || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    this.guessService.surrenderGuess(this.sketchId).subscribe({
      next: (res) => {
        this.session = res;
        this.confirmSurrender = false;
        this.isSubmitting = false;
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || 'Errore durante la resa.';
      }
    });
  }
}