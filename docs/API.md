# Task Manager API Documentation

## Base URL
```
http://localhost:8080
```

## Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Response Format

### Success Response

```json
{
  "id": 1,
  "title": "Task title",
  "description": "Task description",
  "status": "PENDING",
  "userId": 1
}
```

### Error Response

```json
{
  "type": "about:blank",
  "title": "Error Title",
  "status": 400,
  "detail": "Detailed error message",
  "instance": "/api/endpoint",
  "description": "Human-readable description"
}
```

## Status Codes

| Code | Description                      |
|------|----------------------------------|
| 200  | Success                          |
| 201  | Created                          |
| 204  | No Content (successful deletion) |
| 400  | Bad Request                      |
| 401  | Unauthorized                     |
| 403  | Forbidden                        |
| 404  | Not Found                        |
| 409  | Conflict (duplicate resource)    |
| 500  | Internal Server Error            |

## Endpoints

### Authentication

#### Register User

```http
POST /auth/register
```

**Request Body:**

```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Success Response (200):**

```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "createdAt": "2025-10-11T10:30:00",
  "updatedAt": "2025-10-11T10:30:00"
}
```

**Error Response (409):**

```json
{
    "type": "about:blank",
    "title": "Conflict",
    "status": 409,
    "detail": "User with email 'john@example.com' already exists",
    "instance": "/auth/register",
    "description": "A user with this email/username already exists"
}
```

---

#### Login

```http
POST /auth/login
```

**Request Body:**

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Success Response (200):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}
```

**Error Response (401):**

```json
{
    "type": "about:blank",
    "title": "Unauthorized",
    "status": 401,
    "detail": "Bad credentials",
    "instance": "/auth/login",
    "description": "The username or password is incorrect"
}
```

---

### Tasks

#### Create Task

```http
POST /api/tasks
Authorization: Bearer <token>
```

**Request Body:**

```json
{
  "title": "Complete documentation",
  "description": "Write API documentation",
  "status": "PENDING"
}
```

**Validation Rules:**
- `title`: Required, 1-100 characters
- `description`: Required max 500 characters
- `status`: Optional, must be one of: PENDING, COMPLETED (defaults to PENDING)

**Success Response (201):**

```json
{
  "id": 1,
  "title": "Complete documentation",
  "description": "Write API documentation",
  "status": "PENDING",
  "userId": 1
}
```

**Error Response (400):**

```json
{
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "Validation failed",
    "instance": "/api/tasks",
    "description": "title:Title is required and cannot be empty"
}
```

---

#### Get All Tasks

```http
GET /api/tasks
Authorization: Bearer <token>
```

**Query Parameters:**

| Parameter | Type   | Required | Description                                        |
|-----------|--------|----------|----------------------------------------------------|
| `status`  | String | No       | Filter by status (PENDING, COMPLETED) |

**Examples:**

```http
GET /api/tasks
GET /api/tasks?status=PENDING
GET /api/tasks?status=COMPLETED
```

**Success Response (200):**

```json
[
  {
    "id": 1,
    "title": "Task 1",
    "description": "Description 1",
    "status": "PENDING",
    "userId": 1
  },
  {
    "id": 2,
    "title": "Task 2",
    "description": "Description 2",
    "status": "COMPLETED",
    "userId": 1
  }
]
```

**Empty Response (200):**

```json
[]
```

---

#### Get Task by ID

```http
GET /api/tasks/{id}
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id`      | Long | Yes      | Task ID     |

**Success Response (200):**

```json
{
  "id": 1,
  "title": "Task title",
  "description": "Task description",
  "status": "PENDING",
  "userId": 1
}
```

**Error Response (404):**

```json
{
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Task not found with id: 1",
    "instance": "/api/tasks/1",
    "description": "The requested task does not exist"
}
```

**Error Response (403):**

