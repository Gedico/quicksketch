package com.quicksketch.backend.service;

import com.quicksketch.backend.dto.auth.*;
import com.quicksketch.backend.exception.auth.InvalidCredentialsException;
import com.quicksketch.backend.exception.auth.InvalidRegistrationDataException;
import com.quicksketch.backend.exception.auth.UserAlreadyExistsException;
import com.quicksketch.backend.model.User;
import com.quicksketch.backend.repository.UserRepository;
import com.quicksketch.backend.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // ------------------ REGISTER ------------------------
    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername() != null ? request.getUsername().trim() : "";
        String email = request.getEmail() != null ? request.getEmail().trim() : "";
        String password = request.getPassword() != null ? request.getPassword() : "";

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new InvalidRegistrationDataException("Tutti i campi sono obbligatori.");
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new InvalidRegistrationDataException("Inserisci un indirizzo email valido (deve contenere '@' e '.').");
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new InvalidRegistrationDataException("La password deve contenere almeno 8 caratteri, una lettera maiuscola e un numero.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Lo username '" + username + "' è già stato scelto. Scegline un altro.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("L'email '" + email + "' è già registrata.");
        }

        User user = new User(
                username,
                email,
                passwordEncoder.encode(password)
        );

        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }

    // ------------------ LOGIN ---------------------------
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Username o password non validi."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Username o password non validi.");
        }

        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
}