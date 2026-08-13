# English Learning Project — Copilot Instructions

## 1. Project Overview

This project is a personal English Learning Platform designed for learning and practicing English.

The long-term goal is to provide:

- Vocabulary learning
- Vocabulary with images
- Vocabulary with short videos
- Grammar practice
- Voice-to-text
- Voice translation
- Pronunciation analysis
- Grammar analysis
- Personalized learning
- AI English Tutor

The project will be developed incrementally.

Do NOT implement the entire system at once.

---

# 2. Development Philosophy

The primary goal is not only to build the application, but also to learn software engineering, backend development, frontend development, distributed systems, and AI engineering.

Therefore:

1. Implement one feature at a time.
2. Explain important design decisions before implementing complex features.
3. Prefer simple and maintainable solutions.
4. Do not introduce unnecessary frameworks or libraries.
5. Do not modify unrelated files.
6. Reuse existing project patterns.
7. Write tests for important business logic.
8. Keep components small and focused.
9. Avoid premature optimization.
10. Do not implement future phases unless explicitly requested.

When a requirement is ambiguous, explain the ambiguity and propose a reasonable solution before making major changes.

---

# 3. Technology Stack

## Backend

- Java 21
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- MySQL

Future technologies:

- MongoDB
- Apache Kafka
- Docker
- AI / Speech-to-Text services
- Translation services

## Frontend

- React
- TypeScript
- Vite
- Axios

## Testing

Backend:

- JUnit
- Mockito
- Spring Boot Test

Frontend:

- React Testing Library when appropriate

End-to-end:

- Playwright

---

# 4. Backend Architecture

Use a layered architecture.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database