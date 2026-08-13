# English Learning Project — Phase 2

## Goal

Extend the Phase 1 foundation into a usable English learning application.

Phase 2 focuses on:

- Secure authentication
- User profile
- Vocabulary management
- Grammar practice
- Learning progress
- React integration
- Backend and frontend testing

The goal is to make the core learning workflow functional before introducing MongoDB, Kafka, voice processing, translation, or AI.

---

# Phase 2 Architecture

```text
┌──────────────────────────────┐
│            React             │
│          TypeScript          │
│                              │
│ Login / Register             │
│ Vocabulary                   │
│ Grammar                      │
│ Dashboard                    │
└──────────────┬───────────────┘
               │ REST API / JWT
               ▼
┌──────────────────────────────┐
│         Spring Boot          │
│                              │
│ Controller → Service         │
│              ↓               │
│          Repository          │
└──────────────┬───────────────┘
               │ JPA / Hibernate
               ▼
┌──────────────────────────────┐
│            MySQL             │
│ users / vocabularies         │
│ progress / grammar           │
└──────────────────────────────┘
```

---

# Phase 2 Prerequisites

Phase 1 must be completed before starting Phase 2.

- [ ] Spring Boot starts successfully
- [ ] React starts successfully
- [ ] MySQL connection works
- [ ] React can communicate with Spring Boot
- [ ] `/api/v1/health` works
- [ ] Project is committed to Git

Do not start Phase 2 if the Phase 1 foundation is unstable.

---

# Milestone 1 — User Domain

## Goal

Create the user domain model and basic user management.

## User Entity

Initial fields:

```text
id
username
email
password
created_at
updated_at
```

## Tasks

- [ ] Create User entity
- [ ] Create UserRepository
- [ ] Create User DTOs
- [ ] Create UserService
- [ ] Create UserController
- [ ] Add validation
- [ ] Add timestamps
- [ ] Add unique constraint for email
- [ ] Add appropriate username constraint
- [ ] Add unit tests
- [ ] Add integration tests where useful

---

# Milestone 2 — Authentication

## Goal

Implement secure authentication using Spring Security and JWT.

## Technologies

- Spring Security
- JWT
- BCrypt password hashing

## Registration

```text
POST /api/v1/auth/register
```

Request:

```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "Password123!"
}
```

Never return the password.

## Login

```text
POST /api/v1/auth/login
```

Request:

```json
{
  "email": "john@example.com",
  "password": "Password123!"
}
```

Response:

```json
{
  "accessToken": "...",
  "tokenType": "Bearer"
}
```

## Tasks

- [ ] Add Spring Security
- [ ] Add JWT dependency
- [ ] Implement password hashing
- [ ] Implement registration
- [ ] Implement login
- [ ] Implement JWT generation
- [ ] Implement JWT validation
- [ ] Implement authentication filter
- [ ] Protect authenticated endpoints
- [ ] Configure public endpoints
- [ ] Configure CORS
- [ ] Handle authentication errors
- [ ] Add unit tests
- [ ] Add integration tests

