package com.taskmanager.store.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
            content = @Content(schema = @Schema(implementation = UserDto.class))
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User with this email/username already exists"
        )
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterUserDto registerUserDto) {
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
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials"
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        log.info("Login attempt for email: {}", loginUserDto.getEmail());
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        
        String jwtToken = jwtService.generateToken(authenticatedUser);
        
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        log.info("User successfully authenticated: {}", loginUserDto.getEmail());
        
        return ResponseEntity.ok(loginResponse);
    }
}
