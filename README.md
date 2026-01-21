# Flashcard App Service

A Spring Boot backend service for a flashcard learning application.  
This service provides RESTful APIs for user authentication, flashcard set management,  
and spaced repetition learning features.

## Overview

The Flashcard App Service is a comprehensive backend solution designed to support interactive  
learning through digital flashcards. It implements user authentication, flashcard organization,  
progress tracking, and automated email notifications for daily practice reminders.

## Key Features

- **User Authentication & Authorization**: JWT-based authentication with secure password handling
- **Flashcard Management**: Create, read, update, and delete flashcard sets and cards
- **Spaced Repetition**: Track and manage last-seen flashcards for optimal learning intervals
- **User Profiles**: Manage user information and email verification
- **Daily Email Notifications**: Automated email service for user engagement and reminders
- **API Documentation**: Built-in Swagger/OpenAPI documentation

## Technology Stack

- **Framework**: Spring Boot 3.4.4
- **Language**: Java 21
- **Database**: PostgreSQL
- **Database Migrations**: Liquibase
- **Security**: Spring Security + JWT (JJWT)
- **API Documentation**: Springdoc OpenAPI
- **Email Service**: Spring Mail + Resend Java SDK
- **Testing**: JUnit 5 + Mockito
- **Build Tool**: Gradle

## Project Structure

```
src/
├── main/java/com/sekhanov/flashcard/
│   ├── config/              # Configuration classes
│   ├── controller/          # REST API endpoints
│   ├── dto/                 # Data Transfer Objects
│   ├── entity/              # JPA entities
│   ├── repository/          # Data access layer
│   ├── service/             # Business logic
│   └── utils/               # Utility classes
└── resources/
    ├── application.properties  # Configuration
    └── db/changelog/           # Liquibase migrations
```

## Getting Started

### Prerequisites

- Java 21 or higher
- PostgreSQL database
- Gradle wrapper (included)

### Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd flashcard-app-service
   ```

2. **Configure the database**
   Update `src/main/resources/application.properties` or `application.yml` with your PostgreSQL connection details:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/flashcard_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Configure email service** (if using Resend)
   ```properties
   resend.api.key=your_api_key
   ```

4. **Run database migrations**
   Liquibase migrations are automatically applied on application startup.

## Build and Test

### Build the application

```bash
./gradlew build
```

### Run the application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080` by default.

### Run tests

```bash
./gradlew test
```

### Generate JAR file

```bash
./gradlew bootJar
```

## API Endpoints

The service exposes the following main endpoints:

- **Authentication**: `/api/auth/*` - User login and registration
- **Users**: `/api/users/*` - User profile management
- **Flashcard Sets**: `/api/flashcard-sets/*` - Manage flashcard collections
- **Cards**: `/api/cards/*` - Manage individual flashcards
- **Mail**: `/api/mail/*` - Email operations

### API Documentation

Swagger UI is available at: `http://localhost:8080/swagger-ui/index.html`

## Docker Support

The service includes Docker configuration for containerized deployment:

```bash
docker-compose up
```

See `Dockerfile` and `docker-compose.yml` for configuration details.

## Database Schema

The database includes the following main tables:
- `users` - User accounts and authentication
- `word_lists` / `flashcard_sets` - Collections of flashcards
- `words` / `cards` - Individual flashcard entries
- `user_word_list` - Associations between users and flashcard sets
- `last_seen_flashcard_set` - Spaced repetition tracking

Migrations are managed via Liquibase in `src/main/resources/db/changelog/`.

## Support

For issues or questions, please open an issue in the repository.