package com.example.ToDoApp.controller;

import com.example.ToDoApp.entity.Task;
import com.example.ToDoApp.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

    /**
     * GET /api/tasks?status={all|pending|done}
     * Liste les tâches de l'utilisateur courant, optionnellement filtrées par statut.
     */
    @GetMapping
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

    /**
     * POST /api/tasks
     * Crée une nouvelle tâche pour l'utilisateur courant.
     */
    @PostMapping
    public ResponseEntity<Task> create(
            @Valid @RequestBody Task task,
            Principal principal
    ) {
        task.setUserId(principal.getName());
        Task saved = taskService.create(task);
        return ResponseEntity.ok(saved);
    }

    /**
     * PUT /api/tasks/{id}
     * Met à jour tous les champs d'une tâche (titre, description, dueDate, completed).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> update(
            @PathVariable Long id,
            @Valid @RequestBody Task task,
            Principal principal
    ) {
        Task updated = taskService.update(id, principal.getName(), task);
        return ResponseEntity.ok(updated);
    }

    /**
     * PUT /api/tasks/{id}/done
     * Marque une tâche comme terminée.
     */
    @PutMapping("/{id}/done")
    public ResponseEntity<Task> markDone(
            @PathVariable Long id,
            Principal principal
    ) {
        Task updated = taskService.markDone(id, principal.getName());
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/tasks/{id}
     * Supprime une tâche si elle appartient à l'utilisateur courant.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Principal principal
    ) {
        taskService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
