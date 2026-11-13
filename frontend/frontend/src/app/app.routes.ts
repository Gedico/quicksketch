import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { CreateSketchComponent } from './features/create-sketch/create-sketch';
import { GuessSketchComponent } from './features/guess-sketch/guess-sketch';
import { LeaderboardComponent } from './features/leaderboard/leaderboard';
import { ProfileComponent } from './features/profile/profile';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  // Rotte pubbliche (accessibili a tutti)
  { path: '', component: HomeComponent },
  { path: 'leaderboard', component: LeaderboardComponent },

  // Rotte riservate ai non autenticati
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },

  // Rotte protette (solo utenti autenticati)
  { path: 'create', component: CreateSketchComponent, canActivate: [authGuard] },
  { path: 'guess/:id', component: GuessSketchComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },

  // Fallback per rotte inesistenti
  { path: '**', redirectTo: '' }
];