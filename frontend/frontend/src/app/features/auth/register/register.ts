import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { RegisterRequestDto } from '../../../core/dto/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent {
  credentials: RegisterRequestDto = {
    username: '',
    email: '',
    password: ''
  };

  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  // Controllo formato email: deve contenere '@' e almeno un '.' dopo la chiocciola
  isEmailValid(): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(this.credentials.email.trim());
  }

  // Controllo password: min 8 caratteri, almeno 1 maiuscola, almeno 1 numero
  isPasswordStrong(): boolean {
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d).{8,}$/;
    return passwordRegex.test(this.credentials.password);
  }

  // Controllo coincidenza password
  doPasswordsMatch(): boolean {
    return (
      this.credentials.password.length > 0 &&
      this.credentials.password === this.confirmPassword
    );
  }

  onRegister(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const username = this.credentials.username.trim();
    const email = this.credentials.email.trim();

    if (!username || !email || !this.credentials.password || !this.confirmPassword) {
      this.errorMessage = 'Compila tutti i campi obbligatori.';
      return;
    }

    if (!this.isEmailValid()) {
      this.errorMessage = 'Inserisci un indirizzo email valido (deve contenere "@" e ".").';
      return;
    }

    if (!this.isPasswordStrong()) {
      this.errorMessage = 'La password deve avere almeno 8 caratteri, una lettera maiuscola e un numero.';
      return;
    }

    if (!this.doPasswordsMatch()) {
      this.errorMessage = 'Le due password non coincidono.';
      return;
    }

    this.isLoading = true;

    this.authService.register({ username, email, password: this.credentials.password }).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Registrazione completata! Reindirizzamento al login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: (err) => {
        this.isLoading = false;
        // Se il backend restituisce un messaggio specifico (es. "Username già in uso"), lo mostriamo direttamente
        if (err.error?.message) {
          this.errorMessage = err.error.message;
        } else if (err.status === 409) {
          this.errorMessage = 'Questo username o questa email sono già registrati.';
        } else {
          this.errorMessage = 'Errore durante la registrazione. Riprova.';
        }
      }
    });
  }
}
