package com.example.ToDoApp.dto;

import com.example.ToDoApp.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

public record CreateTaskRequest(
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 1, max = 100, message = "Le titre doit faire entre 1 et 100 caractères")
    String title,
    
    @Size(max = 1024, message = "La description ne peut pas dépasser 1024 caractères")
    String description,
    
    @FutureOrPresent(message = "La date d'échéance doit être aujourd'hui ou dans le futur")
    LocalDate dueDate,
    
    @NotNull(message = "Le statut de complétion est obligatoire")
    Boolean completed
) {
    public CreateTaskRequest {
        if (completed == null) {
            completed = false;
        }
    }
    
    public CreateTaskRequest(String title, String description, LocalDate dueDate) {
        this(title, description, dueDate, false);
    }
    
    // Méthode de conversion vers entité
    public Task toEntity(String userId) {
        Task task = new Task();
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setDueDate(this.dueDate);
        task.setCompleted(this.completed);
        task.setUserId(userId);
        return task;
    }
}
