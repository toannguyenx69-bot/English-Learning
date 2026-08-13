package com.example.englishlearning.controller;

import com.example.englishlearning.dto.UserCreateRequest;
import com.example.englishlearning.dto.UserResponse;
import com.example.englishlearning.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUserReturnsCreated() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("john_doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123!");

        UserResponse response = new UserResponse(1L, "john_doe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        when(userService.createUser(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getUserReturnsOk() throws Exception {
        UserResponse response = new UserResponse(1L, "john_doe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getCurrentUserReturnsAuthenticatedProfile() throws Exception {
        UserResponse response = new UserResponse(1L, "john_doe", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        when(userService.getCurrentUser("john@example.com")).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me")
                        .with(user("john@example.com").password("pass").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void updateCurrentUserReturnsUpdatedProfile() throws Exception {
        UserResponse response = new UserResponse(1L, "john_updated", "john@example.com", LocalDateTime.now(), LocalDateTime.now());
        when(userService.updateCurrentUser(eq("john@example.com"), any())).thenReturn(response);

        String body = "{\"username\":\"john_updated\",\"email\":\"john@example.com\"}";

        mockMvc.perform(put("/api/v1/users/me")
                        .with(csrf())
                        .with(user("john@example.com").password("pass").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void updateCurrentUserRejectsInvalidProfileInput() throws Exception {
        String body = "{\"username\":\"\",\"email\":\"not-an-email\"}";

        mockMvc.perform(put("/api/v1/users/me")
                        .with(csrf())
                        .with(user("john@example.com").password("pass").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
