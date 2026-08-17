package com.example.englishlearning.service;

import com.example.englishlearning.service.dictionary.DictionaryWordResult;
import com.example.englishlearning.service.merriamwebster.MerriamWebsterEntry;
import com.example.englishlearning.service.merriamwebster.MerriamWebsterPronunciation;
import com.example.englishlearning.service.merriamwebster.MerriamWebsterSound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DictionaryApiService {

    private static final Logger log = LoggerFactory.getLogger(DictionaryApiService.class);

    private final WebClient webClient;
    private final String apiKey;
    private final int timeoutSeconds;

    public DictionaryApiService(WebClient dictionaryWebClient,
                               @Value("${merriam-webster.api.key:}") String apiKey,
                               @Value("${merriam-webster.api.timeout-seconds:10}") int timeoutSeconds) {
        this.webClient = dictionaryWebClient;
        this.apiKey = apiKey;
        this.timeoutSeconds = timeoutSeconds;
    }

    @org.springframework.beans.factory.annotation.Autowired
    public DictionaryApiService(@Value("${merriam-webster.api.base-url:https://www.dictionaryapi.com/api/v3/references/collegiate/json}") String baseUrl,
                               @Value("${merriam-webster.api.key:}") String apiKey,
                               @Value("${merriam-webster.api.timeout-seconds:10}") int timeoutSeconds,
                               WebClient.Builder webClientBuilder) {
        this(webClientBuilder
                .baseUrl(baseUrl)
                .build(), apiKey, timeoutSeconds);
    }

    public DictionaryWordResult searchWord(String word) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("Word is required");
        }

        String normalizedWord = word.trim();

        try {
            List<MerriamWebsterEntry> entries = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/{word}");
                        if (apiKey != null && !apiKey.isBlank()) {
                            uriBuilder.queryParam("key", apiKey);
                        }
                        return uriBuilder.build(normalizedWord);
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        if (clientResponse.statusCode().value() == 404) {
                            return Mono.error(new DictionaryApiNotFoundException("Dictionary entry not found for word: " + normalizedWord));
                        }
                        return Mono.error(new DictionaryApiException("Merriam-Webster API request failed: " + clientResponse.statusCode()));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            Mono.error(new DictionaryApiException("Merriam-Webster API server error: " + clientResponse.statusCode())))
                    .bodyToMono(new ParameterizedTypeReference<List<MerriamWebsterEntry>>() {})
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .block();

            if (entries == null || entries.isEmpty()) {
                return DictionaryWordResult.empty(normalizedWord, "MERRIAM_WEBSTER");
            }

            MerriamWebsterEntry entry = entries.get(0);
            return parseEntry(entry, normalizedWord);
        } catch (DictionaryApiNotFoundException e) {
            log.warn("Merriam-Webster API returned 404 for word '{}': {}", normalizedWord, e.getMessage());
            return DictionaryWordResult.empty(normalizedWord, "MERRIAM_WEBSTER");
        } catch (WebClientResponseException e) {
            log.warn("Merriam-Webster API request failed for word '{}': {}", normalizedWord, e.getMessage());
            return DictionaryWordResult.empty(normalizedWord, "MERRIAM_WEBSTER");
        } catch (RuntimeException e) {
            if (e instanceof DictionaryApiException) {
                log.warn("Merriam-Webster API request failed for word '{}': {}", normalizedWord, e.getMessage());
                return DictionaryWordResult.empty(normalizedWord, "MERRIAM_WEBSTER");
            }
            if (e.getCause() instanceof java.util.concurrent.TimeoutException) {
                log.warn("Merriam-Webster API request timed out for word '{}': {}", normalizedWord, e.getMessage());
                return DictionaryWordResult.empty(normalizedWord, "MERRIAM_WEBSTER");
            }
            log.error("Unexpected exception while retrieving Merriam-Webster data for word '{}'", normalizedWord, e);
            return DictionaryWordResult.empty(normalizedWord, "MERRIAM_WEBSTER");
        }
    }

    private DictionaryWordResult parseEntry(MerriamWebsterEntry entry, String fallbackWord) {
        if (entry == null) {
            return DictionaryWordResult.empty(fallbackWord, "MERRIAM_WEBSTER");
        }

        String word = Optional.ofNullable(entry.getWord()).orElse(fallbackWord);
        String usPronunciation = null;
        String ukPronunciation = null;
        String ipa = null;
        String usAudioUrl = null;
        String ukAudioUrl = null;
        List<String> definitions = new ArrayList<>();

        if (entry.getShortdef() != null) {
            definitions.addAll(entry.getShortdef());
        }

        if (entry.getHwi() != null && entry.getHwi().getPrs() != null) {
            for (MerriamWebsterPronunciation pronunciation : entry.getHwi().getPrs()) {
                if (pronunciation == null) {
                    continue;
                }

                String pronunciationText = pronunciation.getIpa();
                if (pronunciationText != null && !pronunciationText.isBlank() && ipa == null) {
                    ipa = pronunciationText;
                }

                String geo = pronunciation.getGeo();
                boolean isUsCandidate = isUsRegion(geo);
                boolean isUkCandidate = isUkRegion(geo);

                if (pronunciationText != null && !pronunciationText.isBlank()) {
                    if (isUsCandidate && usPronunciation == null) {
                        usPronunciation = pronunciationText;
                    }
                    if (isUkCandidate && ukPronunciation == null) {
                        ukPronunciation = pronunciationText;
                    }
                }

                String audioUrl = resolveAudioUrl(pronunciation.getSound());
                if (audioUrl == null || audioUrl.isBlank()) {
                    continue;
                }

                if (isUsCandidate && usAudioUrl == null) {
                    usAudioUrl = audioUrl;
                } else if (isUkCandidate && ukAudioUrl == null) {
                    ukAudioUrl = audioUrl;
                }
            }
        }

        return new DictionaryWordResult(
                word,
                usPronunciation,
                ukPronunciation,
                ipa,
                usAudioUrl,
                ukAudioUrl,
                definitions,
                List.of(),
                "MERRIAM_WEBSTER"
        );
    }

    private String resolveAudioUrl(MerriamWebsterSound sound) {
        if (sound == null) {
            return null;
        }

        if (sound.getMp3() != null && !sound.getMp3().isBlank()) {
            return sound.getMp3();
        }

        if (sound.getAudio() != null && !sound.getAudio().isBlank()) {
            return "https://media.merriam-webster.com/audio/prons/en/us/mp3/" + sound.getAudio().substring(0, 1) + "/" + sound.getAudio() + ".mp3";
        }

        return null;
    }

    private boolean isUsRegion(String region) {
        return region != null && (region.equalsIgnoreCase("us") || region.equalsIgnoreCase("american") || region.equalsIgnoreCase("usa"));
    }

    private boolean isUkRegion(String region) {
        return region != null && (region.equalsIgnoreCase("uk") || region.equalsIgnoreCase("british") || region.equalsIgnoreCase("gb"));
    }

    public static class DictionaryApiException extends RuntimeException {
        public DictionaryApiException(String message) {
            super(message);
        }

        public DictionaryApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DictionaryApiNotFoundException extends DictionaryApiException {
        public DictionaryApiNotFoundException(String message) {
            super(message);
        }
    }
}
