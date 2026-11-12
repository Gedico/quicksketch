import { AfterViewInit, Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SketchService } from '../../core/services/sketch';

type SessionState = 'SELECTING_WORD' | 'DRAWING' | 'COMPLETED';

@Component({
  selector: 'app-create-sketch',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './create-sketch.html',
  styleUrl: './create-sketch.scss'
})
export class CreateSketchComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('sketchCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  sessionState: SessionState = 'SELECTING_WORD';
  wordOptions: string[] = [];
  selectedWord: string = '';
  sketchId: string = '';
  savedImagePreview: string = '';

  // Timer 2 minuti (120 secondi)
  timeLeft: number = 120;
  private timerInterval: any = null;

  // Stato Canvas e Strumenti
  private ctx!: CanvasRenderingContext2D;
  private isDrawing: boolean = false;
  isCanvasLocked: boolean = true; // Bloccato finché non si sceglie la parola
  isSaving: boolean = false;
  isLoadingWords: boolean = false;
  errorMessage: string = '';

  currentColor: string = '#000000';
  brushSize: number = 4;
  isEraser: boolean = false;

  readonly availableColors: string[] = [
    '#000000', '#e63946', '#2a9d8f', '#264653',
    '#f4a261', '#e9c46a', '#4361ee', '#7209b7'
  ];

  constructor(
    private sketchService: SketchService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadWordOptions();
  }

  ngAfterViewInit(): void {
    this.initCanvas();
  }

  private initCanvas(): void {
    if (!this.canvasRef) return;
    const canvas = this.canvasRef.nativeElement;
    const context = canvas.getContext('2d');
    if (!context) return;

    this.ctx = context;
    this.ctx.fillStyle = '#ffffff';
    this.ctx.fillRect(0, 0, canvas.width, canvas.height);
    this.ctx.lineCap = 'round';
    this.ctx.lineJoin = 'round';
  }

  // FASE 1: Richiesta delle 3 parole al backend
  loadWordOptions(): void {
    this.isLoadingWords = true;
    this.errorMessage = '';
    this.sketchService.getWordOptions().subscribe({
      next: (words) => {
        this.wordOptions = words;
        this.isLoadingWords = false;
      },
      error: () => {
        this.isLoadingWords = false;
        this.errorMessage = 'Impossibile caricare le parole dal server. Riprova.';
      }
    });
  }

  // FASE 2: Scelta della parola -> Chiusura popup -> Avvio sessione sul backend e timer
  selectWord(word: string): void {
    this.errorMessage = '';
    this.selectedWord = word;

    this.sketchService.startSketch({ word }).subscribe({
      next: (response) => {
        this.sketchId = response.id;
        this.sessionState = 'DRAWING';
        this.isCanvasLocked = false;
        this.startTimer();
      },
      error: () => {
        this.errorMessage = 'Errore nell’avvio della sessione. Riprova.';
      }
    });
  }

  private startTimer(): void {
    this.clearTimer();
    this.timeLeft = 120;
    this.timerInterval = setInterval(() => {
      if (this.timeLeft > 0) {
        this.timeLeft--;
      } else {
        this.finishAndSaveSketch();
      }
    }, 1000);
  }

  get formattedTime(): string {
    const minutes = Math.floor(this.timeLeft / 60);
    const seconds = this.timeLeft % 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  }

  // --- Logica Disegno sul Canvas ---

  startDrawing(event: MouseEvent): void {
    if (this.isCanvasLocked || this.sessionState !== 'DRAWING' || !this.ctx) return;
    this.isDrawing = true;
    const { offsetX, offsetY } = event;
    this.ctx.beginPath();
    this.ctx.moveTo(offsetX, offsetY);
  }

  draw(event: MouseEvent): void {
    if (!this.isDrawing || this.isCanvasLocked || !this.ctx) return;
    const { offsetX, offsetY } = event;
    this.ctx.strokeStyle = this.isEraser ? '#ffffff' : this.currentColor;
    this.ctx.lineWidth = this.brushSize;
    this.ctx.lineTo(offsetX, offsetY);
    this.ctx.stroke();
  }

  stopDrawing(): void {
    if (!this.isDrawing) return;
    this.isDrawing = false;
    this.ctx.closePath();
  }

  setColor(color: string): void {
    this.isEraser = false;
    this.currentColor = color;
  }

  activateEraser(): void {
    this.isEraser = true;
  }

  clearCanvas(): void {
    if (this.isCanvasLocked || !this.ctx) return;
    const canvas = this.canvasRef.nativeElement;
    this.ctx.fillStyle = '#ffffff';
    this.ctx.fillRect(0, 0, canvas.width, canvas.height);
  }

  // FASE 3: Scadenza timer o click su "Finito" -> Salvataggio automatico e riepilogo
  finishAndSaveSketch(): void {
    if (this.isCanvasLocked || this.isSaving || this.sessionState !== 'DRAWING') return;

    this.isCanvasLocked = true;
    this.isDrawing = false;
    this.clearTimer();
    this.isSaving = true;

    const imageData = this.canvasRef.nativeElement.toDataURL('image/png');
    this.savedImagePreview = imageData;

    this.sketchService.completeSketch(this.sketchId, { imageData }).subscribe({
      next: () => {
        this.isSaving = false;
        this.sessionState = 'COMPLETED';
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = 'Errore durante il salvataggio dello sketch.';
      }
    });
  }

  // Azione dal riepilogo finale: "Crea un nuovo sketch"
  startNewSketch(): void {
    this.clearTimer();
    this.selectedWord = '';
    this.sketchId = '';
    this.savedImagePreview = '';
    this.isCanvasLocked = true;
    this.isSaving = false;
    this.isEraser = false;
    this.currentColor = '#000000';
    this.brushSize = 4;
    this.sessionState = 'SELECTING_WORD';

    // Pulisce la tela sullo sfondo e carica 3 nuove parole
    this.initCanvas();
    this.loadWordOptions();
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }

  // Salvataggio d'emergenza se l'utente chiude o aggiorna la pagina mentre disegna
  @HostListener('window:beforeunload')
  onBeforeUnload(): void {
    if (this.sessionState === 'DRAWING' && !this.isCanvasLocked && this.sketchId && this.canvasRef) {
      const imageData = this.canvasRef.nativeElement.toDataURL('image/png');
      fetch(`http://localhost:8080/api/sketches/${this.sketchId}/complete`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        keepalive: true,
        body: JSON.stringify({ imageData })
      });
    }
  }

  private clearTimer(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  ngOnDestroy(): void {
    if (this.sessionState === 'DRAWING' && !this.isCanvasLocked && this.sketchId && this.canvasRef) {
      this.finishAndSaveSketch();
    }
    this.clearTimer();
  }
}
