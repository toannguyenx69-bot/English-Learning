# English Learning Project — Phase 3

## 1. Phase Overview

Phase 3 introduces multimedia vocabulary and MongoDB.

The main goal is to extend the existing English Learning application with:

- MongoDB
- Vocabulary multimedia metadata
- Images
- Short videos
- Pronunciation/audio metadata
- Rich vocabulary details
- Advanced vocabulary search/filtering
- React multimedia learning UI

Phase 3 builds on Phase 1 and Phase 2.

Do not rewrite the existing architecture unless there is a clear technical reason.

---

# 2. Prerequisites

Before starting Phase 3, verify that Phase 1 and Phase 2 are complete.

Required:

- Spring Boot backend works
- React frontend works
- MySQL works
- User registration works
- JWT authentication works
- Vocabulary management works
- Grammar practice works
- Learning progress works
- Dashboard works
- Backend tests pass
- Frontend builds successfully

If a prerequisite is not working, fix it before implementing Phase 3.

---

# 3. Phase 3 Architecture

The application will use two databases.

```text
                         React
                           |
                           | REST API
                           |
                           v
                  +-------------------+
                  |    Spring Boot    |
                  +---------+---------+
                            |
                +-----------+-----------+
                |                       |
                v                       v
           +---------+             +---------+
           |  MySQL  |             | MongoDB |
           +---------+             +---------+
                |                       |
                |                       |
        Structured data          Flexible content
                |                       |
        - Users                  - Images
        - Vocabulary             - Videos
        - Grammar                - Audio
        - Progress               - Media metadata