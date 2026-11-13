import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequestDto, RegisterRequestDto } from '../dto/auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/api/auth';

  // State in memoria senza toccare localStorage (Anti-XSS)
  isAuthenticatedSignal = signal<boolean>(false);

  constructor(private http: HttpClient) {
    this.checkSession();
  }

  checkSession(): void {
    this.http.get(`${this.apiUrl}/me`, { withCredentials: true }).subscribe({
      next: () => this.isAuthenticatedSignal.set(true),
      error: () => this.isAuthenticatedSignal.set(false)
    });
  }

  register(request: RegisterRequestDto): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, request);
  }

  login(request: LoginRequestDto): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, request, {
      responseType: 'text',
      withCredentials: true
    }).pipe(
      tap(() => {
        this.isAuthenticatedSignal.set(true);
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}, {
      responseType: 'text',
      withCredentials: true
    }).pipe(
      tap(() => {
        this.isAuthenticatedSignal.set(false);
      })
    );
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSignal();
  }
}