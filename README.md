# Task Manager Application

A full-stack task management application built with Spring Boot (backend) and Angular (frontend), with JWT authentication and CRUD operations.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)
- [Author](#author)
- [Acknowledgments](#acknowledgments)

## Features

- **User Authentication**
  - User registration with email validation
  - JWT-based authentication
  - Secure password hashing with BCrypt
  
- **Task Management**
  - Create, read, update, and delete tasks
  - Filter tasks by status (PENDING, COMPLETED)
  - User-specific task isolation
  
- **Security**
  - JWT token authentication
  - Protected API endpoints
  - User authorization for task operations
  
- **User Experience**
  - Responsive UI design
  - Real-time toast notifications
  - Loading states and error handling

## Tech Stack

### Backend
- Java 25
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- H2 Database (file-based)
- JWT (JSON Web Tokens)
- Maven
- Lombok
- MapStruct
- Swagger/OpenAPI 3.0

### Frontend
- Angular 20+
- TypeScript
- RxJS
- Angular Signals
- Standalone Components
- CSS3

## Prerequisites

- **Java Development Kit (JDK)**: 25 or higher
- **Maven**: 3.9+
- **Node.js**: 18+ and npm
- **Angular CLI**: 20+
- **IDE**: IntelliJ IDEA, VS Code, or Eclipse

## Installation

### Backend Setup

1. **Clone the repository**
```bash
   git clone https://github.com/MercyKorir/task-manager.git
   cd task-manager/store
```

2. **Configure the application**
   
   The application uses H2 in-memory database. Configuration is in `src/main/resources/application.properties`

3. **Build the project**
```bash
   mvn clean install
```

### Frontend Setup

1. **Navigate to frontend directory**
```bash
   cd task-manager/frontend
```

2. **Install dependencies**
```bash
   npm install
```

## Running the Application

### Backend

```bash
# Development mode
mvn spring-boot:run

# Or run the JAR file
java -jar target/store-0.0.1-SNAPSHOT.jar
```

Backend will start on: **http://localhost:8080**

### Frontend

```bash
# Development mode
ng serve

# Or
npm start
```

Frontend will start on: **http://localhost:4200**

### Run Both Simultaneously

Open two terminal windows and run both commands above.

## API Documentation

### Interactive API Documentation

Once the backend is running, access the Swagger UI:

**URL:** http://localhost:8080/swagger-ui.html

**OpenAPI JSON:** http://localhost:8080/api-docs

### API Endpoints

#### Authentication Endpoints (Public)

| Method | Endpoint         | Description                 |
|--------|------------------|-----------------------------|
| POST   | `/auth/register` | Register a new user         |
| POST   | `/auth/login`    | Login and receive JWT token |

#### Task Endpoints (Protected - Requires JWT)

| Method | Endpoint                    | Description |
|--------|-----------------------------|------------------------------------------|
| POST   | `/api/tasks`                | Create a new task                        |
| GET    | `/api/tasks`                | Get all tasks for the logged-in user     |
| GET    | `/api/tasks?status=PENDING` | Get tasks filtered by status             |
| GET    | `/api/tasks/{id}`           | Get a specific task by ID                |
| PUT    | `/api/tasks/{id}`           | Update a task (partial update supported) |
| DELETE | `/api/tasks/{id}`           | Delete a task                            |

### Authentication

Protected endpoints require JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Example API Requests

#### Register User

```bash
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login

```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

#### Create Task

```bash
POST http://localhost:8080/api/tasks
Authorization: Bearer <your-token>
Content-Type: application/json

{
  "title": "Complete project documentation",
  "description": "Write comprehensive README and API docs",
  "status": "PENDING"
}
```

#### Update Task (Partial)

```bash
PUT http://localhost:8080/api/tasks/1
Authorization: Bearer <your-token>
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

For complete API documentation, see [API.md](docs/API.md)

## Testing

### Run Backend Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthenticationServiceTest

# Run with coverage
mvn clean test jacoco:report
```

### Run Frontend Tests

```bash
# Run unit tests
ng test

# Run e2e tests
ng e2e
```

## Project Structure

### Backend Structure

```
store/
├── data/ # H2 DB
├── logs/ # Slf4j logs
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/store/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── ApplicationConfiguration.java
│   │   │   │   ├── SecurityConfiguration.java
│   │   │   │   ├── OpenApiConfiguration.java
│   │   │   │   └── CustomAuthenticationEntryPoint.java
│   │   │   ├── controllers/         # REST Controllers
│   │   │   │   ├── AuthenticationController.java
│   │   │   │   └── TaskController.java
│   │   │   ├── dtos/                # Data Transfer Objects
│   │   │   │   ├── LoginUserDto.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── RegisterUserDto.java
│   │   │   │   ├── TaskDto.java
│   │   │   │   └── UserDto.java
│   │   │   ├── entities/            # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Task.java
│   │   │   │   └── Status.java
│   │   │   ├── exceptions/          # Custom Exceptions
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── TaskNotFoundException.java
│   │   │   │   ├── UnauthorizedTaskAccessException.java
│   │   │   │   ├── UserNotFoundException.java
│   │   │   │   └── DuplicateUserException.java
│   │   │   ├── filters/             # Security Filters
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   ├── mappers/             # MapStruct Mappers
│   │   │   │   ├── TaskMapper.java
│   │   │   │   └── UserMapper.java
│   │   │   ├── repositories/        # JPA Repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── TaskRepository.java
│   │   │   └── services/            # Business Logic
│   │   │       ├── AuthenticationService.java
│   │   │       └── JwtService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                        # Test classes
│       └── java/com/taskmanager/store/
├── pom.xml
└── README.md
```

### Frontend Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/                    # Core module
│   │   │   ├── guards/              # Route guards
│   │   │   ├── interceptors/        # HTTP interceptors
│   │   │   ├── models/              # Data models
│   │   │   └── services/            # Services
│   │   ├── features/                # Feature modules
│   │   │   ├── auth/                # Authentication
│   │   │   │   ├── login/
│   │   │   │   └── register/
│   │   │   └── tasks/               # Task management
│   │   │       ├── task-list/
│   │   │       ├── task-item/
│   │   │       └── task-form/
│   │   └── shared/                  # Shared components
│   │       ├── navbar/
│   │       └── toast-container/
├── angular.json
├── package.json
└── README.md
```

## Architecture

### Backend Architecture

The backend follows a layered architecture:

1. **Controller Layer:** Handles HTTP requests and responses
2. **Service Layer:** Contains business logic
3. **Repository Layer:** Data access using Spring Data JPA
4. **Security Layer:** JWT authentication and authorization
5. **Exception Handling:** Global exception handler for consistent error responses

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         Client Layer                        │
│                    (Angular Frontend)                       │
└─────────────────────────┬───────────────────────────────────┘
                          │ HTTP/REST
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                     Controller Layer                        │
│          (AuthenticationController, TaskController)         │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                          │
│       (AuthenticationService, JwtService)      │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                    Repository Layer                         │
│              (UserRepository, TaskRepository)               │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ↓
┌─────────────────────────────────────────────────────────────┐
│                      Database Layer                         │
│                    (H2 File-based DB)                       │
└─────────────────────────────────────────────────────────────┘
```

### Security Flow

```
Client Request
    ↓
JWT Authentication Filter
    ↓
Spring Security Filter Chain
    ↓
Controller (if authenticated)
    ↓
Service Layer
    ↓
Repository Layer
    ↓
Database
```

### JWT Authentication Flow

```
1. User registers and logs in
   ↓
2. Server generates JWT token
   ↓
3. Client stores token (localStorage)
   ↓
4. Client sends token with each request (Authorization header)
   ↓
5. Server validates token
   ↓
6. Server processes request if valid
```

## Security

- **Password Security:** BCrypt hashing algorithm with salt
- **JWT Tokens:** Signed with HS256 algorithm
- **Token Expiration:** 1 hour (configurable in application.properties)
- **CORS Configuration:** Configured for localhost:4200
- **Authorization:** Users can only access their own tasks
- **Input Validation:** Server-side validation for inputs
- **SQL Injection Prevention:** Using JPA with parameterized queries

### Security Configuration

The application uses Spring Security with the following configuration:

- Public endpoints: `/auth/register`, `/auth/login`
- Protected endpoints: `/api/**` (requires JWT authentication)
- Stateless session management
- Custom authentication entry point for unauthorized access

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/Feature`)
3. Commit your changes (`git commit -m 'Add some Feature'`)
4. Push to the branch (`git push origin feature/Feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Use meaningful variable and method names
- Write unit tests for new features
- Update documentation as needed

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

**Mercy Chelangat**

- GitHub: [@MercyKorir](https://github.com/MercyKorir)

## Acknowledgments

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Documentation](https://angular.io/docs)
- [JWT.io](https://jwt.io/) - JWT debugging and resources
- [Baeldung](https://www.baeldung.com/) - Spring tutorials
- [MapStruct](https://mapstruct.org/) - Object mapping framework

---

## Support

If you encounter any issues or have questions:

1. Check the [API Documentation](docs/API.md)
2. Review existing [GitHub Issues](https://github.com/MercyKorir/task-manager/issues)
3. Create a new issue with detailed information

---

## Future Enhancements

- [ ] Task due dates and reminders
- [ ] Task priority levels
- [ ] Task categories/tags
- [ ] User profile management
- [ ] Task sharing between users
- [ ] Email notifications
- [ ] PostgreSQL/MySQL support
- [ ] Docker containerization
- [ ] CI/CD pipeline
- [ ] Unit test coverage improvement

---

**Note:** This is a technical assessment project demonstrating full-stack development skills with Spring Boot and Angular.