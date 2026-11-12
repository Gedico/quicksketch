package com.quicksketch.backend.repository;

import com.quicksketch.backend.model.Guess;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GuessRepository extends MongoRepository<Guess, String> {

    // Recupera tutti i tentativi (indovinati o sbagliati) effettuati da un utente
    List<Guess> findByUsername(String username);

    // Ci servirà subito nello step successivo per verificare se l'utente ha già tentato
    boolean existsByUsernameAndSketchId(String username, String sketchId);
}