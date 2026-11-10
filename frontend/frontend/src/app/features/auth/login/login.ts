import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  credentials = { username: '', password: '' };
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService) {}

  onLogin(): void {
    this.authService.login(this.credentials).subscribe({
      next: (res) => {
        this.errorMessage = '';
        this.successMessage = 'Login effettuato con successo! Token salvato.';
        console.log('JWT Token:', res.token);
      },
      error: (err) => {
        this.successMessage = '';
        this.errorMessage = err.error?.message || 'Credenziali non valide o server non raggiungibile';
      }
    });
  }
}
