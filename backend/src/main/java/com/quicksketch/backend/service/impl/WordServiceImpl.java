package com.quicksketch.backend.service.impl;

import com.quicksketch.backend.service.WordService;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WordServiceImpl implements WordService {

    private final List<String> words = new ArrayList<>();

    @PostConstruct
    public void loadWords() {
        ClassPathResource resource = new ClassPathResource("words.txt");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                for (String word : line.split(",")) {
                    String cleaned = word.trim().toLowerCase();
                    if (!cleaned.isEmpty()) {
                        words.add(cleaned);
                    }
                }
            }
            System.out.println(">>> WordService: caricate " + words.size() + " parole da words.txt");
        } catch (Exception e) {
            System.err.println(">>> ERRORE lettura words.txt: " + e.getMessage());
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
        if (words.size() <= count) {
            return new ArrayList<>(words);
        }
        List<String> copy = new ArrayList<>(words);
        java.util.Collections.shuffle(copy, ThreadLocalRandom.current());
        return copy.subList(0, count);
    }



}