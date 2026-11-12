package com.quicksketch.backend.controller;

import com.quicksketch.backend.dto.sketch.*;
import com.quicksketch.backend.service.SketchService;
import com.quicksketch.backend.service.WordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

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


//--------------------  CREATE SKETCH  ---------------------------------------------------

    // FASE 1: Restituisce 3 parole casuali tra cui scegliere
    @GetMapping("/word-options")
    public ResponseEntity<List<String>> getWordOptions() {
        return ResponseEntity.ok(wordService.getRandomWords(3));
    }

    // FASE 2: Crea lo sketch con la parola scelta e fa partire la sessione (restituisce l'ID)
    @PostMapping("/start")
    public ResponseEntity<SketchResponse> startSketch(@Valid @RequestBody StartSketchRequest request,
                                                      Authentication authentication) {
        String authorUsername = authentication.getName();
        return ResponseEntity.ok(sketchService.startSketch(authorUsername, request));
    }

    // FASE 3: Salva il disegno al termine dei 2 minuti usando l'ID ricevuto in Fase 2
    @PutMapping("/{id}/complete")
    public ResponseEntity<SketchResponse> completeSketch(@PathVariable String id,
                                                         @Valid @RequestBody CompleteSketchRequest request,
                                                         Authentication authentication) {
        String authorUsername = authentication.getName();
        return ResponseEntity.ok(sketchService.completeSketch(id, authorUsername, request));
    }


//---------------------  VISUALIZZAZIONE CONDIZIONATA  ---------------------------------------------

    @GetMapping("/playable")
    public ResponseEntity<List<SketchResponse>> getPlayableSketches(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = authentication.getName();
        return ResponseEntity.ok(sketchService.getPlayableSketches(username));
    }
}