package com.quicksketch.backend.repository;

import com.quicksketch.backend.model.Sketch;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SketchRepository extends MongoRepository<Sketch, String> {
    List<Sketch> findByAuthorUsername(String authorUsername);
}