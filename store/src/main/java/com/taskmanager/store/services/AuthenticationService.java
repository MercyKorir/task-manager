package com.taskmanager.store.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskmanager.store.dtos.LoginUserDto;
import com.taskmanager.store.dtos.RegisterUserDto;
import com.taskmanager.store.entities.User;
import com.taskmanager.store.exceptions.DuplicateUserException;
import com.taskmanager.store.exceptions.UserNotFoundException;
import com.taskmanager.store.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User signup(RegisterUserDto input) {
        log.info("Attempting to register new user with email: {}", input.getEmail());

        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
                log.warn("Registration attempt with existing email: {}", input.getEmail());
                throw new DuplicateUserException("email", input.getEmail());
        }

        if (userRepository.findByUsername(input.getUsername()).isPresent()) {
                log.warn("Registration attempt with existing username: {}", input.getUsername());
                throw new DuplicateUserException("username", input.getUsername());
        }

        User user = User.builder()
                .username(input.getUsername())
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .build();

        log.info("User registered successfully - Email: {}", user.getEmail());

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        log.debug("Attempting to authenticate user: {}", input.getEmail());
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> {
                        log.warn("User not found after successful authentication: {}", input.getEmail());
                        return new UserNotFoundException();
                });
    }
}
