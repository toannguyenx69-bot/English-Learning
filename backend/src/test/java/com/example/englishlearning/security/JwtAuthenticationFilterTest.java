package com.example.englishlearning.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private JwtTokenProvider tokenProvider;
    private UserDetailsService userDetailsService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        tokenProvider = Mockito.mock(JwtTokenProvider.class);
        userDetailsService = Mockito.mock(UserDetailsService.class);
        filter = new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Test
    void doFilterInternalSetsAuthenticationWhenTokenValid() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(tokenProvider.validateToken("token123")).thenReturn(true);
        when(tokenProvider.getEmailFromToken("token123")).thenReturn("john@example.com");

        UserDetails userDetails = User.withUsername("john@example.com").password("password").authorities("ROLE_USER").build();
        when(userDetailsService.loadUserByUsername(eq("john@example.com"))).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
