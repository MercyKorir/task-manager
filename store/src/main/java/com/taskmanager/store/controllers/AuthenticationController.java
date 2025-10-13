package com.taskmanager.store.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.store.dtos.ErrorResponse;
import com.taskmanager.store.dtos.LoginResponse;
import com.taskmanager.store.dtos.LoginUserDto;
import com.taskmanager.store.dtos.RegisterUserDto;
import com.taskmanager.store.dtos.UserDto;
import com.taskmanager.store.entities.User;
import com.taskmanager.store.mappers.UserMapper;
import com.taskmanager.store.services.AuthenticationService;
import com.taskmanager.store.services.JwtService;

import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "Authentication management APIs")
@Slf4j
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    public AuthenticationController(
        JwtService jwtService,
        AuthenticationService authenticationService,
        UserMapper userMapper
    ) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with username, email and password"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "id": 1,
                        "username": "johndoe",
                        "email": "john@example.com",
                        "createdAt": "2025-10-11T10:30:00",
                        "updatedAt": "2025-10-11T10:30:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User with this email already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Conflict",
                        "status": 409,
                        "detail": "User with email 'john@example.com' already exists",
                        "instance": "/auth/register"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Validation failed",
                        "instance": "/auth/register",
                        "description": "username: Username is required and cannot be empty"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration details",
            required = true,
            content = @Content(
                schema = @Schema(implementation = RegisterUserDto.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "username": "johndoe",
                        "email": "john@example.com",
                        "password": "password123"
                    }
                    """
                )
            )
        )
        @Valid @RequestBody RegisterUserDto registerUserDto
    ) {
        log.info("Registration attempt for email: {}", registerUserDto.getEmail());
        User registeredUser = authenticationService.signup(registerUserDto);
        log.info("User successfully registered with ID: {} and email: {}", 
                    registeredUser.getId(), registeredUser.getEmail());
        
        UserDto response = userMapper.toUserDto(registeredUser);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Login user",
        description = "Authenticates user credentials and returns JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "expiresIn": 3600000
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Unauthorized",
                        "status": 401,
                        "detail": "Bad credentials",
                        "instance": "/auth/login"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login credentials",
            required = true,
            content = @Content(
                schema = @Schema(implementation = LoginUserDto.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "email": "john@example.com",
                        "password": "password123"
                    }
                    """
                )
            )
        )
        @Valid @RequestBody LoginUserDto loginUserDto
    ) {
        log.info("Login attempt for email: {}", loginUserDto.getEmail());
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        
        String jwtToken = jwtService.generateToken(authenticatedUser);
        
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        log.info("User successfully authenticated: {}", loginUserDto.getEmail());
        
        return ResponseEntity.ok(loginResponse);
    }
}
