package com.example.ToDoApp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

/**
 * DTO pour la mise à jour d'une tâche.
 * Ne contient pas userId car celui-ci est extrait du contexte d'authentification.
 */
public class UpdateTaskRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 1, max = 100, message = "Le titre doit faire entre 1 et 100 caractères")
    private String title;

    @Size(max = 1024, message = "La description ne peut pas dépasser 1024 caractères")
    private String description;

    @FutureOrPresent(message = "La date d'échéance doit être aujourd'hui ou dans le futur")
    private LocalDate dueDate;

    @NotNull(message = "Le statut de complétion est obligatoire")
    private Boolean completed = false;

    private Long id;

    // Constructeurs
    public UpdateTaskRequest() {}

    // Getters et Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
