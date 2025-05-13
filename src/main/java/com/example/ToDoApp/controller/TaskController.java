package com.example.ToDoApp.controller;

import com.example.ToDoApp.dto.CreateTaskRequest;
import com.example.ToDoApp.dto.UpdateTaskRequest;
import com.example.ToDoApp.entity.Task;
import com.example.ToDoApp.service.TaskService;
import com.example.ToDoApp.exception.TaskNotFoundException;
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
     * GET /api/tasks/{id}
     * Récupère une tâche spécifique par son ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(
            @PathVariable Long id,
            Principal principal
    ) {
        // Récupérer toutes les tâches de l'utilisateur
        List<Task> tasks = taskService.list(principal.getName(), null, Sort.by("id"));
        
        // Chercher la tâche avec l'ID spécifié
        Task task = tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException(id));
        
        return ResponseEntity.ok(task);
    }

    /**
     * POST /api/tasks
     * Crée une nouvelle tâche pour l'utilisateur courant.
     */
    @PostMapping
    public ResponseEntity<Task> create(
            @Valid @RequestBody CreateTaskRequest request,
            Principal principal
    ) {
        // Conversion du DTO en entité Task
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setCompleted(request.getCompleted());
        
        // Affectation de l'utilisateur à partir du Principal
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
            @Valid @RequestBody UpdateTaskRequest request,
            Principal principal
    ) {
        // Récupérer la tâche existante pour conserver l'userId
        List<Task> tasks = taskService.list(principal.getName(), null, Sort.by("id"));
        Task existingTask = tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException(id));
        
        // Mettre à jour les champs avec les données du request
        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setDueDate(request.getDueDate());
        existingTask.setCompleted(request.getCompleted());
        // L'userId reste inchangé
        
        Task updated = taskService.update(id, principal.getName(), existingTask);
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
