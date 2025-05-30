package com.example.ToDoApp.exception;

/**
 * Énumération simplifiée des codes d'erreur essentiels
 */
public enum ErrorCode {
    TASK_NOT_FOUND("Tâche non trouvée"),
    ACCESS_DENIED("Accès refusé"),
    VALIDATION_ERROR("Erreur de validation"),
    SYSTEM_ERROR("Erreur système");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
} 