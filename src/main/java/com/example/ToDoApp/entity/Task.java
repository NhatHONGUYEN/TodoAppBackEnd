package com.example.ToDoApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Le titre est obligatoire")
  @Size(min = 1, max = 100, message = "Le titre doit faire entre 1 et 100 caractères")
  @Column(nullable = false)
  private String title;

  @Size(max = 1024, message = "La description ne peut pas dépasser 1024 caractères")
  @Column(length = 1024)
  private String description;

  @FutureOrPresent(message = "La date d'échéance doit être aujourd'hui ou dans le futur")
  private LocalDate dueDate;

  @NotNull(message = "Le statut de complétion est obligatoire")
  @Column(nullable = false)
  private Boolean completed = false;

  @NotBlank(message = "L'ID utilisateur est obligatoire")
  @Column(nullable = false)
  private String userId;

  public Task() {}


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
  
}
