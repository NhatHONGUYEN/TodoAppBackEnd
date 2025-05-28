package com.example.ToDoApp.dto;

import com.example.ToDoApp.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

/**
 * DTO pour la mise à jour d'une tâche.
 * Ne contient pas userId car celui-ci est extrait du contexte d'authentification.
 */
public record UpdateTaskRequest(
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 1, max = 100, message = "Le titre doit faire entre 1 et 100 caractères")
    String title,
    
    @Size(max = 1024, message = "La description ne peut pas dépasser 1024 caractères")
    String description,
    
    @FutureOrPresent(message = "La date d'échéance doit être aujourd'hui ou dans le futur")
    LocalDate dueDate,
    
    @NotNull(message = "Le statut de complétion est obligatoire")
    Boolean completed,
    
    Long id
) {
    public UpdateTaskRequest {
        if (completed == null) {
            completed = false;
        }
    }
    
    // Méthode pour appliquer les modifications à une entité existante
    public void applyTo(Task task) {
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setDueDate(this.dueDate);
        task.setCompleted(this.completed);
    }
}