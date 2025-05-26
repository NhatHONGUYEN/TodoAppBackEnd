package com.example.ToDoApp.controller;

import com.example.ToDoApp.dto.CreateTaskRequest;
import com.example.ToDoApp.dto.UpdateTaskRequest;
import com.example.ToDoApp.entity.Task;
import com.example.ToDoApp.exception.TaskNotFoundException;
import com.example.ToDoApp.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;  // ← import nécessaire
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /** Accessible à tous les utilisateurs authentifiés avec le rôle USER ou ADMIN */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<Task>> list(
            @RequestParam(defaultValue = "all") String status,
            Principal principal
    ) {
        String userId = principal.getName();
        Boolean completed = null;
        if ("pending".equalsIgnoreCase(status))  completed = false;
        if ("done".equalsIgnoreCase(status))     completed = true;

        List<Task> tasks = taskService.list(userId, completed, Sort.by("dueDate").ascending());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Task> getById(
            @PathVariable Long id,
            Principal principal
    ) {
        List<Task> tasks = taskService.list(principal.getName(), null, Sort.by("id"));
        Task task = tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException(id));
        return ResponseEntity.ok(task);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Task> create(
            @Valid @RequestBody CreateTaskRequest request,
            Principal principal
    ) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setCompleted(request.getCompleted());
        task.setUserId(principal.getName());
        Task saved = taskService.create(task);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Task> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            Principal principal
    ) {
        List<Task> tasks = taskService.list(principal.getName(), null, Sort.by("id"));
        Task existingTask = tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException(id));

        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setDueDate(request.getDueDate());
        existingTask.setCompleted(request.getCompleted());

        Task updated = taskService.update(id, principal.getName(), existingTask);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/done")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Task> markDone(
            @PathVariable Long id,
            Principal principal
    ) {
        Task updated = taskService.markDone(id, principal.getName());
        return ResponseEntity.ok(updated);
    }

    /** Seuls les ADMINS peuvent supprimer n’importe quelle tâche */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Principal principal
    ) {
        taskService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
