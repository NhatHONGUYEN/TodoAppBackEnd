package com.example.ToDoApp.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * Structure simplifiée des réponses d'erreur de l'API
 */
public record ApiError(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime timestamp,
    int status,
    String message,
    String path
) {
    public ApiError(int status, String message, String path) {
        this(LocalDateTime.now(), status, message, path);
    }
} 