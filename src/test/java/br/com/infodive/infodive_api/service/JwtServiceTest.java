package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String MOCK_SECRET = "9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", MOCK_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L); // 24h
    }

    @Test
    void generateToken_ShouldReturnValidJwtToken() {
        String token = jwtService.generateToken("admin@infodive.com.br", "Admin User", "ROLE_ADMIN");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void extractEmail_ShouldReturnCorrectSubject() {
        String email = "dev@infodive.com.br";
        String token = jwtService.generateToken(email, "Dev User", "ROLE_ADMIN");

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void extractRole_ShouldReturnCorrectRole() {
        String token = jwtService.generateToken("user@infodive.com.br", "Test User", "ROLE_BLOGGER");

        String role = jwtService.extractRole(token);

        assertEquals("ROLE_BLOGGER", role);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidOrMalformedToken() {
        assertFalse(jwtService.isTokenValid("invalid.jwt.token"));
        assertFalse(jwtService.isTokenValid(""));
        assertFalse(jwtService.isTokenValid(null));
    }
}
