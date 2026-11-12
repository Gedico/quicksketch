package com.quicksketch.backend.service;

import com.quicksketch.backend.dto.guess.GuessSessionResponse;
import com.quicksketch.backend.dto.guess.InProgressGuessResponse;

import java.util.List;

public interface GuessService {

    GuessSessionResponse startOrResumeGuess(String username, String sketchId);

    GuessSessionResponse submitAttempt(String username, String sketchId, String word);

    GuessSessionResponse surrenderGuess(String username, String sketchId);

    List<InProgressGuessResponse> getInProgressGuesses(String username);
}