Public endpoints:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
GET  /api/v1/health
```

Other learning APIs should require authentication.

---

# Milestone 3 — User Profile

## Goal

Allow authenticated users to view and update their profile.

## APIs

```text
GET /api/v1/users/me
PUT /api/v1/users/me
```

## Tasks

- [ ] Create authenticated user endpoint
- [ ] Return current user's profile
- [ ] Update profile information
- [ ] Validate input
- [ ] Prevent unauthorized access
- [ ] Add tests

Do not allow normal profile APIs to modify sensitive authentication fields.

---

# Milestone 4 — Vocabulary Management

## Goal

Create the first complete vocabulary management workflow.

## Initial Vocabulary Model

```text
id
word
meaning
pronunciation
part_of_speech
example
difficulty
created_at
updated_at
```

Example:

```json
{
  "word": "run",
  "meaning": "chạy",
  "pronunciation": "/rʌn/",
  "partOfSpeech": "verb",
  "example": "I run every morning.",
  "difficulty": "A1"
}
```

## Tasks

- [ ] Create Vocabulary entity
- [ ] Create VocabularyRepository
- [ ] Create Vocabulary DTOs
- [ ] Create VocabularyService
- [ ] Create VocabularyController
- [ ] Implement create
- [ ] Implement read
- [ ] Implement update
- [ ] Implement delete
- [ ] Implement search
- [ ] Implement pagination
- [ ] Add validation
- [ ] Add unit tests
- [ ] Add integration tests

## APIs

```text
GET    /api/v1/vocabularies
GET    /api/v1/vocabularies/{id}
POST   /api/v1/vocabularies
PUT    /api/v1/vocabularies/{id}
DELETE /api/v1/vocabularies/{id}
```

---

# Milestone 5 — Vocabulary Learning Progress

## Goal

Allow authenticated users to track vocabulary they have learned.

## UserVocabulary

Initial fields:

```text
id
user_id
vocabulary_id
learned
learned_at
created_at
updated_at
```

## Tasks

- [ ] Create UserVocabulary entity
- [ ] Create repository
- [ ] Create DTOs
- [ ] Create service
- [ ] Create controller
- [ ] Mark vocabulary as learned
- [ ] Mark vocabulary as not learned
- [ ] Get user's learned vocabulary
- [ ] Get user's vocabulary progress
- [ ] Prevent duplicate user/vocabulary relationships
- [ ] Add tests

## APIs

```text
POST   /api/v1/vocabularies/{id}/learn
DELETE /api/v1/vocabularies/{id}/learn
GET    /api/v1/users/me/vocabularies
GET    /api/v1/users/me/vocabularies/progress
```

The backend should identify the user from the authenticated JWT instead of accepting an arbitrary user ID from the client.

---

# Milestone 6 — Grammar Content

## Goal

Create grammar topics and questions.

## Grammar Question

Initial fields:

```text
id
question
explanation
difficulty
topic
created_at
updated_at
```

## Grammar Answer

Initial fields:

```text
id
question_id
answer
is_correct
```

## Tasks

- [ ] Create GrammarQuestion entity
- [ ] Create GrammarAnswer entity
- [ ] Create repositories
- [ ] Create DTOs
- [ ] Create services
- [ ] Create controllers
- [ ] Add validation
- [ ] Add tests

---

# Milestone 7 — Grammar Practice

## Goal

Allow users to answer grammar questions and receive feedback.

## APIs

```text
GET  /api/v1/grammar/questions
GET  /api/v1/grammar/questions/{id}
POST /api/v1/grammar/questions/{id}/answer
```

Answer request:

```json
{
  "answerId": 3
}
```

Response:

```json
{
  "correct": true,
  "correctAnswer": "went",
  "explanation": ""Yesterday" indicates the past tense."
}
```

## Tasks

- [ ] Get grammar questions
- [ ] Get question details
- [ ] Submit answer
- [ ] Determine correctness
- [ ] Return explanation
- [ ] Handle invalid question ID
- [ ] Handle invalid answer ID
- [ ] Add tests

Do not expose the correct answer before the user submits an answer.

---

# Milestone 8 — Grammar Progress

## Goal

Track user grammar practice results.

Potential data:

```text
user_id
question_id
selected_answer_id
correct
attempted_at
```

## Tasks

- [ ] Create grammar attempt entity
- [ ] Create repository
- [ ] Create service
- [ ] Save attempts
- [ ] Calculate score
- [ ] Get user grammar statistics
- [ ] Add tests

## APIs

```text
GET /api/v1/users/me/grammar/progress
GET /api/v1/users/me/grammar/statistics
```

---

# Milestone 9 — React Authentication

## Goal

Connect React authentication to the backend.

## Pages

```text
/login
/register
```

## Tasks

- [ ] Create Login page
- [ ] Create Register page
- [ ] Create authentication service
- [ ] Connect registration API
- [ ] Connect login API
- [ ] Implement authentication state
- [ ] Add protected routes
- [ ] Handle expired/invalid authentication
- [ ] Add logout
- [ ] Add form validation
- [ ] Add loading state
- [ ] Add error state

---

# Milestone 10 — React Vocabulary

## Goal

Build the vocabulary learning interface.

## Pages

```text
/vocabulary
/vocabulary/:id
```

## Features

- [ ] Vocabulary list
- [ ] Search
- [ ] Pagination
- [ ] Vocabulary detail
- [ ] Display pronunciation
- [ ] Display example
- [ ] Display difficulty
- [ ] Mark as learned
- [ ] Show learned status
- [ ] Loading state
- [ ] Error state
- [ ] Empty state

---

# Milestone 11 — React Grammar

## Goal

Build the grammar practice interface.

## Page

```text
/grammar
```

## Features

- [ ] Display grammar question
- [ ] Display answer choices
- [ ] Submit answer
- [ ] Show correct/incorrect result
- [ ] Show explanation
- [ ] Track score
- [ ] Move to next question
- [ ] Loading state
- [ ] Error state

---

# Milestone 12 — Dashboard

## Goal

Create a simple personalized learning dashboard.

Display:

```text
Vocabulary learned
Grammar questions attempted
Grammar accuracy
Learning progress
```

## Tasks

- [ ] Create Dashboard page
- [ ] Create dashboard service
- [ ] Display vocabulary statistics
- [ ] Display grammar statistics
- [ ] Display overall progress
- [ ] Handle loading state
- [ ] Handle errors
- [ ] Create responsive layout

---

# Milestone 13 — Backend Testing

## Goal

Improve confidence in the backend.

## Unit Tests

Cover:

- [ ] Authentication service
- [ ] User service
- [ ] Vocabulary service
- [ ] Learning progress service
- [ ] Grammar service
- [ ] Grammar progress service

Test:

```text
Happy path
Validation failure
Not found
Duplicate data
Unauthorized access
Business rule violations
```

## Integration Tests

Add integration tests for important APIs:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
GET  /api/v1/vocabularies
POST /api/v1/vocabularies/{id}/learn
POST /api/v1/grammar/questions/{id}/answer
```

