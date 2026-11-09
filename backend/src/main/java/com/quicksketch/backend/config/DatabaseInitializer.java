package com.quicksketch.backend.config;

import com.quicksketch.backend.model.WordPool;
import com.quicksketch.backend.repository.WordPoolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initWordPool(WordPoolRepository wordPoolRepository) {
        return args -> {
            if (wordPoolRepository.count() == 0) {
                List<String> words = List.of(
                        "Gatto", "Cane", "Chitarra", "Sole", "Automobile",
                        "Casa", "Albero", "Computer", "Pizza", "Telefono",
                        "Aereo", "Libro", "Orologio", "Bicicletta", "Faro"
                );
                words.forEach(w -> wordPoolRepository.save(new WordPool(w)));
                System.out.println(">> Database Atlas Popolato: Word pool inizializzato con " + words.size() + " parole.");
            }
        };
    }
}