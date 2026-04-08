package com.retail.pos.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "4b633534346231333464333535313437346233373539353934653530346234313531333134393633343633383335343834313333333934393431353234643530";
    private final String issuer = "pos-backend";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtService, "jwtIssuer", issuer);
    }

    @Test
    void shouldGenerateValidToken() {
        String userId = "user-123";
        String username = "testuser";
        Map<String, Object> claims = Collections.singletonMap("role", "ADMIN");

        String token = jwtService.generateToken(userId, username, claims);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertEquals(userId, jwtService.extractSubject(token));
        assertEquals("ADMIN", jwtService.extractClaim(token, c -> c.get("role", String.class)));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        assertFalse(jwtService.isTokenValid("invalid.token.here"));
    }
}