---

# Milestone 14 — Frontend Testing

## Goal

Test important React behavior.

Add tests for:

- [ ] Login form
- [ ] Registration form
- [ ] Vocabulary list
- [ ] Vocabulary search
- [ ] Mark vocabulary as learned
- [ ] Grammar question
- [ ] Grammar answer
- [ ] Dashboard

Test user behavior rather than implementation details.

---

# Milestone 15 — API and Error Handling Review

Review:

- [ ] REST API naming
- [ ] HTTP status codes
- [ ] DTO consistency
- [ ] Error response format
- [ ] Validation
- [ ] Authentication errors
- [ ] Authorization
- [ ] CORS
- [ ] Logging
- [ ] Exception handling
- [ ] Database constraints
- [ ] Pagination

Use a consistent error format:

```json
{
  "timestamp": "2026-08-11T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email is invalid",
  "path": "/api/v1/auth/register"
}
```

---

# Milestone 16 — Security Review

Before finishing Phase 2:

- [ ] Passwords are hashed
- [ ] Passwords are never returned
- [ ] JWT secret is not hard-coded
- [ ] Database credentials are not hard-coded
- [ ] Protected APIs require authentication
- [ ] Users cannot access another user's learning progress
- [ ] Users cannot modify another user's profile
- [ ] Sensitive information is not logged
- [ ] CORS is restricted appropriately
- [ ] Input validation is enabled

---

# Phase 2 Final Definition of Done

## Backend

- [ ] User management works
- [ ] Registration works
- [ ] Login works
- [ ] JWT authentication works
- [ ] Protected APIs work
- [ ] User profile works
- [ ] Vocabulary CRUD works
- [ ] Vocabulary search works
- [ ] Vocabulary pagination works
- [ ] Vocabulary learning progress works
- [ ] Grammar questions work
- [ ] Grammar answer submission works
- [ ] Grammar progress works
- [ ] Dashboard APIs work
- [ ] Important backend services have tests
- [ ] Important APIs have integration tests

## Frontend

