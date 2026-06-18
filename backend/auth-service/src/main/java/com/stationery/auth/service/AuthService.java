package com.stationery.auth.service;

import com.stationery.auth.dto.AuthRequest;
import com.stationery.auth.dto.AuthResponse;
import com.stationery.auth.dto.RegisterRequest;

public interface AuthService {
    String register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    void validateToken(String token);
}
