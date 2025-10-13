package com.taskmanager.store.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Title is required and cannot be empty")
    private String title;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Description is required and cannot be empty")
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
