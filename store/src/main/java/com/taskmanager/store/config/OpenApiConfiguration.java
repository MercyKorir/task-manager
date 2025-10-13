package com.taskmanager.store.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Task Manager API",
        version = "1.0",
        description = """
            REST API for Task Management System with JWT Authentication.
            
            ## Features
            - User registration and authentication
            - JWT token-based security
            - CRUD operations for tasks
            - Task filtering by status
            - User-specific task isolation
            
            ## Authentication
            All endpoints except `/auth/register` and `/auth/login` require JWT authentication.
            After logging in, include the token in the Authorization header:
            `Authorization: Bearer <your-token>`
            
            ## Status Codes
            - 200: Success
            - 201: Created
            - 204: No Content
            - 400: Bad Request
            - 401: Unauthorized
            - 403: Forbidden
            - 404: Not Found
            - 409: Conflict
            - 500: Internal Server Error
            """,
        contact = @Contact(
            name = "Mercy Chelangat"
        )
    ),
    servers = {
        @Server(
            description = "Local Development Server",
            url = "http://localhost:8080"
        )
    },
    tags = {
        @Tag(name = "Authentication", description = "User registration and login endpoints"),
        @Tag(name = "Tasks", description = "Task management endpoints (requires authentication)")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Authentication. Format: Bearer <token>",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfiguration {

}
