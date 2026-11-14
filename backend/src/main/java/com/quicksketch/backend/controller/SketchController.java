package com.quicksketch.backend.controller;

import com.quicksketch.backend.dto.sketch.CompleteSketchRequest;
import com.quicksketch.backend.dto.sketch.SketchResponse;
import com.quicksketch.backend.dto.sketch.StartSketchRequest;
import com.quicksketch.backend.exception.common.UnauthorizedActionException;
import com.quicksketch.backend.service.SketchService;
import com.quicksketch.backend.service.WordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sketches")
public class SketchController {

    private final SketchService sketchService;
    private final WordService wordService;

    public SketchController(SketchService sketchService, WordService wordService) {
        this.sketchService = sketchService;
        this.wordService = wordService;
    }

    @GetMapping
    public ResponseEntity<List<SketchResponse>> getAllSketches() {
        return ResponseEntity.ok(sketchService.getAllSketches());
    }

    // FASE 1: Restituisce 3 parole casuali tra cui scegliere
    @GetMapping("/word-options")
    public ResponseEntity<List<String>> getWordOptions() {
        return ResponseEntity.ok(wordService.getRandomWords(3));
    }

    // FASE 2: Crea lo sketch con la parola scelta e fa partire la sessione
    @PostMapping("/start")
    public ResponseEntity<SketchResponse> startSketch(@Valid @RequestBody StartSketchRequest request,
                                                      Authentication authentication) {
        String authorUsername = extractAuthenticatedUsername(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sketchService.startSketch(authorUsername, request));
    }

    // FASE 3: Salva il disegno al termine dei 2 minuti usando l'ID ricevuto in Fase 2
    @PutMapping("/{id}/complete")
    public ResponseEntity<SketchResponse> completeSketch(@PathVariable String id,
                                                         @Valid @RequestBody CompleteSketchRequest request,
                                                         Authentication authentication) {
        String authorUsername = extractAuthenticatedUsername(authentication);
        return ResponseEntity.ok(sketchService.completeSketch(id, authorUsername, request));
    }

    // VISUALIZZAZIONE CONDIZIONATA
    @GetMapping("/playable")
    public ResponseEntity<List<SketchResponse>> getPlayableSketches(Authentication authentication) {
        String username = extractAuthenticatedUsername(authentication);
        return ResponseEntity.ok(sketchService.getPlayableSketches(username));
    }

    private String extractAuthenticatedUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedActionException("Devi effettuare l'accesso per eseguire questa operazione.");
        }
        return authentication.getName();
    }
}