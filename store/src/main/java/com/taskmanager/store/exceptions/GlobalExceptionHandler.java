package com.taskmanager.store.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateUserException.class)
    public ProblemDetail handleDuplicateUserException(DuplicateUserException exception) {
        log.error("Duplicate User found exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(409),
            exception.getMessage()
        );
        errorDetail.setProperty("description", "A user with this email/username already exists");
        return errorDetail;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
        log.error("Bad credentials exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(401),
            exception.getMessage()
        );
        errorDetail.setProperty("description", "The username or password is incorrect");
        return errorDetail;
    }

    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleAccountStatusException(AccountStatusException exception) {
        log.error("Account status exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(403), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "The account is locked");
        return errorDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        log.error("Access denied exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(403), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "You are not authorized to access this resource");
        return errorDetail;
    }

    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleSignatureException(SignatureException exception) {
        log.error("JWT signature exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(403), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "The JWT signature is invalid");
        return errorDetail;
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredJwtException(ExpiredJwtException exception) {
        log.error("JWT expired exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(403), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "The JWT token has expired");
        return errorDetail;
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ProblemDetail handleMalformedJwtException(MalformedJwtException exception) {
        log.error("Malformed JWT exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(403), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "The JWT token is malformed or invalid");
        return errorDetail;
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ProblemDetail handleTaskNotFoundException(TaskNotFoundException exception) {
        log.error("Task not found exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(404), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "The requested task does not exist");
        return errorDetail;
    }

    @ExceptionHandler(UnauthorizedTaskAccessException.class)
    public ProblemDetail handleUnauthorizedTaskAccessException(UnauthorizedTaskAccessException exception) {
        log.warn("Unauthorized task access exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(403), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "You do not have permission to access this task");
        return errorDetail;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFoundException(UserNotFoundException exception) {
        log.error("User not found exception: {}", exception.getMessage());
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(404), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "The requested user does not exist");
        return errorDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("Illegal argument exception: {}", exception.getMessage());
        
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(400), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "Invalid parameter value provided");
        return errorDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException exception) {
        log.error("Validation exception: {}", exception.getMessage());

        String errors = exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .reduce((e1, e2) -> e1 + ", " + e2)
                        .orElse("Validation failed");
        
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(400),
            "Validation failed"
        );

        errorDetail.setProperty("description", errors);
        return errorDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception exception) {
        log.error("Unexpected exception occurred: {}", exception.getMessage(), exception);
        
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(500), 
            exception.getMessage()
        );
        errorDetail.setProperty("description", "Unknown internal server error.");
        return errorDetail;
    }
}
