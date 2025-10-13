package com.taskmanager.store.exceptions;

public class UnauthorizedTaskAccessException extends RuntimeException {
    public UnauthorizedTaskAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedTaskAccessException(Long taskId) {
        super("Unauthorized access to task with id: " + taskId);
    }
}
