package com.quicksketch.backend.repository;

import com.quicksketch.backend.model.WordPool;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WordPoolRepository extends MongoRepository<WordPool, String> {
}