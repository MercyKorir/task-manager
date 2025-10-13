package com.taskmanager.store.dtos;

import com.taskmanager.store.entities.Status;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Task information")
public class TaskDto {
    @Schema(description = "Task ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title is required and cannot be empty")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    @Schema(description = "Task title", example = "Complete documentation", required = true, minLength = 1, maxLength = 100)
    private String title;

    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    @Schema(description = "Task description", example = "Write API documentation", required = true, minLength = 1, maxLength = 500)
    private String description;

    @Schema(description = "Task status", example = "PENDING", allowableValues = {"PENDING", "COMPLETED"})
    private Status status;

    @Schema(description = "User ID who owns this task", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;
}
