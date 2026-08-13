package com.example.englishlearning.service;

import com.example.englishlearning.dto.UserCreateRequest;
import com.example.englishlearning.dto.UserResponse;
import com.example.englishlearning.entity.User;
import com.example.englishlearning.exception.DuplicateEmailException;
import com.example.englishlearning.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void createUserShouldSaveAndReturnResponse() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("john_doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123!");

        User savedUser = new User("john_doe", "john@example.com", "Password123!");
        savedUser.setId(1L);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertEquals(1L, response.getId());
        assertEquals("john_doe", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void createUserShouldHashPasswordBeforeSave() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("john_doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123!");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertNotEquals("Password123!", capturedUser.getPassword());
        assertTrue(passwordEncoder.matches("Password123!", capturedUser.getPassword()));
    }

    @Test
    void createUserShouldFailWhenEmailAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("john_doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123!");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateEmailException.class, () -> userService.createUser(request));
    }
}
