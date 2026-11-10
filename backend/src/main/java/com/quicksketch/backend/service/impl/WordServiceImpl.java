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
import java.util.Random;

@Service
public class WordServiceImpl implements WordService {

    private final List<String> words = new ArrayList<>();
    private final Random random = new Random();

    @PostConstruct
    public void loadWords() {
        try {
            ClassPathResource resource = new ClassPathResource("words.txt");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                String[] parsedWords = line.split(",");
                for (String word : parsedWords) {
                    words.add(word.trim().toLowerCase());
                }
            }
        } catch (Exception e) {
            words.add("disegno");
        }
    }

    @Override
    public String getRandomWord() {
        if (words.isEmpty()) {
            return "disegno";
        }
        return words.get(random.nextInt(words.size()));
    }
}