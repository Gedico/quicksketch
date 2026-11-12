package com.quicksketch.backend.service.impl;

import com.quicksketch.backend.dto.guess.GuessSessionResponse;
import com.quicksketch.backend.dto.guess.InProgressGuessResponse;
import com.quicksketch.backend.model.Guess;
import com.quicksketch.backend.model.Sketch;
import com.quicksketch.backend.model.User;
import com.quicksketch.backend.repository.GuessRepository;
import com.quicksketch.backend.repository.SketchRepository;
import com.quicksketch.backend.repository.UserRepository;
import com.quicksketch.backend.service.GuessService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GuessServiceImpl implements GuessService {

    private static final int MAX_ATTEMPTS = 10;

    private final GuessRepository guessRepository;
    private final SketchRepository sketchRepository;
    private final UserRepository userRepository;

    public GuessServiceImpl(GuessRepository guessRepository,
                            SketchRepository sketchRepository,
                            UserRepository userRepository) {
        this.guessRepository = guessRepository;
        this.sketchRepository = sketchRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GuessSessionResponse startOrResumeGuess(String username, String sketchId) {
        Sketch sketch = getValidPlayableSketch(username, sketchId);

        Optional<Guess> existingOpt = guessRepository.findByUsernameAndSketchId(username, sketchId);
        if (existingOpt.isPresent()) {
            Guess existing = existingOpt.get();
            if (existing.isFinished()) {
                throw new IllegalStateException("Hai già completato questo sketch");
            }
            return buildSessionResponse(sketch, existing);
        }

        Guess newGuess = new Guess(sketchId, username);
        Guess saved = guessRepository.save(newGuess);
        return buildSessionResponse(sketch, saved);
    }

    @Override
    public GuessSessionResponse submitAttempt(String username, String sketchId, String rawWord) {
        Sketch sketch = getValidPlayableSketch(username, sketchId);

        Guess guess = guessRepository.findByUsernameAndSketchId(username, sketchId)
                .orElseGet(() -> guessRepository.save(new Guess(sketchId, username)));

        if (guess.isFinished()) {
            throw new IllegalStateException("Questa partita è già terminata");
        }

        // Normalizzazione: ignora maiuscole/minuscole e spazi superflui
        String normalizedAttempt = rawWord.trim().toLowerCase();
        String normalizedSecret = sketch.getWord().trim().toLowerCase();

        int newAttempts = guess.getAttemptsUsed() + 1;
        guess.setAttemptsUsed(newAttempts);
        guess.getAttemptedWords().add(normalizedAttempt);

        if (normalizedAttempt.equals(normalizedSecret)) {
            // VITTORIA
            guess.setFinished(true);
            guess.setCorrect(true);
            guess.setFinishedAt(Instant.now());
            guessRepository.save(guess);

            applyWinStatistics(username, sketch, newAttempts);
            return buildSessionResponse(sketch, guess);
        }

        if (newAttempts >= MAX_ATTEMPTS) {
            // SCONFITTA PER ESAURIMENTO TENTATIVI
            guess.setFinished(true);
            guess.setCorrect(false);
            guess.setFinishedAt(Instant.now());
            guessRepository.save(guess);

            applyLossStatistics(username, sketch, newAttempts);
            return buildSessionResponse(sketch, guess);
        }

        // TENTATIVO SBAGLIATO MA PARTITA ANCORA APERTA
        Guess updated = guessRepository.save(guess);
        return buildSessionResponse(sketch, updated);
    }

    @Override
    public GuessSessionResponse surrenderGuess(String username, String sketchId) {
        Sketch sketch = getValidPlayableSketch(username, sketchId);

        Guess guess = guessRepository.findByUsernameAndSketchId(username, sketchId)
                .orElseGet(() -> guessRepository.save(new Guess(sketchId, username)));

        if (guess.isFinished()) {
            throw new IllegalStateException("Questa partita è già terminata");
        }

        guess.setFinished(true);
        guess.setCorrect(false);
        guess.setFinishedAt(Instant.now());
        guessRepository.save(guess);

        applyLossStatistics(username, sketch, guess.getAttemptsUsed());
        return buildSessionResponse(sketch, guess);
    }

    @Override
    public List<InProgressGuessResponse> getInProgressGuesses(String username) {
        List<Guess> inProgressList = guessRepository.findByUsernameAndFinishedFalseOrderByStartedAtDesc(username);
        List<InProgressGuessResponse> result = new ArrayList<>();

        for (Guess guess : inProgressList) {
            sketchRepository.findById(guess.getSketchId()).ifPresent(sketch -> {
                if (sketch.isCompleted()) {
                    result.add(new InProgressGuessResponse(
                            sketch.getId(),
                            sketch.getAuthorUsername(),
                            sketch.getImageData(),
                            guess.getAttemptsUsed(),
                            MAX_ATTEMPTS,
                            guess.getStartedAt()
                    ));
                }
            });
        }
        return result;
    }

    private Sketch getValidPlayableSketch(String username, String sketchId) {
        Sketch sketch = sketchRepository.findById(sketchId)
                .orElseThrow(() -> new IllegalArgumentException("Sketch non trovato"));

        if (!sketch.isCompleted()) {
            throw new IllegalStateException("Lo sketch non è ancora stato completato");
        }

        if (sketch.getAuthorUsername().equals(username)) {
            throw new SecurityException("Non puoi indovinare un tuo stesso sketch");
        }

        return sketch;
    }

    private void applyWinStatistics(String playerUsername, Sketch sketch, int attemptsUsed) {
        // 1. Statistiche giocatore che ha indovinato
        userRepository.findByUsername(playerUsername).ifPresent(player -> {
            player.setWordsGuessed(player.getWordsGuessed() + 1);
            player.setTotalGuessAttemptsUsed(player.getTotalGuessAttemptsUsed() + attemptsUsed);
            userRepository.save(player);
        });

        // 2. Statistiche proprietario dello sketch
        userRepository.findByUsername(sketch.getAuthorUsername()).ifPresent(author -> {
            author.setSketchesPlayedByOthers(author.getSketchesPlayedByOthers() + 1);
            author.setSketchesGuessedByOthers(author.getSketchesGuessedByOthers() + 1);
            userRepository.save(author);
        });

        // 3. Registra il giocatore nello sketch
        if (!sketch.getPlayedByUsernames().contains(playerUsername)) {
            sketch.getPlayedByUsernames().add(playerUsername);
            sketchRepository.save(sketch);
        }
    }

    private void applyLossStatistics(String playerUsername, Sketch sketch, int attemptsUsed) {
        // 1. Statistiche giocatore che ha perso/abbandonato
        userRepository.findByUsername(playerUsername).ifPresent(player -> {
            player.setWordsFailed(player.getWordsFailed() + 1);
            player.setTotalGuessAttemptsUsed(player.getTotalGuessAttemptsUsed() + attemptsUsed);
            userRepository.save(player);
        });

        // 2. Statistiche proprietario dello sketch (giocato ma non indovinato)
        userRepository.findByUsername(sketch.getAuthorUsername()).ifPresent(author -> {
            author.setSketchesPlayedByOthers(author.getSketchesPlayedByOthers() + 1);
            userRepository.save(author);
        });

        // 3. Registra il giocatore nello sketch
        if (!sketch.getPlayedByUsernames().contains(playerUsername)) {
            sketch.getPlayedByUsernames().add(playerUsername);
            sketchRepository.save(sketch);
        }
    }

    private GuessSessionResponse buildSessionResponse(Sketch sketch, Guess guess) {
        String status;
        if (!guess.isFinished()) {
            status = "IN_PROGRESS";
        } else {
            status = guess.isCorrect() ? "WON" : "LOST";
        }

        int remaining = Math.max(0, MAX_ATTEMPTS - guess.getAttemptsUsed());
        String revealedWord = guess.isFinished() ? sketch.getWord() : null;

        return new GuessSessionResponse(
                sketch.getId(),
                sketch.getAuthorUsername(),
                sketch.getImageData(),
                sketch.getCreatedAt(),
                status,
                guess.getAttemptsUsed(),
                remaining,
                MAX_ATTEMPTS,
                guess.getAttemptedWords(),
                revealedWord
        );
    }
}