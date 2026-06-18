package com.stationery.auth.controller;

import com.stationery.auth.dto.AuthRequest;
import com.stationery.auth.dto.AuthResponse;
import com.stationery.auth.dto.RegisterRequest;
import com.stationery.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new STUDENT or ADMIN account")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate Token", description = "Checks if the provided JWT token is valid")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return ResponseEntity.ok("Token is valid");
    }
}