```json
{
    "type": "about:blank",
    "title": "Forbidden",
    "status": 403,
    "detail": "Unauthorized access to task with id: 33",
    "instance": "/api/tasks/33",
    "description": "You do not have permission to access this task"
}
```

---

#### Update Task

```http
PUT /api/tasks/{id}
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id`      | Long | Yes      | Task ID     |

**Request Body (Partial Update Supported):**

Update only status:
```json
{
  "status": "COMPLETED"
}
```

Update only title and description:
```json
{
  "title": "Updated title",
  "description": "Updated description"
}
```

Full update:
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "status": "COMPLETED"
}
```

**Validation Rules:**
- `title`: Optional, 1-100 characters if provided
- `description`: Optional, max 500 characters if provided
- `status`: Optional, must be one of: PENDING, COMPLETED

**Success Response (200):**

```json
{
  "id": 1,
  "title": "Updated title",
  "description": "Updated description",
  "status": "COMPLETED",
  "userId": 1
}
```

**Error Response (404):**

```json
{
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Task not found with id: 1",
    "instance": "/api/tasks/1",
    "description": "The requested task does not exist"
}
```

**Error Response (403):**

```json
{
    "type": "about:blank",
    "title": "Forbidden",
    "status": 403,
    "detail": "Unauthorized access to task with id: 33",
    "instance": "/api/tasks/33",
    "description": "You do not have permission to access this task"
}
```

---

#### Delete Task

```http
DELETE /api/tasks/{id}
Authorization: Bearer <token>
```

**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id`      | Long | Yes      | Task ID     |

**Success Response (204 No Content):**

No response body

**Error Response (404):**

```json
{
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Task not found with id: 1",
    "instance": "/api/tasks/1",
    "description": "The requested task does not exist"
}
```

**Error Response (403):**

```json
{
    "type": "about:blank",
    "title": "Forbidden",
    "status": 403,
    "detail": "Unauthorized access to task with id: 33",
    "instance": "/api/tasks/33",
    "description": "You do not have permission to access this task"
}
```

---

## Task Status Values

| Status        | Description                       |
|---------------|-----------------------------------|
| `PENDING`     | Task is waiting to be started     |
| `COMPLETED`   | Task has been finished            |

---

## Common Error Scenarios

### Invalid Token

**Request:**
```http
GET /api/tasks
Authorization: Bearer invalid_token
```

**Response (403):**
```json
{
    "type": "about:blank",
    "title": "Forbidden",
    "status": 403,
    "detail": "JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.",
    "instance": "/api/tasks",
    "description": "The JWT signature is invalid"
}
```

---

### Missing Token

**Request:**
```http
GET /api/tasks
```

**Response (403):**
```json
{
    "type": "about:blank",
    "title": "Forbidden",
    "status": 403,
    "detail": "Full authentication is required to access this resource",
    "instance": null,
    "properties": {
        "description": "You are not authorized to access this resource"
    }
}
```

---

### Validation Error

**Request:**
```http
POST /api/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "description": "Description without title"
}
```

**Response (400):**
```json
{
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "Validation failed",
    "instance": "/api/tasks",
    "description": "title:Title is required and cannot be empty"
}
```

---

## Examples

### Complete Workflow Example

#### 1. Register a new user
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

#### 2. Login to get token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600000
}
```

#### 3. Create a task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "title": "Write documentation",
    "description": "Complete the API documentation",
    "status": "PENDING"
  }'
```

#### 4. Get all tasks
```bash
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 5. Update task status
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "status": "COMPLETED"
  }'
```

#### 6. Delete a task
```bash
curl -X DELETE http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## Notes

- JWT tokens expire after 1 hour (3600000 milliseconds)
- All timestamps are in ISO 8601 format
- All endpoints except `/auth/register` and `/auth/login` require authentication
- Users can only access their own tasks
- Status defaults to `PENDING` if not specified during task creation

---

## OpenAPI/Swagger Documentation

Interactive API documentation is available at:
```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON specification:
```
http://localhost:8080/api-docs
```