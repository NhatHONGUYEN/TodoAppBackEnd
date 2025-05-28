package com.example.ToDoApp.controller;

import com.example.ToDoApp.dto.UpdateTaskRequest;
import com.example.ToDoApp.entity.Task;
import com.example.ToDoApp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.example.ToDoApp.controller.TaskControllerUtils.findTaskOrThrow;

@RestController
@RequestMapping("/api/tasks/update")
@Tag(name = "Mise à jour de tâches", description = "API pour la mise à jour des tâches")
public class TaskUpdateController {

    private final TaskService taskService;

    public TaskUpdateController(TaskService taskService) {
        this.taskService = taskService;
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
        Task existingTask = findTaskOrThrow(id, principal.getName(), taskService);
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
        // Vérifier d'abord que la tâche existe et appartient à l'utilisateur
        findTaskOrThrow(id, principal.getName(), taskService);
        Task updated = taskService.markDone(id, principal.getName());
        return ResponseEntity.ok(updated);
    }
} 