- [ ] Registration page works
- [ ] Login page works
- [ ] Logout works
- [ ] Protected routes work
- [ ] Vocabulary page works
- [ ] Vocabulary search works
- [ ] Vocabulary detail works
- [ ] Mark as learned works
- [ ] Grammar practice works
- [ ] Grammar feedback works
- [ ] Dashboard works
- [ ] Loading/error/empty states are handled

## Security

- [ ] Passwords are hashed
- [ ] JWT authentication is implemented
- [ ] Secrets are externalized
- [ ] Unauthorized access is rejected
- [ ] User data is isolated between users

## Quality

- [ ] Tests pass
- [ ] Backend builds successfully
- [ ] Frontend builds successfully
- [ ] No unrelated code changes remain
- [ ] README is updated
- [ ] Git history contains meaningful commits

---

# Phase 2 Restrictions

Do NOT implement these during Phase 2 unless explicitly requested:

- MongoDB
- Apache Kafka
- Speech-to-text
- Voice translation
- AI APIs
- AI English Tutor
- Advanced pronunciation analysis
- Image processing
- Video processing
- Docker
- Kubernetes
- Cloud deployment
- MLOps

These will be introduced in later phases.

---

# Recommended Development Order

```text
Phase 1 Foundation
       ↓
1. User Domain
       ↓
2. Authentication / JWT
       ↓
3. User Profile
       ↓
4. Vocabulary
       ↓
5. Vocabulary Progress
       ↓
6. Grammar Content
       ↓
7. Grammar Practice
       ↓
8. Grammar Progress
       ↓
9. React Authentication
       ↓
10. React Vocabulary
       ↓
11. React Grammar
       ↓
12. Dashboard
       ↓
13. Backend Testing
       ↓
14. Frontend Testing
       ↓
15. API / Error Review
       ↓
16. Security Review
       ↓
Phase 2 Complete
```

---

# Copilot Workflow

For every milestone:

1. Read `.github/copilot-instructions.md`.
2. Read this `phase-2.md`.
3. Inspect the existing implementation.
4. Check which tasks are already completed.
5. Identify files that need to change.
6. Explain the implementation approach for complex tasks.
7. Implement only the requested milestone/task.
8. Reuse existing architecture and conventions.
9. Add or update tests.
10. Run the relevant tests.
11. Check compilation/build errors.
12. Review security implications.
13. Do not modify unrelated files.
14. Summarize the changes.
15. Wait for the next instruction.

Do not automatically implement future milestones.

---

# Suggested Copilot Prompt Pattern

Use this when starting a milestone:

```text
Read:
- .github/copilot-instructions.md
- .github/prompts/phase-2.md

We are currently working on Phase 2, Milestone X.

First inspect the existing implementation.

Do not modify files yet.

Tell me:
1. What is already implemented?
2. What is missing for this milestone?
3. Which files need to be created or modified?
4. What is your implementation approach?
5. Are there any design or security concerns?

Wait for my confirmation before implementing.
```

After reviewing the plan:

```text
Implement the agreed changes for Phase 2, Milestone X.

Requirements:
- Follow .github/copilot-instructions.md.
- Modify only relevant files.
- Follow the existing architecture.
- Add appropriate tests.
- Do not implement future milestones.

After implementation:
1. Run relevant tests.
2. Check for compilation/build errors.
3. Summarize the changes.
4. Explain anything I should review manually.
```

---

# Learning Objectives

Phase 2 should help the developer understand:

## Spring Boot

- Dependency injection
- REST controllers
- Services
- Repositories
- DTOs
- Validation
- Exception handling
- Spring Security
- JWT
- Transactions

## Database

- JPA entities
- Relationships
- Foreign keys
- Constraints
- Indexes
- Pagination
- Transactions

## React

- Components
- Props
- State
- Hooks
- Forms
- API calls
- Authentication state
- Protected routes
- Error/loading states

## Software Engineering

- Layered architecture
- REST API design
- Authentication vs authorization
- Unit testing
- Integration testing
- Git workflow
- Security fundamentals

The goal is to understand these concepts while building the application, not simply to accept AI-generated code.
