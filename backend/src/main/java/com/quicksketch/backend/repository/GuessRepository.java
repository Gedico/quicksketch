package com.quicksketch.backend.repository;

import com.quicksketch.backend.model.Guess;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GuessRepository extends MongoRepository<Guess, String> {

    List<Guess> findByUsername(String username);

    Optional<Guess> findByUsernameAndSketchId(String username, String sketchId);

    // Recupera le partite iniziate dall'utente e ancora in sospeso (finished = false)
    List<Guess> findByUsernameAndFinishedFalseOrderByStartedAtDesc(String username);
}