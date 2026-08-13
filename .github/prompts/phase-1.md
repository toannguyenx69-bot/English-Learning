# English Learning Project — Phase 1

## Goal

Build the foundation of the English Learning Platform.

### Technology

Backend:
- Java 21
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- MySQL
- Jakarta Validation

Frontend:
- React
- TypeScript
- Vite
- Axios

Testing:
- JUnit
- Mockito
- Spring Boot Test
- React Testing Library when appropriate

---

# Phase 1 Architecture

```text
┌──────────────┐
│    React     │
│  TypeScript  │
│    :5173     │
└──────┬───────┘
       │
       │ REST API / HTTP
       ▼
┌──────────────┐
│ Spring Boot  │
│    :8080     │
│  Embedded    │
│   Tomcat     │
└──────┬───────┘
       │
       │ JPA / Hibernate
       ▼
┌──────────────┐
│    MySQL     │
│    :3306     │
└──────────────┘
```

Phase 1 does not include MongoDB, Kafka, voice processing, translation, or AI.

---

# Milestone 1 — Project Setup

## Goal

Create a working development environment with:

- Spring Boot backend
- React frontend
- MySQL database
- React → Spring Boot communication
- Basic health check
- Basic tests

## Backend Setup

- [ ] Create Spring Boot project inside `backend/`
- [ ] Configure Java 21
- [ ] Configure Maven
- [ ] Add Spring Web
- [ ] Add Spring Data JPA
- [ ] Add MySQL Driver
- [ ] Add Jakarta Validation
- [ ] Use embedded Tomcat
- [ ] Use `application.yml`
- [ ] Configure MySQL using environment variables
- [ ] Verify Spring Boot starts successfully

## MySQL Setup

- [ ] Create `english_learning` database
- [ ] Configure database connection
- [ ] Verify Spring Boot can connect to MySQL
- [ ] Do not create application entities yet

## Backend Health Check

Create:

```text
GET /api/v1/health
```

Expected response:

```json
{
  "status": "UP"
}
```

- [ ] Create health endpoint
- [ ] Add controller test
- [ ] Verify endpoint manually

## Frontend Setup

- [ ] Create React project inside `frontend/`
- [ ] Use TypeScript
- [ ] Use Vite
- [ ] Install dependencies
- [ ] Verify React application starts successfully

## React → Spring Boot

- [ ] Configure Axios
- [ ] Create API service
- [ ] Call `GET /api/v1/health`
- [ ] Display backend status in React
- [ ] Handle loading state
- [ ] Handle success state
- [ ] Handle error state
- [ ] Configure CORS in Spring Boot
- [ ] Verify end-to-end communication

## Milestone 1 Definition of Done

- [ ] MySQL is running
- [ ] Spring Boot starts successfully
- [ ] Embedded Tomcat starts successfully
- [ ] React starts successfully
- [ ] React can call Spring Boot
- [ ] `/api/v1/health` returns `UP`
- [ ] Spring Boot can connect to MySQL
- [ ] No hard-coded database credentials
- [ ] Basic tests pass
- [ ] No unrelated features have been implemented
- [ ] Changes are committed to Git

---

# Milestone 2 — Database Foundation

## Goal

Create the initial relational database model.

Initial tables:

```text
users
vocabularies
user_vocabularies
grammar_questions
grammar_answers
```

## Tasks

- [ ] Design database schema
- [ ] Review schema before implementation
- [ ] Create JPA entities
- [ ] Create entity relationships
- [ ] Create repositories
- [ ] Add appropriate indexes
- [ ] Add unique constraints where needed
- [ ] Verify schema generation/migration strategy
- [ ] Add repository tests where useful

Do not implement authentication yet unless explicitly requested.

---

# Milestone 3 — User

## Goal

Implement basic user management.

## Tasks

- [ ] User entity
- [ ] User DTOs
- [ ] User repository
- [ ] User service
- [ ] User controller
- [ ] Registration API
- [ ] Login API
- [ ] Validation
- [ ] Duplicate email handling
- [ ] Global exception handling
- [ ] Unit tests

Example APIs:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
```

Security requirements:

- Never store plaintext passwords.
- Use secure password hashing.
- Do not hard-code secrets.

---

# Milestone 4 — Vocabulary

## Goal

Implement vocabulary management.

## Initial vocabulary model

```text
word
meaning
pronunciation
part_of_speech
example
difficulty
created_at
updated_at
```

Multimedia such as images and videos will be added later.

## Tasks

- [ ] Vocabulary entity
- [ ] Vocabulary DTOs
- [ ] Vocabulary repository
- [ ] Vocabulary service
- [ ] Vocabulary controller
- [ ] Create vocabulary
- [ ] Get vocabulary by ID
- [ ] Get vocabulary list
- [ ] Search vocabulary
- [ ] Update vocabulary
- [ ] Delete vocabulary
- [ ] Validation
- [ ] Unit tests

Example APIs:

```text
GET    /api/v1/vocabularies
GET    /api/v1/vocabularies/{id}
POST   /api/v1/vocabularies
PUT    /api/v1/vocabularies/{id}
DELETE /api/v1/vocabularies/{id}
```

---

# Milestone 5 — Learning Progress

## Goal

Allow users to track vocabulary learning progress.

## Tasks

- [ ] UserVocabulary entity
- [ ] Repository
- [ ] Service
- [ ] Controller
- [ ] Mark vocabulary as learned
- [ ] Get user's learned vocabulary
- [ ] Track learning timestamp
- [ ] Prevent duplicate progress records
- [ ] Add unit tests

Example APIs:

```text
POST /api/v1/users/{userId}/vocabularies/{vocabularyId}/learn
GET  /api/v1/users/{userId}/vocabularies
```

---

# Milestone 6 — Grammar

## Goal

Implement basic grammar practice.

## Grammar functionality

Support:

- Grammar topics
- Questions
- Multiple-choice answers
- Correct answers
- Explanations
- User results

Example:

```text
Question:
She ___ to school yesterday.

