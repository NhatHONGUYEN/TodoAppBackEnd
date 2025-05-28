package com.example.ToDoApp.controller;

import com.example.ToDoApp.dto.CreateTaskRequest;
import com.example.ToDoApp.dto.UpdateTaskRequest;
import com.example.ToDoApp.entity.Task;
import com.example.ToDoApp.exception.TaskNotFoundException;
import com.example.ToDoApp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "API de gestion des tâches")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /** Accessible à tous les utilisateurs authentifiés avec le rôle USER ou ADMIN */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Récupérer toutes les tâches", description = "Récupère les tâches de l'utilisateur connecté avec filtrage optionnel par statut")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tâches récupérées avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<List<Task>> list(
            @Parameter(description = "Filtre par statut: all, pending, done") @RequestParam(defaultValue = "all") String status,
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
    @Operation(summary = "Récupérer une tâche par ID", description = "Récupère une tâche spécifique par son ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tâche trouvée"),
        @ApiResponse(responseCode = "404", description = "Tâche non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<Task> getById(
            @Parameter(description = "ID de la tâche") @PathVariable Long id,
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
    @Operation(summary = "Créer une nouvelle tâche", description = "Crée une nouvelle tâche pour l'utilisateur connecté")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tâche créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<Task> create(
            @Valid @RequestBody CreateTaskRequest request,
            Principal principal
    ) {
        // Utilisation de la méthode de conversion du record
        Task task = request.toEntity(principal.getName());
        Task saved = taskService.create(task);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Mettre à jour une tâche", description = "Met à jour une tâche existante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tâche mise à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Tâche non trouvée"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<Task> update(
            @Parameter(description = "ID de la tâche") @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            Principal principal
    ) {
        List<Task> tasks = taskService.list(principal.getName(), null, Sort.by("id"));
        Task existingTask = tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException(id));

        // Utilisation de la méthode applyTo du record
        request.applyTo(existingTask);

        Task updated = taskService.update(id, principal.getName(), existingTask);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/done")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Marquer une tâche comme terminée", description = "Change le statut d'une tâche à 'terminée'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tâche marquée comme terminée"),
        @ApiResponse(responseCode = "404", description = "Tâche non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<Task> markDone(
            @Parameter(description = "ID de la tâche") @PathVariable Long id,
            Principal principal
    ) {
        Task updated = taskService.markDone(id, principal.getName());
        return ResponseEntity.ok(updated);
    }

    /** Seuls les ADMINS peuvent supprimer n'importe quelle tâche */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Supprimer une tâche", description = "Supprime une tâche (admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tâche supprimée avec succès"),
        @ApiResponse(responseCode = "404", description = "Tâche non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content),
        @ApiResponse(responseCode = "403", description = "Accès refusé", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la tâche") @PathVariable Long id,
            Principal principal
    ) {
        taskService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}