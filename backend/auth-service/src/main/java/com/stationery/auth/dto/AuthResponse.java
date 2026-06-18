package com.stationery.auth.dto;

public class AuthResponse {
    private String token;
    private String name;
    private String role;
    private String message;

    public AuthResponse(String token, String name, String role, String message) {
        this.token = token;
        this.name = name;
        this.role = role;
        this.message = message;
    }

    // Getters
    public String getToken() { return token; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
}
