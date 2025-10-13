package com.taskmanager.store.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.taskmanager.store.dtos.ErrorResponse;
import com.taskmanager.store.dtos.TaskDto;
import com.taskmanager.store.entities.Status;
import com.taskmanager.store.entities.Task;
import com.taskmanager.store.entities.User;
import com.taskmanager.store.exceptions.TaskNotFoundException;
import com.taskmanager.store.exceptions.UnauthorizedTaskAccessException;
import com.taskmanager.store.mappers.TaskMapper;
import com.taskmanager.store.repositories.TaskRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Tasks", description = "Task management APIs")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    @Operation(
        summary = "Create a new task",
        description = "Creates a new task for the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Task created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "id": 1,
                        "title": "Complete documentation",
                        "description": "Write API documentation",
                        "status": "PENDING",
                        "userId": 1
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
                        "instance": "/api/tasks",
                        "description": "title: Title is required and cannot be empty"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Missing or invalid JWT token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Unauthorized",
                        "status": 401,
                        "detail": "Full authentication is required to access this resource",
                        "instance": "/api/tasks"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - Invalid JWT token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Forbidden",
                        "status": 403,
                        "detail": "JWT signature is invalid",
                        "instance": "/api/tasks",
                        "description": "The JWT signature is invalid"
                    }
                    """
                )
            )
        )
    })
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Task details to create",
            required = true,
            content = @Content(
                schema = @Schema(implementation = Task.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "title": "Complete documentation",
                        "description": "Write API documentation",
                        "status": "PENDING"
                    }
                    """
                )
            )
        )
        @Valid @RequestBody Task task,
        UriComponentsBuilder uriBuilder
    ) {
        User currentUser = getCurrentUser();
        log.info("Creating task for user: {} (ID: {})", currentUser.getEmail(), currentUser.getId());

        task.setUser(currentUser);
        Task savedTask = taskRepository.save(task);

        TaskDto response = taskMapper.toDto(savedTask);
        
        var uri = uriBuilder.path("/api/tasks/{id}").buildAndExpand(savedTask.getId()).toUri();

        log.info("Task created successfully with ID: {} for user: {}",
            savedTask.getId(), currentUser.getEmail());
        
        return ResponseEntity.created(uri).body(response);
    }

    @Operation(
        summary = "Get all tasks",
        description = "Retrieves all tasks for the authenticated user. Optionally filter by status."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Multiple tasks",
                        value = """
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
                        """
                    ),
                    @ExampleObject(
                        name = "Empty list",
                        value = "[]"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status parameter",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Invalid status: INVALID. Valid values are: PENDING, COMPLETED",
                        "instance": "/api/tasks",
                        "description": "Invalid parameter value provided"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Forbidden",
                        "status": 403,
                        "detail": "Full authentication is required to access this resource",
                        "instance": "/api/tasks"
                    }
                    """
                )
            )
        )
    })
    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(
        @Parameter(description = "Filter tasks by status (PENDING, COMPLETED)", example = "PENDING", required = false)
        @RequestParam(required = false, name = "status") String filterByStatus
    ) {
        User currentUser = getCurrentUser();
        log.info("Fetching all tasks for user: {} (ID: {})", currentUser.getEmail(), currentUser.getId());

        List<Task> tasks;

        if (filterByStatus != null && !filterByStatus.isEmpty()) {
            try {
                Status status = Status.valueOf(filterByStatus.toUpperCase());
                tasks = taskRepository.findByUserAndStatus(currentUser, status);
                log.info("Found {} tasks with status {} for user: {}",
                    tasks.size(), status, currentUser.getEmail());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status filter provided: {}", filterByStatus);
                throw new IllegalArgumentException("Invalid status: " + filterByStatus +
                    ". Valid values are: PENDING, COMPLETED");
            }
        } else {
            tasks = taskRepository.findByUser(currentUser);
            log.info("Found {} tasks for user: {}", tasks.size(), currentUser.getEmail());
        }

        List<TaskDto> responses = taskMapper.toDtoList(tasks);

        return ResponseEntity.ok(responses);
    }

    @Operation(
        summary = "Get task by ID",
        description = "Retrieves a specific task by its ID. User can only access their own tasks."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Task retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "id": 1,
                        "title": "Task title",
                        "description": "Task description",
                        "status": "PENDING",
                        "userId": 1
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Forbidden",
                        "status": 403,
                        "detail": "Full authentication is required to access this resource",
                        "instance": "/api/tasks/1"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - task belongs to another user",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Forbidden",
                        "status": 403,
                        "detail": "Unauthorized access to task with id: 33",
                        "instance": "/api/tasks/33",
                        "description": "You do not have permission to access this task"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Not Found",
                        "status": 404,
                        "detail": "Task not found with id: 1",
                        "instance": "/api/tasks/1",
                        "description": "The requested task does not exist"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@Parameter(description = "Task ID", example = "1") @PathVariable Long id) {
        User currentUser = getCurrentUser();
        log.info("Fetching task with ID: {} for user: {}", id, currentUser.getEmail());
    
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Task not found with ID: {}", id);
                return new TaskNotFoundException(id);
            });
        
        if (!task.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized access attempt to task ID: {} by user: {}", id, currentUser.getEmail());
            throw new UnauthorizedTaskAccessException(id);
        }

        log.info("Task retrieved successfully - ID: {}, Title: {}", task.getId(), task.getTitle());
        TaskDto response = taskMapper.toDto(task);

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update a task",
        description = "Updates an existing task. Only the provided fields will be updated (partial update supported)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Task updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "id": 1,
                        "title": "Updated title",
                        "description": "Updated description",
                        "status": "COMPLETED",
                        "userId": 1
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Forbidden",
                        "status": 403,
                        "detail": "Full authentication is required to access this resource",
                        "instance": "/api/tasks/1"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - task belongs to another user",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Forbidden",
                        "status": 403,
                        "detail": "Unauthorized access to task with id: 33",
                        "instance": "/api/tasks/33",
                        "description": "You do not have permission to access this task"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Not Found",
                        "status": 404,
                        "detail": "Task not found with id: 1",
                        "instance": "/api/tasks/1",
                        "description": "The requested task does not exist"
                    }
                    """
                )
            )
        )
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
        @Parameter(description = "Task ID", example = "1") @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Task fields to update (partial update supported)",
            required = true,
            content = @Content(
                schema = @Schema(implementation = TaskDto.class),
                examples = {
                    @ExampleObject(
                        name = "Update status only",
                        summary = "Partial update - status",
                        value = """
                        {
                            "status": "COMPLETED"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Update title and description",
                        summary = "Partial update - title and description",
                        value = """
                        {
                            "title": "Updated title",
                            "description": "Updated description"
                        }
                        """
                    ),
                    @ExampleObject(
                        name = "Full update",
                        summary = "Update all fields",
                        value = """
                        {
                            "title": "Updated title",
                            "description": "Updated description",
                            "status": "COMPLETED"
                        }
                        """
                    )
                }
            )
        )
        @RequestBody TaskDto taskDto
    ) {
        User currentUser = getCurrentUser();
        log.info("Updating task with ID: {} for user: {}", id, currentUser.getEmail());
        
        Task task = taskRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Task not found with ID: {} for update", id);
                        return new TaskNotFoundException(id);
                    });
        
        if (!task.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized update attempt to task ID: {} by user: {}", id, currentUser.getEmail());
            throw new UnauthorizedTaskAccessException(id);
        }

        taskMapper.updateTaskFromDto(taskDto, task);
        Task updatedTask = taskRepository.save(task);
        
        TaskDto response = taskMapper.toDto(updatedTask);

        log.info("Task updated successfully - ID: {}", task.getId());

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete a task",
        description = "Deletes a task by its ID. User can only delete their own tasks."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Task deleted successfully"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Forbidden",
                        "status": 403,
                        "detail": "Full authentication is required to access this resource",
                        "instance": "/api/tasks/1"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - task belongs to another user",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Forbidden",
                        "status": 403,
                        "detail": "Unauthorized access to task with id: 33",
                        "instance": "/api/tasks/33",
                        "description": "You do not have permission to access this task"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "type": "about:blank",
                        "title": "Not Found",
                        "status": 404,
                        "detail": "Task not found with id: 1",
                        "instance": "/api/tasks/1",
                        "description": "The requested task does not exist"
                    }
                    """
                )
            )
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "Task ID", example = "1") @PathVariable Long id) {
        User currentUser = getCurrentUser();
        log.info("Deleting task with ID: {} for user: {}", id, currentUser.getEmail());

        Task task = taskRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Task not found with ID: {} for deletion", id);
                        return new TaskNotFoundException(id);
                    });
        
        if (!task.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized deletion attempt to task ID: {} by user: {}", id, currentUser.getEmail());
            throw new UnauthorizedTaskAccessException(id);
        }

        taskRepository.delete(task);
        log.info("Task deleted successfully - ID: {}", id);
        
        return ResponseEntity.noContent().build();
    }
}
