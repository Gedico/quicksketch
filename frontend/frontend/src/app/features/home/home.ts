import { Component, OnInit } from '@angular/core';
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
export class HomeComponent implements OnInit {
  sketches: SketchResponse[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private sketchService: SketchService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAllSketches();
  }

  loadAllSketches(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.sketchService.getAllSketches().subscribe({
      next: (data) => {
        this.sketches = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Errore nel caricamento degli sketch:', err);
        this.errorMessage = 'Impossibile caricare la galleria degli sketch.';
        this.isLoading = false;
      }
    });
  }
}
