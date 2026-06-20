package com.stationery.auth.service;

import com.stationery.auth.dto.AuthRequest;
import com.stationery.auth.dto.AuthResponse;
import com.stationery.auth.entity.User;
import com.stationery.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");
        user.setRole("STUDENT");
        user.setName("Test Student");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@test.com", "STUDENT")).thenReturn("mockedToken");

        AuthRequest req = new AuthRequest();
        req.setEmail("test@test.com");
        req.setPassword("rawPassword");

        AuthResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("mockedToken", response.getToken());
        assertEquals("Test Student", response.getName());
    }

    @Test
    void testLoginFailureInvalidPassword() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        AuthRequest req = new AuthRequest();
        req.setEmail("test@test.com");
        req.setPassword("wrongPassword");

        Exception exception = assertThrows(RuntimeException.class, () -> authService.login(req));
        assertEquals("Invalid Password", exception.getMessage());
    }

    @Test
    void testRegisterSuccess() {
        com.stationery.auth.dto.RegisterRequest req = new com.stationery.auth.dto.RegisterRequest();
        req.setEmail("new@test.com");
        req.setPassword("password");
        req.setName("New Student");
        req.setRole("STUDENT");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        String result = authService.register(req);

        assertEquals("User saved successfully", result);
        org.mockito.Mockito.verify(userRepository).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void testRegisterFailureDuplicateEmail() {
        com.stationery.auth.dto.RegisterRequest req = new com.stationery.auth.dto.RegisterRequest();
        req.setEmail("existing@test.com");

        User existingUser = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUser));

        Exception exception = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testValidateToken() {
        org.mockito.Mockito.doNothing().when(jwtUtil).validateToken(anyString());
        assertDoesNotThrow(() -> authService.validateToken("some-token"));
    }
}
