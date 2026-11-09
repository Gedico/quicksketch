package com.quicksketch.backend.repository;

import com.quicksketch.backend.model.GuessAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface GuessAttemptRepository extends MongoRepository<GuessAttempt, String> {
    Optional<GuessAttempt> findByUserIdAndSketchId(String userId, String sketchId);
}