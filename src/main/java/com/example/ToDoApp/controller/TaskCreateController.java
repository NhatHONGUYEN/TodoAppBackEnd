package com.example.ToDoApp.controller;

import com.example.ToDoApp.dto.CreateTaskRequest;
import com.example.ToDoApp.entity.Task;
import com.example.ToDoApp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/tasks/create")
@Tag(name = "Création de tâches", description = "API pour la création des tâches")
public class TaskCreateController {

    private final TaskService taskService;

    public TaskCreateController(TaskService taskService) {
        this.taskService = taskService;
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
        Task task = request.toEntity(principal.getName());
        Task saved = taskService.create(task);
        return ResponseEntity.ok(saved);
    }
} 