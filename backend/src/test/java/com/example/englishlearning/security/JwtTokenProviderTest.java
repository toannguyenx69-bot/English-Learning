package com.example.englishlearning.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", "ThisIsASecretKeyForTestingPurposes1234567890");
        ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", 3600000L);
        tokenProvider.init();
    }

    @Test
    void generateTokenReturnsValidJwt() {
        String token = tokenProvider.generateToken("john@example.com");

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertTrue(tokenProvider.getEmailFromToken(token).contains("john@example.com"));
    }
}
