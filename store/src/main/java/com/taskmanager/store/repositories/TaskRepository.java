package com.taskmanager.store.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.store.entities.Status;
import com.taskmanager.store.entities.Task;
import com.taskmanager.store.entities.User;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @EntityGraph(attributePaths = "user")
    List<Task> findByUser(User user);

    @EntityGraph(attributePaths = "user")
    List<Task> findByUserAndStatus(User user, Status status);
}
