package com.quicksketch.backend.repository;

import com.quicksketch.backend.model.Sketch;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface SketchRepository extends MongoRepository<Sketch, String> {

    // Per la galleria pubblica (utenti non loggati)
    List<Sketch> findAllByCompletedTrueOrderByCreatedAtDesc();

    // Per la vista giocabile (utenti loggati: esclude i propri e quelli già tentati)
    List<Sketch> findByCompletedTrueAndAuthorUsernameNotAndIdNotInOrderByCreatedAtDesc(
            String authorUsername,
            Collection<String> excludedIds
    );
}