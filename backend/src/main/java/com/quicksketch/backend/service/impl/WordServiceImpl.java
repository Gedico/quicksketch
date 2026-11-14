package com.quicksketch.backend.service.impl;

import com.quicksketch.backend.exception.game.GameRuleViolationException;
import com.quicksketch.backend.service.WordService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WordServiceImpl implements WordService {

    private static final Logger logger = LoggerFactory.getLogger(WordServiceImpl.class);
    private final List<String> words = new ArrayList<>();

    @PostConstruct
    public void loadWords() {
        ClassPathResource resource = new ClassPathResource("words.txt");
        Set<String> uniqueWords = new LinkedHashSet<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                for (String word : line.split(",")) {
                    String cleaned = word.trim().toLowerCase();
                    if (!cleaned.isEmpty()) {
                        uniqueWords.add(cleaned);
                    }
                }
            }
            words.addAll(uniqueWords);
            logger.info("WordService: caricate {} parole uniche da words.txt", words.size());
        } catch (Exception e) {
            logger.error("Errore durante la lettura di words.txt: {}", e.getMessage());
        }

        if (words.isEmpty()) {
            words.add("disegno");
        }
    }

    @Override
    public String getRandomWord() {
        return words.get(ThreadLocalRandom.current().nextInt(words.size()));
    }

    @Override
    public List<String> getRandomWords(int count) {
        if (count <= 0) {
            throw new GameRuleViolationException("Il numero di parole richieste deve essere maggiore di zero.");
        }

        if (words.size() <= count) {
            return new ArrayList<>(words);
        }

        List<String> copy = new ArrayList<>(words);
        Collections.shuffle(copy, ThreadLocalRandom.current());
        return new ArrayList<>(copy.subList(0, count));
    }
}