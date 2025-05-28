package com.example.ToDoApp.controller;

import com.example.ToDoApp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.example.ToDoApp.controller.TaskControllerUtils.findTaskOrThrow;

@RestController
@RequestMapping("/api/tasks/delete")
@Tag(name = "Suppression de tâches", description = "API pour la suppression des tâches")
public class TaskDeleteController {

    private final TaskService taskService;

    public TaskDeleteController(TaskService taskService) {
        this.taskService = taskService;
    }

    /** Seuls les ADMINS peuvent supprimer n'importe quelle tâche */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Supprimer une tâche", description = "Supprime une tâche")
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
        // Vérifier d'abord que la tâche existe et appartient à l'utilisateur
        findTaskOrThrow(id, principal.getName(), taskService);
        taskService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
} 