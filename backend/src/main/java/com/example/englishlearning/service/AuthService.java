package com.example.englishlearning.service;

import com.example.englishlearning.dto.LoginRequest;
import com.example.englishlearning.dto.LoginResponse;
import com.example.englishlearning.entity.User;
import com.example.englishlearning.exception.InvalidCredentialsException;
import com.example.englishlearning.repository.UserRepository;
import com.example.englishlearning.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            String email = authentication.getName();
            userRepository.findByEmail(email)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

            String token = jwtTokenProvider.generateToken(email);
            return new LoginResponse(token, "Bearer");
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }
}
