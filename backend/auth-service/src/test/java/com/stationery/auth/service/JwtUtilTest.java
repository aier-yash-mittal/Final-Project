package com.stationery.auth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String validSecret;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Generate a valid base64 encoded 256-bit key for testing
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        validSecret = Encoders.BASE64.encode(key.getEncoded());

        // Use reflection to set the @Value annotated private fields
        ReflectionTestUtils.setField(jwtUtil, "secret", validSecret);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 3600000L); // 1 hour
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("test@example.com", "ADMIN");
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3, "JWT should have 3 parts");
    }

    @Test
    void testValidateTokenSuccess() {
        String token = jwtUtil.generateToken("test@example.com", "STUDENT");
        assertDoesNotThrow(() -> jwtUtil.validateToken(token));
    }

    @Test
    void testValidateTokenFailure() {
        // Create an invalid token by modifying a valid one
        String validToken = jwtUtil.generateToken("test@example.com", "STUDENT");
        String invalidToken = validToken.substring(0, validToken.length() - 5) + "abcde";

        assertThrows(Exception.class, () -> jwtUtil.validateToken(invalidToken));
    }
}
