package com.example.ToDoApp.controller;

import com.example.ToDoApp.entity.Task;
import com.example.ToDoApp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.example.ToDoApp.controller.TaskControllerUtils.findTaskOrThrow;
import static com.example.ToDoApp.controller.TaskControllerUtils.getCompletedFilterValue;

@RestController
@RequestMapping("/api/tasks/read")
@Tag(name = "Lecture de tâches", description = "API pour la lecture des tâches")
public class TaskReadController {

    private final TaskService taskService;

    public TaskReadController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Récupérer toutes les tâches", description = "Récupère les tâches de l'utilisateur connecté avec filtrage optionnel par statut et pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tâches récupérées avec succès"),
        @ApiResponse(responseCode = "401", description = "Non autorisé", content = @Content)
    })
    public ResponseEntity<?> list(
            @Parameter(description = "Filtre par statut: all, pending, done") @RequestParam(defaultValue = "all") String status,
            @Parameter(description = "Numéro de page (commence à 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        String userId = principal.getName();
        Boolean completed = getCompletedFilterValue(status);
        
        // Créer l'objet Pageable avec tri par date d'échéance
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        
        // Appeler la méthode modifiée du service pour la pagination
        Page<Task> taskPage = taskService.listPaginated(userId, completed, pageable);
        
        return ResponseEntity.ok(taskPage);
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
        Task task = findTaskOrThrow(id, principal.getName(), taskService);
        return ResponseEntity.ok(task);
    }
} 