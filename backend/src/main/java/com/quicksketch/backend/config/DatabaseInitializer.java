package com.quicksketch.backend.config;

import com.quicksketch.backend.model.WordPool;
import com.quicksketch.backend.repository.WordPoolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("seed")// Si attiva solo se lanci l'app col profilo 'seed'
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initWordPool(WordPoolRepository wordPoolRepository) {
        return args -> {
            // Popola solo se la collezione 'words' è completamente vuota
            if (wordPoolRepository.count() == 0) {
                List<String> words = List.of(
                        "Gatto", "Cane", "Chitarra", "Sole", "Automobile",
                        "Casa", "Albero", "Computer", "Pizza", "Telefono",
                        "Aereo", "Libro", "Orologio", "Bicicletta", "Faro"
                );
                words.forEach(w -> {
                    try {
                        wordPoolRepository.save(new WordPool(w));
                    } catch (Exception ignored) {
                        // Ignora eventuali duplicati in sicurezza
                    }
                });
                System.out.println(">> MongoDB Atlas: Collezione 'words' inizializzata con successo!");
            }
        };
    }
}