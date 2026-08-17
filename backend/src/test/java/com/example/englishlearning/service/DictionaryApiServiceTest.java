package com.example.englishlearning.service;

import com.example.englishlearning.service.dictionary.DictionaryWordResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DictionaryApiServiceTest {

    @Test
    void searchWordReturnsParsedPronunciationAndAudio() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        [
                          {
                            "meta": {"id": "river"},
                            "hwi": {
                              "hw": "river",
                              "prs": [
                                {
                                  "geo": "uk",
                                  "ipa": "ˈriv-ɚ",
                                  "sound": {
                                    "audio": "bix00002",
                                    "mp3": "https://media.merriam-webster.com/audio/prons/en/gb/mp3/r/river002.mp3"
                                  }
                                },
                                {
                                  "geo": "us",
                                  "ipa": "ˈriv-ər",
                                  "sound": {
                                    "audio": "bix00001",
                                    "mp3": "https://media.merriam-webster.com/audio/prons/en/us/mp3/r/river001.mp3"
                                  }
                                }
                              ]
                            }
                          }
                        ]
                        """));
        server.start();

        try {
            DictionaryApiService service = new DictionaryApiService(server.url("/").toString(), "test-key", 3,
                    WebClient.builder());

            DictionaryWordResult result = service.searchWord("river");

            assertNotNull(result);
            assertEquals("river", result.getWord());
            assertEquals("ˈriv-ɚ", result.getUkPronunciation());
            assertEquals("ˈriv-ər", result.getUsPronunciation());
            assertEquals("ˈriv-ɚ", result.getIpa());
            assertEquals("https://media.merriam-webster.com/audio/prons/en/gb/mp3/r/river002.mp3", result.getUkAudioUrl());
            assertEquals("https://media.merriam-webster.com/audio/prons/en/us/mp3/r/river001.mp3", result.getUsAudioUrl());
        } finally {
            server.shutdown();
        }
    }

    @Test
    void searchWordDoesNotInferAccentWhenRegionIsMissing() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        [
                          {
                            "meta": {"id": "glimmer"},
                            "hwi": {
                              "hw": "glimmer",
                              "prs": [
                                {
                                  "ipa": "ˈɡlɪm.ər"
                                }
                              ]
                            }
                          }
                        ]
                        """));
        server.start();

        try {
            DictionaryApiService service = new DictionaryApiService(server.url("/").toString(), "test-key", 3,
                    WebClient.builder());

            DictionaryWordResult result = service.searchWord("glimmer");

            assertNotNull(result);
            assertEquals("glimmer", result.getWord());
            assertEquals("ˈɡlɪm.ər", result.getIpa());
            assertNull(result.getUsPronunciation());
            assertNull(result.getUkPronunciation());
            assertNull(result.getUsAudioUrl());
            assertNull(result.getUkAudioUrl());
        } finally {
            server.shutdown();
        }
    }

    @Test
    void searchWordReturnsEmptyWhenNotFound() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{\"title\":\"No Definitions Found\"}"));
        server.start();

        try {
            DictionaryApiService service = new DictionaryApiService(server.url("/").toString(), "test-key", 3,
                    WebClient.builder());

            DictionaryWordResult result = service.searchWord("xyznotfoundword");

            assertNotNull(result);
            assertEquals("xyznotfoundword", result.getWord());
            assertNull(result.getIpa());
            assertNull(result.getUsAudioUrl());
        } finally {
            server.shutdown();
        }
    }

    @Test
    void searchWordHandlesMissingPhoneticsAndAudio() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        [
                          {
                            "meta": {"id": "glimmer"},
                            "hwi": {"hw": "glimmer"}
                          }
                        ]
                        """));
        server.start();

        try {
            DictionaryApiService service = new DictionaryApiService(server.url("/").toString(), "test-key", 3,
                    WebClient.builder());

            DictionaryWordResult result = service.searchWord("glimmer");

            assertEquals("glimmer", result.getWord());
            assertNull(result.getIpa());
            assertNull(result.getUsAudioUrl());
            assertNull(result.getUkAudioUrl());
        } finally {
            server.shutdown();
        }
    }

    @Test
    void searchWordHandlesTimeout() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBodyDelay(2, java.util.concurrent.TimeUnit.SECONDS)
                .setBody("[]"));
        server.start();

        try {
            DictionaryApiService service = new DictionaryApiService(server.url("/").toString(), "test-key", 1,
                    WebClient.builder());

            DictionaryWordResult result = service.searchWord("slowword");

            assertEquals("slowword", result.getWord());
            assertNull(result.getIpa());
        } finally {
            server.shutdown();
        }
    }
}
