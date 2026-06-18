package com.stationery.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password cannot be empty")
    private String password;
    
    @NotBlank(message = "Role cannot be empty")
    private String role; // e.g., STUDENT or ADMIN

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
