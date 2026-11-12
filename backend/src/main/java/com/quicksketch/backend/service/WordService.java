package com.quicksketch.backend.service;

import java.util.List;

public interface WordService {
    String getRandomWord();
    List<String> getRandomWords(int count);
}