A. go
B. goes
C. went
D. going

Correct:
C

Explanation:
"Yesterday" indicates the past tense.
```

## Tasks

- [ ] Grammar question entity
- [ ] Grammar answer entity
- [ ] Repository
- [ ] Service
- [ ] Controller
- [ ] Get grammar questions
- [ ] Submit answer
- [ ] Return correct/incorrect result
- [ ] Return explanation
- [ ] Add unit tests

Example APIs:

```text
GET  /api/v1/grammar/questions
POST /api/v1/grammar/questions/{id}/answer
```

---

# Milestone 7 — React Authentication

## Goal

Connect the authentication UI to the backend.

## Tasks

- [ ] Login page
- [ ] Registration page
- [ ] Authentication service
- [ ] API integration
- [ ] Form validation
- [ ] Error handling
- [ ] Basic navigation
- [ ] Loading states

---

# Milestone 8 — React Vocabulary

## Goal

Create the vocabulary learning UI.

## Tasks

- [ ] Vocabulary page
- [ ] Vocabulary list
- [ ] Search
- [ ] Vocabulary detail
- [ ] Mark as learned
- [ ] Loading state
- [ ] Error state
- [ ] Empty state

---

# Milestone 9 — React Grammar

## Goal

Create the grammar practice UI.

## Tasks

- [ ] Grammar page
- [ ] Display question
- [ ] Display answers
- [ ] Submit answer
- [ ] Show correct/incorrect result
- [ ] Show explanation
- [ ] Track score

---

# Milestone 10 — Dashboard

## Goal

Create a simple learning dashboard.

Display:

```text
Vocabulary learned
Grammar questions completed
Grammar score
Learning progress
```

## Tasks

- [ ] Dashboard page
- [ ] Progress API
- [ ] Vocabulary statistics
- [ ] Grammar statistics
- [ ] Basic progress visualization

---

# Phase 1 Final Definition of Done

Phase 1 is complete when:

- [ ] Backend starts successfully
- [ ] Frontend starts successfully
- [ ] React communicates with Spring Boot
- [ ] Spring Boot communicates with MySQL
- [ ] User registration works
- [ ] User login works
- [ ] Vocabulary CRUD works
- [ ] Vocabulary learning progress works
- [ ] Grammar practice works
- [ ] React UI supports the main features
- [ ] Important backend services have tests
- [ ] No hard-coded secrets exist
- [ ] README contains setup instructions
- [ ] Code is committed to Git

---

# Phase 1 Restrictions

Do NOT implement these during Phase 1 unless explicitly requested:

- MongoDB
- Apache Kafka
- Speech-to-text
- Voice translation
- AI APIs
- AI English Tutor
- Advanced pronunciation analysis
- Docker
- Kubernetes
- Cloud deployment
- MLOps

These will be introduced in later phases.

---

# Recommended Development Order

```text
1. Project setup
       ↓
2. MySQL configuration
       ↓
3. Health check
       ↓
4. React setup
       ↓
5. React → Spring Boot
       ↓
6. Database foundation
       ↓
7. User
       ↓
8. Vocabulary
       ↓
9. Learning Progress
       ↓
10. Grammar
       ↓
11. React Authentication
       ↓
12. React Vocabulary
       ↓
13. React Grammar
       ↓
14. Dashboard
       ↓
15. Tests / cleanup
```

---

# Copilot Workflow

For every milestone:

1. Read `.github/copilot-instructions.md`.
2. Inspect the existing project.
3. Identify the files that need to change.
4. Explain the implementation approach when the task is complex.
5. Implement only the requested milestone or task.
6. Add or update tests.
7. Check compilation/build errors.
8. Check for obvious bugs.
9. Do not modify unrelated files.
10. Summarize the changes.
11. Wait for the next instruction.

Do not automatically implement future milestones.

---

# Learning Rule

This is also a learning project.

When implementing an important concept, explain:

- Why it is needed
- What problem it solves
- Why this design was chosen
- What alternatives exist
- Important trade-offs

For simple changes, keep the explanation short.

The goal is to understand the system rather than blindly accept AI-generated code.
