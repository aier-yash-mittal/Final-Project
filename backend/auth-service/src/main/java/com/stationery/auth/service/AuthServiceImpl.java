package com.stationery.auth.service;

import com.stationery.auth.dto.AuthRequest;
import com.stationery.auth.dto.AuthResponse;
import com.stationery.auth.dto.RegisterRequest;
import com.stationery.auth.entity.User;
import com.stationery.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String register(RegisterRequest request) {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole().toUpperCase());
        userRepository.save(user);
        return "User saved successfully";
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));
        
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
            return new AuthResponse(token, user.getName(), user.getRole(), "Login successful");
        } else {
            throw new RuntimeException("Invalid Password");
        }
    }

    @Override
    public void validateToken(String token) {
        jwtUtil.validateToken(token);
    }
}
