package com.quicksketch.backend.repository;

import com.quicksketch.backend.model.Sketch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SketchRepository extends MongoRepository<Sketch, String> {
    long countByAuthorUsernameAndCompletedTrue(String authorUsername);
    List<Sketch> findAllByCompletedTrueOrderByCreatedAtDesc();
}