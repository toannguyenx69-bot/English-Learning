# English Learning

English Learning is a personal platform for learning and practicing English vocabulary and grammar. The project is intentionally built in stages so each feature can be developed, tested, and refined independently.

## Project goals

The application is designed to support:

- vocabulary learning
- vocabulary with images
- vocabulary with short videos
- grammar practice
- voice-to-text input
- voice translation
- pronunciation analysis
- grammar analysis
- personalized learning
- an AI English tutor experience

## Architecture

The system follows a layered backend design:

- Controller
- Service
- Repository
- Database

This keeps the application easier to reason about and makes feature development incremental.

## Technology stack

### Backend

- Java 17
- Spring Boot 3.2.0
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- MySQL

### Frontend

- React 18
- TypeScript
- Vite
- Axios

### Testing

- JUnit
- Mockito
- Spring Boot Test

## Repository layout

```text
backend/
  src/main/java/      Java backend source code
  src/main/resources/ application configuration and data files
  src/test/java/      backend tests
frontend/
  src/                React frontend source code
  package.json        frontend dependencies and scripts
```

## Local development setup

### 1. Backend

From the project root:

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend runs by default with an embedded H2 configuration unless MySQL environment variables are provided.

### 2. Frontend

From the project root:

```bash
cd frontend
npm install
npm run dev
```

The frontend is expected to run separately from the backend and use the backend API base URL configured in the app.

## Environment configuration

The backend can be configured with MySQL using environment variables. Typical values are:

```bash
DB_URL=jdbc:mysql://localhost:3306/english_learning
DB_DRIVER=com.mysql.cj.jdbc.Driver
DB_USER=root
DB_PASSWORD=your_password
```

If these variables are unset, the default configuration falls back to H2 for local development and testing.

## Main backend features

The backend currently includes:

- user authentication and registration
- vocabulary listing and retrieval
- vocabulary search and pagination
- grammar question retrieval
- grammar answer submission and feedback
- user progress tracking for vocabulary and grammar

## API conventions

The project follows a versioned API pattern under `/api/v1`.

Examples:

- `/api/v1/auth/...`
- `/api/v1/vocabulary/...`
- `/api/v1/grammar/...`
- `/api/v1/users/...`

## Data import notes

The project includes a vocabulary import script in the backend data resources. This is used to import Longman vocabulary content into the configured database and is intended for development data setup.

## Development principles

This project follows the same engineering principles documented in the project instructions:

- implement one feature at a time
- keep the architecture simple and maintainable
- prefer clear domain boundaries
- avoid unnecessary frameworks
- do not change unrelated code
- write tests for important business logic
- keep documentation aligned with the actual behavior

## Running the app

Start the backend first, then the frontend:

```bash
cd backend
mvn spring-boot:run
```

```bash
cd frontend
npm run dev
```

The app is then available in the browser through the Vite local development server while the API is served by Spring Boot.

## Notes

This project is being developed incrementally. The documentation reflects the current project state and should be updated as new features are added.
