# Replace Dictionary API Provider

## Goal

Replace the current external pronunciation provider:

https://api.dictionaryapi.dev

with:

Merriam-Webster Dictionary API.

This is a provider replacement only.
The existing pronunciation feature must continue to work without
changing the frontend API contract or the persistence model.

---

## Scope restrictions

Do not redesign the existing vocabulary pronunciation feature.

Keep the existing MySQL table:

vocabulary_pronunciations

Keep the existing VocabularyPronunciation entity unchanged unless
absolutely necessary.

Keep VocabularyPronunciationService API unchanged.

Keep VocabularyPronunciationController API unchanged.

Only replace the external dictionary provider used by
DictionaryApiService.

Do not modify Unsplash functionality.

Keep Browser SpeechSynthesis as the fallback for audio.

---

## Existing architecture

VocabularyPronunciationController
        |
        v
VocabularyPronunciationService
        |
        v
DictionaryApiService
        |
        v
api.dictionaryapi.dev

Replace only the external API implementation:

VocabularyPronunciationController
        |
        v
VocabularyPronunciationService
        |
        v
DictionaryApiService
        |
        v
Merriam-Webster API

---

## Database

Keep the existing table:

vocabulary_pronunciations

Keep these fields:

- vocabulary_id
- us_pronunciation
- uk_pronunciation
- ipa
- us_audio_url
- uk_audio_url
- source
- created_at
- updated_at

Do not create a new pronunciation table.

Set:

source = "MERRIAM_WEBSTER"

---

## Configuration

Add configuration:

merriam-webster.api.base-url
merriam-webster.api.key
merriam-webster.api.timeout-seconds

Example:

merriam-webster.api.base-url=https://www.dictionaryapi.com/api/v3/references/collegiate/json
merriam-webster.api.key=${MERRIAM_WEBSTER_API_KEY}
merriam-webster.api.timeout-seconds=10

Never hard-code the API key.

Never commit the API key to Git.

---

## Backend

Replace the current api.dictionaryapi.dev implementation inside
DictionaryApiService.

Use Spring WebClient.

The service should:

1. Receive a vocabulary word.
2. Call Merriam-Webster API.
3. Parse the response.
4. Extract pronunciation information.
5. Extract audio information when available.
6. Map the result to the existing pronunciation model.
7. Return the result to VocabularyPronunciationService.

No controller, service, or entity contract changes are allowed
unless a required field mapping makes them unavoidable.

---

## Caching

The existing caching strategy must remain unchanged.

Flow:

Request
  |
  v
MySQL
  |
  +-- pronunciation exists
  |       |
  |       +-- return cached data
  |
  +-- pronunciation does not exist
          |
          v
    Merriam-Webster API
          |
          v
       Parse data
          |
          v
       Save MySQL
          |
          v
        Return

Do NOT call Merriam-Webster when pronunciation is already
available in MySQL.

---

## Error handling

Handle:

- word not found
- HTTP errors
- timeout
- network errors
- empty response
- missing pronunciation
- missing audio
- malformed response

A Merriam-Webster failure must not crash the application.

If audio is unavailable, the frontend must use Browser SpeechSynthesis.

---

## Frontend

Do not change the existing pronunciation API contract.

The frontend should continue receiving:

{
  "vocabularyId": 123,
  "word": "apple",
  "pronunciations": [
    {
      "accent": "US",
      "ipa": "...",
      "audioUrl": "..."
    },
    {
      "accent": "UK",
      "ipa": "...",
      "audioUrl": "..."
    }
  ]
}

If audioUrl is unavailable:

US -> browser speech with en-US
UK -> browser speech with en-GB

---

## Testing

Add/update tests for:

1. Merriam-Webster successful response
2. word not found
3. missing pronunciation
4. missing audio
5. timeout
6. HTTP error
7. cached pronunciation
8. API called only when cache is missing

Do not remove existing pronunciation tests unless they are
specifically tied to api.dictionaryapi.dev.

---

## Acceptance checklist

- DictionaryApiService uses Merriam-Webster instead of
  api.dictionaryapi.dev
- vocabulary_pronunciations table remains unchanged
- VocabularyPronunciation entity remains unchanged
- VocabularyPronunciationService API remains unchanged
- VocabularyPronunciationController API remains unchanged
- Unsplash code remains unchanged
- Browser SpeechSynthesis remains the fallback audio mechanism
- Existing pronunciation frontend contract remains unchanged