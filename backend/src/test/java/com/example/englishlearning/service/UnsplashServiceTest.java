package com.example.englishlearning.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnsplashServiceTest {

    @Test
    void searchByWordReturnsFirstResultWhenAvailable() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"results\":[{\"id\":\"abc123\",\"urls\":{\"regular\":\"https://images.unsplash.com/test.jpg\"},\"user\":{\"name\":\"Jane Doe\",\"portfolio_url\":\"https://unsplash.com/@jane\"},\"links\":{\"html\":\"https://unsplash.com/photos/abc123\"}}] }"));
        server.start();

        try {
            WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(server.url("/").toString());
            UnsplashService service = new UnsplashService("test-key", 5, webClientBuilder);

            UnsplashService.UnsplashSearchResult result = service.searchByWord("river");

            assertNotNull(result);
            assertTrue(result.hasImage());
            assertEquals("abc123", result.getProviderPhotoId());
            assertEquals("https://images.unsplash.com/test.jpg", result.getImageUrl());
            assertEquals("Jane Doe", result.getAuthorName());
        } finally {
            server.shutdown();
        }
    }

    @Test
    void searchByWordReturnsEmptyWhenNoResults() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("{\"results\":[]}"));
        server.start();

        try {
            WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(server.url("/").toString());
            UnsplashService service = new UnsplashService("test-key", 5, webClientBuilder);

            UnsplashService.UnsplashSearchResult result = service.searchByWord("nonexistent-word-xyz");

            assertFalse(result.hasImage());
            assertEquals(null, result.getImageUrl());
        } finally {
            server.shutdown();
        }
    }

    @Test
    void searchByWordThrowsWhenAccessKeyMissing() {
        UnsplashService service = new UnsplashService("", 5, WebClient.builder());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> service.searchByWord("river"));
        assertEquals("Unsplash access key is not configured", ex.getMessage());
    }

    @Test
    void searchByWordThrowsWhenRateLimited() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(429).setBody("{\"errors\":[\"Rate limit exceeded\"]}"));
        server.start();

        try {
            WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(server.url("/").toString());
            UnsplashService service = new UnsplashService("test-key", 5, webClientBuilder);

            UnsplashService.RateLimitException ex = assertThrows(UnsplashService.RateLimitException.class,
                    () -> service.searchByWord("river"));
            assertTrue(ex.getMessage().contains("rate limit"));
        } finally {
            server.shutdown();
        }
    }

    @Test
    void searchByWordThrowsWhenRequestTimesOut() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBodyDelay(2, TimeUnit.SECONDS).setBody("{\"results\":[]}"));
        server.start();

        try {
            WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(server.url("/").toString());
            UnsplashService service = new UnsplashService("test-key", 1, webClientBuilder);

            UnsplashService.UnsplashApiException ex = assertThrows(UnsplashService.UnsplashApiException.class,
                    () -> service.searchByWord("river"));
            assertTrue(ex.getMessage().contains("failed") || ex.getMessage().contains("timed out"));
        } finally {
            server.shutdown();
        }
    }
}
