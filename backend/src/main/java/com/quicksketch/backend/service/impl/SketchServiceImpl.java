package com.quicksketch.backend.service.impl;

import com.quicksketch.backend.dto.sketch.CompleteSketchRequest;
import com.quicksketch.backend.dto.sketch.SketchResponse;
import com.quicksketch.backend.dto.sketch.StartSketchRequest;
import com.quicksketch.backend.exception.common.ResourceNotFoundException;
import com.quicksketch.backend.exception.common.UnauthorizedActionException;
import com.quicksketch.backend.exception.game.GameRuleViolationException;
import com.quicksketch.backend.exception.game.InvalidSketchDataException;
import com.quicksketch.backend.mapper.SketchMapper;
import com.quicksketch.backend.model.Guess;
import com.quicksketch.backend.model.Sketch;
import com.quicksketch.backend.repository.GuessRepository;
import com.quicksketch.backend.repository.SketchRepository;
import com.quicksketch.backend.repository.UserRepository;
import com.quicksketch.backend.service.SketchService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SketchServiceImpl implements SketchService {

    private final SketchRepository sketchRepository;
    private final GuessRepository guessRepository;
    private final UserRepository userRepository;
    private final SketchMapper sketchMapper;

    public SketchServiceImpl(SketchRepository sketchRepository,
                             GuessRepository guessRepository,
                             UserRepository userRepository,
                             SketchMapper sketchMapper) {
        this.sketchRepository = sketchRepository;
        this.guessRepository = guessRepository;
        this.userRepository = userRepository;
        this.sketchMapper = sketchMapper;
    }

    @Override
    public SketchResponse startSketch(String authorUsername, StartSketchRequest request) {
        if (request == null || request.getWord() == null || request.getWord().trim().isEmpty()) {
            throw new InvalidSketchDataException("La parola selezionata per lo sketch non può essere vuota.");
        }

        Sketch sketch = new Sketch(
                authorUsername,
                request.getWord().trim().toLowerCase()
        );

        Sketch saved = sketchRepository.save(sketch);
        return sketchMapper.toResponse(saved);
    }

    @Override
    public SketchResponse completeSketch(String sketchId, String authorUsername, CompleteSketchRequest request) {
        Sketch sketch = sketchRepository.findById(sketchId)
                .orElseThrow(() -> new ResourceNotFoundException("Sketch non trovato con ID: " + sketchId));

        if (!sketch.getAuthorUsername().equals(authorUsername)) {
            throw new UnauthorizedActionException("Non sei autorizzato a modificare questo sketch.");
        }

        if (sketch.isCompleted()) {
            throw new GameRuleViolationException("Questo sketch è già stato completato.");
        }

        if (request == null || request.getImageData() == null || request.getImageData().trim().isEmpty()) {
            throw new InvalidSketchDataException("I dati dell'immagine dello sketch sono mancanti.");
        }

        sketch.setImageData(request.getImageData());
        sketch.setCompleted(true);

        Sketch updated = sketchRepository.save(sketch);
        userRepository.findByUsername(authorUsername).ifPresent(author -> {
            author.setSketchesCreated(author.getSketchesCreated() + 1);
            userRepository.save(author);
        });
        return sketchMapper.toResponse(updated);
    }

    @Override
    public List<SketchResponse> getAllSketches() {
        return sketchRepository.findAllByCompletedTrueOrderByCreatedAtDesc()
                .stream()
                .map(sketchMapper::toResponse)
                .toList();
    }

    @Override
    public List<SketchResponse> getPlayableSketches(String username) {
        List<String> attemptedSketchIds = guessRepository.findByUsername(username)
                .stream()
                .map(Guess::getSketchId)
                .toList();

        if (attemptedSketchIds.isEmpty()) {
            attemptedSketchIds = List.of("NONE");
        }

        return sketchRepository
                .findByCompletedTrueAndAuthorUsernameNotAndIdNotInOrderByCreatedAtDesc(username, attemptedSketchIds)
                .stream()
                .map(sketchMapper::toResponse)
                .toList();
    }
}