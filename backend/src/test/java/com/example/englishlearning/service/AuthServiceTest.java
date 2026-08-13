package com.example.englishlearning.service;

import com.example.englishlearning.dto.LoginRequest;
import com.example.englishlearning.dto.LoginResponse;
import com.example.englishlearning.entity.User;
import com.example.englishlearning.exception.InvalidCredentialsException;
import com.example.englishlearning.repository.UserRepository;
import com.example.englishlearning.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        userRepository = Mockito.mock(UserRepository.class);
        jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
        authService = new AuthService(authenticationManager, userRepository, jwtTokenProvider);
    }

    @Test
    void loginReturnsJwtWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("Password123!");

        Authentication authentication = new UsernamePasswordAuthenticationToken("john@example.com", "Password123!");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User("john_doe", "john@example.com", "hashed")));
        when(jwtTokenProvider.generateToken("john@example.com")).thenReturn("jwt-token-123");

        LoginResponse response = authService.login(request);

        assertNotNull(response.getAccessToken());
        assertEquals("jwt-token-123", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void loginThrowsInvalidCredentialsWhenAuthenticationFails() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }
}
