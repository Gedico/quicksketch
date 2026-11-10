package com.quicksketch.backend.controller;

import com.quicksketch.backend.dto.CreateSketchRequest;
import com.quicksketch.backend.dto.SketchResponse;
import com.quicksketch.backend.service.SketchService;
import com.quicksketch.backend.service.WordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sketches")
@CrossOrigin(origins = "*")
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

    @PostMapping
    public ResponseEntity<SketchResponse> createSketch(@Valid @RequestBody CreateSketchRequest request,
                                                       Authentication authentication) {
        String authorUsername = authentication.getName();
        SketchResponse response = sketchService.createSketch(authorUsername, request);
        return ResponseEntity.ok(response);
    }
}