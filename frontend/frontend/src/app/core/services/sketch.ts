import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StartSketchRequest } from '../dto/sketch/start-sketch-request';
import { CompleteSketchRequest } from '../dto/sketch/complete-sketch-request';
import { SketchResponse } from '../dto/sketch/sketch-response';

@Injectable({
  providedIn: 'root'
})
export class SketchService {
  private readonly apiUrl = 'http://localhost:8080/api/sketches';

  constructor(private http: HttpClient) {}

  getWordOptions(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/word-options`, { withCredentials: true });
  }

  startSketch(request: StartSketchRequest): Observable<SketchResponse> {
    return this.http.post<SketchResponse>(`${this.apiUrl}/start`, request, { withCredentials: true });
  }

  completeSketch(sketchId: string, request: CompleteSketchRequest): Observable<SketchResponse> {
    return this.http.put<SketchResponse>(`${this.apiUrl}/${sketchId}/complete`, request, { withCredentials: true });
  }

  getAllSketches(): Observable<SketchResponse[]> {
    return this.http.get<SketchResponse[]>(this.apiUrl);
  }

  getPlayableSketches(): Observable<SketchResponse[]> {
    return this.http.get<SketchResponse[]>(`${this.apiUrl}/playable`, {
      withCredentials: true
    });
  }
}