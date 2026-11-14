package com.quicksketch.backend.controller;

import com.quicksketch.backend.dto.auth.AuthResponse;
import com.quicksketch.backend.dto.auth.LoginRequest;
import com.quicksketch.backend.dto.auth.RegisterRequest;
import com.quicksketch.backend.security.JwtUtils;
import com.quicksketch.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public record MessageResponse(String message) {}
    public record CurrentUserResponse(String username) {}

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    // ---------------- REGISTER -------------------------
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---------------- LOGIN ----------------------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(response.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }

    // ---------------- LOGOUT ---------------------------
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        ResponseCookie cleanCookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(new MessageResponse("Logout effettuato con successo"));
    }

    // ---------------- CURRENT USER ---------------------
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new CurrentUserResponse(authentication.getName()));
    }
}