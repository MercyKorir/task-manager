package com.taskmanager.store.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tasks")
@Schema(description = "Task entity for creating tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Title is required and cannot be empty")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100")
    @Schema(description = "Task title", example = "Complete project", required = true)
    private String title;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Description is required and cannot be empty")
    @Size(min = 1,max = 500, message = "Description must be between 1 and 500 characters")
    @Schema(description = "Task description", example = "Finish all pending tasks", required = true)
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Schema(description = "Task status", example = "PENDING", allowableValues = {"PENDING", "COMPLETED"})
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(hidden = true)
    private User user;
}
