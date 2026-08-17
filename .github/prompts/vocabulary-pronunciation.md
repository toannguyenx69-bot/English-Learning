# Vocabulary Pronunciation Feature

## Goal

Add pronunciation support to the English vocabulary feature.

For each vocabulary word, the application should support:

1. US pronunciation
2. UK pronunciation
3. IPA pronunciation
4. Actual pronunciation audio when available from Dictionary API
5. Browser Text-to-Speech as a fallback

The implementation must be incremental and should not break existing vocabulary,
image, or Unsplash functionality.

---

# Architecture

Frontend:
- React
- TypeScript
- Browser Web Speech API

Backend:
- Java
- Spring Boot
- WebClient
- MySQL

External API:
- Free Dictionary API

The backend is responsible for retrieving and caching pronunciation
metadata/audio URLs.

The frontend is responsible for browser Text-to-Speech.

---

# Important design rule

Do NOT call the Dictionary API every time the vocabulary detail page is opened.

Flow:

First request:

Vocabulary
    |
    v
Check MySQL pronunciation
    |
    +-- exists --> return cached pronunciation
    |
    +-- not exists
            |
            v
      Dictionary API
            |
            v
      Parse pronunciation
            |
            v
       Save MySQL
            |
            v
          Return

Subsequent requests:

Vocabulary
    |
    v
MySQL
    |
    v
Cached pronunciation

No external API call.