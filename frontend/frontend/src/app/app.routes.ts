import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { CreateSketchComponent } from './features/create-sketch/create-sketch';
import { GuessSketchComponent } from './features/guess-sketch/guess-sketch';
import { LeaderboardComponent } from './features/leaderboard/leaderboard';
import { ProfileComponent } from './features/profile/profile';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'create', component: CreateSketchComponent },
  { path: 'guess/:id',component: GuessSketchComponent},
  { path: 'leaderboard', component: LeaderboardComponent },
  { path: 'profile', component: ProfileComponent },
  { path: '**', redirectTo: '' }
];
