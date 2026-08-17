package com.example.englishlearning.service;

import com.example.englishlearning.service.unsplash.UnsplashSearchResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class UnsplashService {

    private final WebClient webClient;
    private final String accessKey;
    private final int timeoutSeconds;
    // @Autowired
    // public UnsplashService(
    //         @Value("${unsplash.access-key:}") String accessKey,
    //         @Value("${unsplash.timeout-seconds:5}") int timeoutSeconds,
    //         WebClient.Builder webClientBuilder
    // ) {
    //     this(accessKey, timeoutSeconds, webClientBuilder
    //             .baseUrl("https://api.unsplash.com")
    //             .build());
    // }

    // UnsplashService(String accessKey, int timeoutSeconds, WebClient webClient) {
    //     this.accessKey = accessKey;
    //     this.timeoutSeconds = timeoutSeconds;
    //     this.webClient = webClient;
    // }
     @Autowired
    public UnsplashService(
            @Value("${unsplash.access-key:}") String accessKey,
            @Value("${unsplash.timeout-seconds:5}") int timeoutSeconds,
            WebClient.Builder webClientBuilder) {

        this.accessKey = accessKey;
        this.timeoutSeconds = timeoutSeconds;

        this.webClient = webClientBuilder
                .baseUrl("https://api.unsplash.com")
                .build();
    }

    public UnsplashSearchResult searchByWord(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("Word is required");
        }

        if (accessKey == null || accessKey.isBlank()) {
            throw new IllegalStateException("Unsplash access key is not configured");
        }

        try {
            UnsplashSearchResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/photos")
                            .queryParam("query", word.trim())
                            .queryParam("per_page", 1)
                            .queryParam("orientation", "landscape")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Client-ID " + accessKey)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().value() == 429) {
                            return Mono.error(new RateLimitException("Unsplash rate limit exceeded"));
                        }
                        return Mono.error(new UnsplashApiException("Unsplash API request failed: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> 
                            Mono.error(new UnsplashApiException("Unsplash API server error: " + clientResponse.statusCode())))
                    .bodyToMono(UnsplashSearchResponse.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                return UnsplashSearchResult.empty();
            }

            UnsplashSearchResponse.Result firstResult = response.getResults().get(0);
            if (firstResult == null) {
                return UnsplashSearchResult.empty();
            }

            String imageUrl = firstResult.getUrls() != null ? firstResult.getUrls().getRegular() : null;
            String sourceUrl = firstResult.getLinks() != null ? firstResult.getLinks().getHtml() : null;
            String authorName = firstResult.getUser() != null ? firstResult.getUser().getName() : null;
            String authorUrl = firstResult.getUser() != null ? firstResult.getUser().getPortfolioUrl() : null;

            return new UnsplashSearchResult(firstResult.getId(), sourceUrl, authorName, authorUrl, imageUrl);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 429) {
                throw new RateLimitException("Unsplash rate limit exceeded", e);
            }
            throw new UnsplashApiException("Unsplash API call failed", e);
        } catch (RateLimitException e) {
            throw e;
        } catch (Exception e) {
            Throwable unwrapped = Exceptions.unwrap(e);
            if (e instanceof UnsplashApiException) {
                throw (UnsplashApiException) e;
            }
            if (unwrapped instanceof java.util.concurrent.TimeoutException
                    || e instanceof java.util.concurrent.TimeoutException) {
                throw new UnsplashTimeoutException("Unsplash request timed out", e);
            }
            throw new UnsplashApiException("Unsplash API call failed", e);
        }
    }

    public static class UnsplashSearchResult {
        private final String providerPhotoId;
        private final String sourceUrl;
        private final String authorName;
        private final String authorUrl;
        private final String imageUrl;

        public UnsplashSearchResult(String providerPhotoId, String sourceUrl, String authorName,
                                    String authorUrl, String imageUrl) {
            this.providerPhotoId = providerPhotoId;
            this.sourceUrl = sourceUrl;
            this.authorName = authorName;
            this.authorUrl = authorUrl;
            this.imageUrl = imageUrl;
        }

        public static UnsplashSearchResult empty() {
            return new UnsplashSearchResult(null, null, null, null, null);
        }

        public boolean hasImage() {
            return imageUrl != null && !imageUrl.isBlank();
        }

        public String getProviderPhotoId() {
            return providerPhotoId;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

        public String getAuthorName() {
            return authorName;
        }

        public String getAuthorUrl() {
            return authorUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }

    public static class UnsplashApiException extends RuntimeException {
        public UnsplashApiException(String message) {
            super(message);
        }

        public UnsplashApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class UnsplashTimeoutException extends UnsplashApiException {
        public UnsplashTimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class RateLimitException extends UnsplashApiException {
        public RateLimitException(String message) {
            super(message);
        }

        public RateLimitException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
